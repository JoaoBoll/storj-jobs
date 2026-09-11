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
  createdAt: string | null;
  updatedAt: string | null;
}

interface NodeCard {
  name: string;
  id: string;
  url: string;
  status: string;
  storage: string;
  availableDiskSpace: number | null;
  accent: string;
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
  public runningJob = '';
  public toastMessage = '';
  public chartOptions: ChartOptions;

  public nodes: NodeCard[] = [];

  public readonly jobs = [
    { label: 'SNO second', endpoint: 'snoSeconds', cadence: 'Every 5 seconds', state: 'Running', lastRun: '12 sec ago', tone: 'green' },
    { label: 'SNO minute', endpoint: 'snoMinute', cadence: 'Every minute', state: 'Running', lastRun: '42 sec ago', tone: 'green' },
    { label: 'SNO hour', endpoint: 'snoHour', cadence: 'Hourly', state: 'Queued', lastRun: '18 min ago', tone: 'blue' },
    { label: 'SNO day', endpoint: 'snoDay', cadence: 'Daily', state: 'Queued', lastRun: 'Today, 00:00', tone: 'blue' },
    { label: 'SNO week', endpoint: 'snoWeek', cadence: 'Weekly', state: 'Queued', lastRun: 'Sun, 00:00', tone: 'blue' },
    { label: 'SNO month', endpoint: 'snoMonth', cadence: 'Monthly', state: 'Queued', lastRun: '01 Sep, 00:00', tone: 'blue' }
  ];

  constructor(private readonly http: HttpClient) {
    this.chartOptions = {
      series: [
        { name: "Used bandwidth", data: [24, 31, 28, 44, 39, 52, 48, 62, 58, 71, 68, 79, 74, 84, 81, 91] },
        { name: "Storage usage", data: [42, 43, 45, 46, 48, 48, 49, 51, 52, 53, 54, 56, 58, 59, 60, 61] }
      ],
      chart: { height: 310, type: "area", toolbar: { show: false }, background: 'transparent' },
      dataLabels: { enabled: false },
      colors: ['#c7f36b', '#5bd6e8'],
      stroke: { curve: "smooth", width: 2 },
      xaxis: {
        categories: ['00:00', '02:00', '04:00', '06:00', '08:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00', '22:00', '00:00', '02:00', '04:00', '06:00'],
        labels: { style: { colors: '#6f7b83' } },
        axisBorder: { show: false },
        axisTicks: { show: false }
      },
      yaxis: { labels: { style: { colors: '#6f7b83' } } },
      grid: { borderColor: '#26323a', strokeDashArray: 4 },
      legend: { show: false },
      tooltip: { theme: 'dark' }
    }

    this.loadNodes();
  }

  public loadNodes(): void {
    this.http.get<NodeResponse[]>('/api/job/nodes').subscribe({
      next: (nodes) => {
        this.nodes = nodes.map((node, index) => ({
          name: `Node ${(node.nodeId || node.id).slice(0, 8)}`,
          id: node.nodeId || node.id,
          url: node.url,
          status: node.enabled ? 'Online' : 'Disabled',
          storage: this.formatBytes(node.availableDiskSpace),
          availableDiskSpace: node.availableDiskSpace,
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

  public selectView(view: string): void {
    this.activeView = view;
  }

  public now(): Date {
    return new Date();
  }

  public runJob(endpoint: string, label: string): void {
    this.runningJob = endpoint;
    this.toastMessage = '';
    this.http.get(`/api/job/${endpoint}`, { responseType: 'text' }).subscribe({
      next: () => {
        this.runningJob = '';
        this.lastSync = new Date();
        this.toastMessage = `${label} disparado com sucesso`;
      },
      error: () => {
        this.runningJob = '';
        this.toastMessage = `Não foi possível executar ${label}`;
      }
    });
  }
}
