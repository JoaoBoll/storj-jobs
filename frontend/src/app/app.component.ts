import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { SplineAreaChartComponent } from './shared/charts/spline-area-chart/spline-area-chart.component';
import { ChartOptions } from './models/chart-options.model';

type Unit = 'MB' | 'GB' | 'TB';
type Interval = '5m' | '15m' | '30m' | '1h' | '1d' | '1w' | '1mo';

interface NodeResponse {
  id: string;
  nodeId: string;
  url: string;
  enabled: boolean;
  availableDiskSpace: number | null;
  usedDiskSpace: number | null;
  totalDiskSpace: number | null;
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

interface OverviewPoint {
  label: string;
  storageUsed: number | null;
  storagePercentOfFirst: number;
  trashUsed: number | null;
  trashPercentOfFirst: number;
  ingressTotal: number | null;
  egressTotal: number | null;
  uptimePercent: number;
}

interface OverviewResponse {
  interval: Interval;
  points: number;
  data: OverviewPoint[];
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  imports: [CommonModule, SplineAreaChartComponent]
})
export class AppComponent {
  public activeView = 'Overview';
  public lastSync = new Date();
  public toastMessage = '';
  public storageUnit: Unit = 'MB';
  public trashUnit: Unit = 'MB';
  public bandwidthUnit: Unit = 'MB';
  public storageInterval: Interval = '5m';
  public trashInterval: Interval = '5m';
  public bandwidthInterval: Interval = '5m';
  public uptimeInterval: Interval = '5m';
  public overviewData: { [key in Interval]: OverviewResponse } = {
    '5m': { interval: '5m', points: 30, data: [] },
    '15m': { interval: '15m', points: 30, data: [] },
    '30m': { interval: '30m', points: 30, data: [] },
    '1h': { interval: '1h', points: 30, data: [] },
    '1d': { interval: '1d', points: 30, data: [] },
    '1w': { interval: '1w', points: 30, data: [] },
    '1mo': { interval: '1mo', points: 30, data: [] }
  };
  public storageChart = this.createChart('#c7f36b', 'Storage used');
  public trashChart = this.createChart('#f4bb61', 'Trash');
  public bandwidthChart = this.createChart('#5bd6e8', 'Bandwidth');
  public uptimeChart = this.createChart('#83a9ff', 'Uptime %');
  public nodes: NodeCard[] = [];

  public readonly jobs = [
    { label: 'SNO 5m', cadence: 'Every 5 minutes', state: 'Scheduled' },
    { label: 'SNO 15m', cadence: 'Every 15 minutes', state: 'Scheduled' },
    { label: 'SNO 30m', cadence: 'Every 30 minutes', state: 'Scheduled' },
    { label: 'SNO hour', cadence: 'Hourly', state: 'Scheduled' },
    { label: 'SNO day', cadence: 'Daily', state: 'Scheduled' },
    { label: 'SNO week', cadence: 'Weekly', state: 'Scheduled' },
    { label: 'SNO month', cadence: 'Monthly', state: 'Scheduled' }
  ];

  constructor(private readonly http: HttpClient) {
    this.loadNodes();
    this.loadOverview();
  }

  public loadOverview(): void {
    const intervals: Interval[] = ['5m', '15m', '30m', '1h', '1d', '1w', '1mo'];
    intervals.forEach(interval => {
      this.http.get<OverviewResponse>(`/api/job/overview?interval=${interval}`).subscribe({
        next: (overview) => {
          this.overviewData[interval] = overview;
          this.updateOverviewCharts();
          this.lastSync = new Date();
        },
        error: () => this.toastMessage = 'Failed to load overview'
      });
    });
  }

  public selectChartInterval(chart: 'storage' | 'trash' | 'bandwidth' | 'uptime', interval: Interval): void {
    if (chart === 'storage') this.storageInterval = interval;
    if (chart === 'trash') this.trashInterval = interval;
    if (chart === 'bandwidth') this.bandwidthInterval = interval;
    if (chart === 'uptime') this.uptimeInterval = interval;
    this.updateOverviewCharts();
  }

  public selectUnit(chart: 'storage' | 'trash' | 'bandwidth', unit: Unit): void {
    if (chart === 'storage') this.storageUnit = unit;
    else if (chart === 'trash') this.trashUnit = unit;
    else if (chart === 'bandwidth') this.bandwidthUnit = unit;
    this.updateOverviewCharts();
  }

  public updateOverviewCharts(): void {
    const storageData = this.overviewData[this.storageInterval];
    const trashData = this.overviewData[this.trashInterval];
    const bandwidthData = this.overviewData[this.bandwidthInterval];
    const uptimeData = this.overviewData[this.uptimeInterval];

    const storageLabels = storageData.data.map(point => this.formatLabel(point.label));
    const trashLabels = trashData.data.map(point => this.formatLabel(point.label));
    const bandwidthLabels = bandwidthData.data.map(point => this.formatLabel(point.label));
    const uptimeLabels = uptimeData.data.map(point => this.formatLabel(point.label));

    this.storageChart = { ...this.storageChart, series: [
      { name: `Storage used (${this.storageUnit})`, data: storageData.data.map(point => this.toUnit(point.storageUsed, this.storageUnit)) },
      { name: '% of first', data: storageData.data.map(point => point.storagePercentOfFirst) }
    ], xaxis: { ...this.storageChart.xaxis, categories: storageLabels } };
    this.trashChart = { ...this.trashChart, series: [
      { name: `Trash (${this.trashUnit})`, data: trashData.data.map(point => this.toUnit(point.trashUsed, this.trashUnit)) },
      { name: '% of first', data: trashData.data.map(point => point.trashPercentOfFirst) }
    ], xaxis: { ...this.trashChart.xaxis, categories: trashLabels } };
    this.bandwidthChart = {
      ...this.bandwidthChart,
      series: [
        { name: 'Ingress', data: bandwidthData.data.map(point => this.toUnit(point.ingressTotal, this.bandwidthUnit)) },
        { name: 'Egress', data: bandwidthData.data.map(point => this.toUnit(point.egressTotal, this.bandwidthUnit)) }
      ],
      xaxis: { ...this.bandwidthChart.xaxis, categories: bandwidthLabels }
    };
    this.uptimeChart = this.withData(this.uptimeChart, 'Uptime %', uptimeData.data.map(point => point.uptimePercent), uptimeLabels);
  }

  public createChart(color: string, name: string): ChartOptions {
    return {
      series: [{ name, data: [] }],
      chart: { height: 260, type: 'line', toolbar: { show: false }, background: 'transparent' },
      dataLabels: { enabled: false }, colors: [color], stroke: { curve: 'smooth', width: 2 },
      xaxis: { categories: [], labels: { style: { colors: '#6f7b83' } }, axisBorder: { show: false }, axisTicks: { show: false } },
      yaxis: { labels: { style: { colors: '#6f7b83' } } }, grid: { borderColor: '#26323a', strokeDashArray: 4 }, legend: { show: true, labels: { colors: '#849197' } }, tooltip: { theme: 'dark' }
    };
  }

  public withData(chart: ChartOptions, name: string, data: number[], labels: string[]): ChartOptions {
    return { ...chart, series: [{ name, data }], xaxis: { ...chart.xaxis, categories: labels } };
  }

  public formatLabel(label: string): string {
    const date = new Date(label);
    return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
  }

  public toUnit(bytes: number | null, unit: Unit): number {
    if (bytes === null || bytes === undefined) return 0;
    const divisor = { MB: 1024 ** 2, GB: 1024 ** 3, TB: 1024 ** 4 }[unit];
    return Number((bytes / divisor).toFixed(2));
  }

  public formatValue(bytes: number | null): string {
    return bytes === null || bytes === undefined ? 'Not available' : `${this.toUnit(bytes, 'MB')} MB`;
  }

  public diskUsage(node: NodeCard): number {
    if (!node.usedDiskSpace || !node.totalDiskSpace) return 0;
    return Math.min(100, Math.round((node.usedDiskSpace / node.totalDiskSpace) * 100));
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
      error: () => this.toastMessage = 'Failed to load registered nodes'
    });
  }

  public get onlineNodes(): number {
    return this.nodes.filter(node => node.status === 'Online').length;
  }

  public selectView(view: string): void {
    this.activeView = view;
  }
}
