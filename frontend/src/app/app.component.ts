import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { SplineAreaChartComponent } from "./shared/charts/spline-area-chart/spline-area-chart.component";
import { ChartOptions } from './models/chart-options.model';

interface NodeResponse {
  id: string;
  nodeId: string;
  url: string;
  enabled: boolean;
  availableDiskSpace: number | null;
  usedDiskSpace: number | null;
  totalDiskSpace: number | null;
  createdAt: string | null;
  updatedAt: string | null;
}

interface NodeCard {
  name: string;
  id: string;
  url: string;
  status: string;
  availableDiskSpace: number | null;
  usedDiskSpace: number | null;
  totalDiskSpace: number | null;
  accent: string;
}

interface OverviewResponse {
  nodes: OverviewNode[];
}

interface OverviewNode {
  nodeId: string;
  storageUsed: number | null;
  storageFirstInterval: number | null;
  trashUsed: number | null;
  trashFirstInterval: number | null;
  ingressTotal: number | null;
  egressTotal: number | null;
  uptimePercent: number | null;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  imports: [CommonModule, SplineAreaChartComponent]
})
export class AppComponent {

  public activeView = 'Nodes';
  public lastSync = new Date();
  public toastMessage = '';
  public displayUnit: 'MB' | 'GB' | 'TB' = 'GB';
  public overview: OverviewResponse = { nodes: [] };
  public storageChart!: ChartOptions;
  public trashChart!: ChartOptions;
  public bandwidthChart!: ChartOptions;
  public uptimeChart!: ChartOptions;

  public nodes: NodeCard[] = [];

  public readonly jobs = [
    { label: 'SNO second', cadence: 'Every 5 seconds', state: 'Scheduled' },
    { label: 'SNO minute', cadence: 'Every minute', state: 'Scheduled' },
    { label: 'SNO hour', cadence: 'Hourly', state: 'Scheduled' },
    { label: 'SNO day', cadence: 'Daily', state: 'Scheduled' },
    { label: 'SNO week', cadence: 'Weekly', state: 'Scheduled' },
    { label: 'SNO month', cadence: 'Monthly', state: 'Scheduled' }
  ];

  constructor(private readonly http: HttpClient) {
    this.storageChart = this.createChart('#c7f36b', 'Storage used');
    this.trashChart = this.createChart('#f4bb61', 'Trash');
    this.bandwidthChart = this.createChart('#5bd6e8', 'Bandwidth');
    this.uptimeChart = this.createChart('#83a9ff', 'Uptime %');

    this.loadNodes();
    this.loadOverview();
  }

  public loadOverview(): void {
    this.http.get<OverviewResponse>('/api/job/overview').subscribe({
      next: (overview) => {
        this.overview = overview;
        this.updateOverviewCharts();
      },
      error: () => {
        this.toastMessage = 'Não foi possível carregar o overview';
      }
    });
  }

  public createChart(color: string, name: string): ChartOptions {
    return {
      series: [{ name, data: [] }],
      chart: { height: 260, type: 'bar', toolbar: { show: false }, background: 'transparent' },
      dataLabels: { enabled: false }, colors: [color], stroke: { width: 2 },
      xaxis: { categories: [], labels: { style: { colors: '#6f7b83' } }, axisBorder: { show: false }, axisTicks: { show: false } },
      yaxis: { labels: { style: { colors: '#6f7b83' } } }, grid: { borderColor: '#26323a', strokeDashArray: 4 }, legend: { show: false }, tooltip: { theme: 'dark' }
    };
  }

  public updateOverviewCharts(): void {
    const categories = this.overview.nodes.map(node => node.nodeId.slice(0, 8));
    this.storageChart = { ...this.storageChart, series: [{ name: 'Storage used', data: this.overview.nodes.map(node => this.toUnit(node.storageUsed)) }], xaxis: { ...this.storageChart.xaxis, categories } };
    this.trashChart = { ...this.trashChart, series: [{ name: 'Trash', data: this.overview.nodes.map(node => this.toUnit(node.trashUsed)) }], xaxis: { ...this.trashChart.xaxis, categories } };
    this.bandwidthChart = { ...this.bandwidthChart, series: [{ name: 'Ingress', data: this.overview.nodes.map(node => this.toUnit(node.ingressTotal)) }, { name: 'Egress', data: this.overview.nodes.map(node => this.toUnit(node.egressTotal)) }], xaxis: { ...this.bandwidthChart.xaxis, categories } };
    this.uptimeChart = { ...this.uptimeChart, series: [{ name: 'Uptime %', data: this.overview.nodes.map(node => node.uptimePercent ?? 0) }], xaxis: { ...this.uptimeChart.xaxis, categories } };
  }

  public averageUptime(): number {
    if (!this.overview.nodes.length) return 0;
    return this.overview.nodes.reduce((total, node) => total + (node.uptimePercent ?? 0), 0) / this.overview.nodes.length;
  }

  public percentOfFirst(current: number | null, first: number | null): number {
    if (current === null || first === null || first === 0) return 0;
    return (current / Math.abs(first)) * 100;
  }

  public averageStorageChange(): number {
    if (!this.overview.nodes.length) return 0;
    return this.overview.nodes.reduce((total, node) => total + this.percentOfFirst(node.storageUsed, node.storageFirstInterval), 0) / this.overview.nodes.length;
  }

  public averageTrashChange(): number {
    if (!this.overview.nodes.length) return 0;
    return this.overview.nodes.reduce((total, node) => total + this.percentOfFirst(node.trashUsed, node.trashFirstInterval), 0) / this.overview.nodes.length;
  }

  public toUnit(bytes: number | null): number {
    if (bytes === null || bytes === undefined) {
      return 0;
    }
    const divisors = { MB: 1024 ** 2, GB: 1024 ** 3, TB: 1024 ** 4 };
    return Number((bytes / divisors[this.displayUnit]).toFixed(2));
  }

  public selectUnit(unit: 'MB' | 'GB' | 'TB'): void {
    this.displayUnit = unit;
    this.updateOverviewCharts();
  }

  public loadNodes(): void {
    this.http.get<NodeResponse[]>('/api/job/nodes').subscribe({
      next: (nodes) => {
        this.nodes = nodes.map((node, index) => ({
          name: `Node ${(node.nodeId || node.id).slice(0, 8)}`,
          id: node.nodeId || node.id,
          url: node.url,
          status: node.enabled ? 'Online' : 'Disabled',
          availableDiskSpace: node.availableDiskSpace,
          usedDiskSpace: node.usedDiskSpace,
          totalDiskSpace: node.totalDiskSpace,
          accent: ['lime', 'cyan', 'amber'][index % 3]
        }));
        this.lastSync = new Date();
      },
      error: () => {
        this.toastMessage = 'Não foi possível carregar os nodes registrados';
      }
    });
  }

  public get onlineNodes(): number {
    return this.nodes.filter(node => node.status === 'Online').length;
  }

  public formatBytes(bytes: number | null): string {
    if (bytes === null || bytes === undefined) {
      return 'Não informado';
    }
    if (bytes === 0) {
      return '0 B';
    }
    const units = ['B', 'KB', 'MB', 'GB', 'TB', 'PB'];
    const unitIndex = Math.floor(Math.log(bytes) / Math.log(1024));
    return `${(bytes / Math.pow(1024, unitIndex)).toFixed(1)} ${units[unitIndex]}`;
  }

  public formatDelta(bytes: number): string {
    const sign = bytes > 0 ? '+' : '';
    return `${sign}${this.formatValue(bytes)}`;
  }

  public formatValue(bytes: number | null): string {
    if (bytes === null || bytes === undefined) {
      return 'Não informado';
    }
    return `${this.toUnit(bytes)} ${this.displayUnit}`;
  }

  public diskUsage(node: NodeCard): number {
    if (!node.usedDiskSpace || !node.totalDiskSpace) {
      return 0;
    }
    return Math.min(100, Math.round((node.usedDiskSpace / node.totalDiskSpace) * 100));
  }

  public selectView(view: string): void {
    this.activeView = view;
  }

}
