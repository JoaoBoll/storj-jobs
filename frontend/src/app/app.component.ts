import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { SplineAreaChartComponent } from './shared/charts/spline-area-chart/spline-area-chart.component';
import { ChartOptions } from './models/chart-options.model';

type Unit = 'Auto' | 'KB' | 'MB' | 'GB';
type ResolvedUnit = 'KB' | 'MB' | 'GB';
type Interval = '5s' | '15s' | '30s' | '5m' | '15m' | '30m' | '1h' | '1d' | '1w' | '1mo';

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

const RANGE_OPTIONS = [10, 20, 30, 60, 90] as const;
type Range = typeof RANGE_OPTIONS[number];
type ChartKey = 'storage' | 'trash' | 'bandwidth' | 'uptime';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  imports: [CommonModule, SplineAreaChartComponent]
})
export class AppComponent implements OnDestroy {
  public activeView = 'Overview';
  public lastSync = new Date();
  public toastMessage = '';
  public storageUnit: Unit = 'Auto';
  public trashUnit: Unit = 'Auto';
  public bandwidthUnit: Unit = 'Auto';
  public readonly rangeOptions = RANGE_OPTIONS;
  public globalInterval: Interval = '5m';
  public get storageInterval(): Interval { return this.globalInterval; }
  public get trashInterval(): Interval { return this.globalInterval; }
  public get bandwidthInterval(): Interval { return this.globalInterval; }
  public uptimeInterval: Interval = '30m';
  public storageRange: Range = 30;
  public trashRange: Range = 30;
  public bandwidthRange: Range = 30;
  public uptimeRange: Range = 30;
  private readonly overviewData = new Map<string, OverviewResponse>();
  public storageSummary = '';
  public storageDeltaText = '';
  public trashSummary = '';
  public trashDeltaText = '';
  public bandwidthSummary = '';
  public bandwidthIngressTotal = '';
  public bandwidthEgressTotal = '';
  public bandwidthIngressDeltaText = '';
  public bandwidthEgressDeltaText = '';
  public uptimeSummary = '';
  public uptimeDeltaText = '';
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

  private readonly chartKeys: readonly ChartKey[] = ['storage', 'trash', 'bandwidth', 'uptime'];
  private sharedTimer?: ReturnType<typeof setInterval>;
  private uptimeTimer?: ReturnType<typeof setInterval>;

  constructor(private readonly http: HttpClient) {
    this.loadNodes();
    this.loadOverview();
    this.scheduleSharedAutoRefresh();
    this.scheduleUptimeAutoRefresh();
  }

  ngOnDestroy(): void {
    if (this.sharedTimer) clearInterval(this.sharedTimer);
    if (this.uptimeTimer) clearInterval(this.uptimeTimer);
  }

  private refreshMsFor(interval: Interval): number {
    const cadence: Record<Interval, number> = {
      '5s': 5_000, '15s': 15_000, '30s': 30_000,
      '5m': 300_000, '15m': 900_000, '30m': 1_800_000,
      '1h': 3_600_000, '1d': 60_000, '1w': 60_000, '1mo': 60_000
    };
    return cadence[interval];
  }

  public selectGlobalInterval(interval: Interval): void {
    this.globalInterval = interval;
    (['storage', 'trash', 'bandwidth'] as const).forEach(chart => this.fetchOverview(chart, true));
    this.scheduleSharedAutoRefresh();
  }

  private scheduleSharedAutoRefresh(): void {
    if (this.sharedTimer) clearInterval(this.sharedTimer);
    const ms = this.refreshMsFor(this.globalInterval);
    this.sharedTimer = setInterval(() => {
      (['storage', 'trash', 'bandwidth'] as const).forEach(chart => this.fetchOverview(chart, true));
    }, ms);
  }

  private scheduleUptimeAutoRefresh(): void {
    if (this.uptimeTimer) clearInterval(this.uptimeTimer);
    const ms = this.refreshMsFor(this.uptimeInterval);
    this.uptimeTimer = setInterval(() => this.fetchOverview('uptime', true), ms);
  }

  public loadOverview(force = false): void {
    this.chartKeys.forEach(chart => this.fetchOverview(chart, force));
  }

  private overviewKey(interval: Interval, range: Range): string {
    return `${interval}:${range}`;
  }

  private fetchOverview(chart: ChartKey, force = false): void {
    const interval = this.intervalFor(chart);
    const range = this.rangeFor(chart);
    const key = this.overviewKey(interval, range);
    if (this.overviewData.has(key) && !force) {
      this.updateChart(chart);
      return;
    }
    this.http.get<OverviewResponse>(`/api/job/overview?interval=${interval}&points=${range}`).subscribe({
      next: (overview) => {
        this.overviewData.set(key, overview);
        this.updateChart(chart);
        this.lastSync = new Date();
      },
      error: () => this.toastMessage = 'Failed to load overview'
    });
  }

  public selectUptimeInterval(interval: Interval): void {
    this.uptimeInterval = interval;
    this.fetchOverview('uptime', true);
    this.scheduleUptimeAutoRefresh();
  }

  public selectChartRange(chart: ChartKey, range: Range): void {
    if (chart === 'storage') this.storageRange = range;
    if (chart === 'trash') this.trashRange = range;
    if (chart === 'bandwidth') this.bandwidthRange = range;
    if (chart === 'uptime') this.uptimeRange = range;
    this.fetchOverview(chart);
  }

  private intervalFor(chart: ChartKey): Interval {
    if (chart === 'storage') return this.storageInterval;
    if (chart === 'trash') return this.trashInterval;
    if (chart === 'bandwidth') return this.bandwidthInterval;
    return this.uptimeInterval;
  }

  private rangeFor(chart: ChartKey): Range {
    if (chart === 'storage') return this.storageRange;
    if (chart === 'trash') return this.trashRange;
    if (chart === 'bandwidth') return this.bandwidthRange;
    return this.uptimeRange;
  }

  public selectUnit(chart: 'storage' | 'trash' | 'bandwidth', unit: Unit): void {
    if (chart === 'storage') this.storageUnit = unit;
    else if (chart === 'trash') this.trashUnit = unit;
    else if (chart === 'bandwidth') this.bandwidthUnit = unit;
    this.updateChart(chart);
  }

  private overviewFor(interval: Interval, range: Range): OverviewResponse {
    return this.overviewData.get(this.overviewKey(interval, range)) ?? { interval, points: range, data: [] };
  }

  private updateChart(chart: ChartKey): void {
    if (chart === 'storage') this.updateStorageChart();
    else if (chart === 'trash') this.updateTrashChart();
    else if (chart === 'bandwidth') this.updateBandwidthChart();
    else this.updateUptimeChart();
  }

  private updateStorageChart(): void {
    const data = this.overviewFor(this.storageInterval, this.storageRange);
    const labels = data.data.map(point => this.formatLabel(point.label, this.storageInterval));
    const rawValues = data.data.map(point => point.storageUsed ?? 0);
    const unit = this.resolveUnit(this.storageUnit, this.representativeBytes(rawValues));
    const values = rawValues.map(bytes => this.toUnit(bytes, unit));
    this.storageChart = {
      ...this.storageChart,
      series: [{ name: `Storage used (${unit})`, data: values }],
      xaxis: { ...this.storageChart.xaxis, categories: labels },
      tooltip: this.buildDeltaTooltip(unit, values)
    };
    const latest = values.length ? values[values.length - 1] : 0;
    this.storageSummary = `${this.formatNumber(latest)} ${unit}`;
    this.storageDeltaText = this.formatDelta(values, unit);
  }

  private updateTrashChart(): void {
    const data = this.overviewFor(this.trashInterval, this.trashRange);
    const labels = data.data.map(point => this.formatLabel(point.label, this.trashInterval));
    const rawValues = data.data.map(point => point.trashUsed ?? 0);
    const unit = this.resolveUnit(this.trashUnit, this.representativeBytes(rawValues));
    const values = rawValues.map(bytes => this.toUnit(bytes, unit));
    this.trashChart = {
      ...this.trashChart,
      series: [{ name: `Trash (${unit})`, data: values }],
      xaxis: { ...this.trashChart.xaxis, categories: labels },
      tooltip: this.buildDeltaTooltip(unit, values)
    };
    const latest = values.length ? values[values.length - 1] : 0;
    this.trashSummary = `${this.formatNumber(latest)} ${unit}`;
    this.trashDeltaText = this.formatDelta(values, unit);
  }

  private updateBandwidthChart(): void {
    const data = this.overviewFor(this.bandwidthInterval, this.bandwidthRange);
    const labels = data.data.map(point => this.formatLabel(point.label, this.bandwidthInterval));
    const rawIngress = data.data.map(point => point.ingressTotal ?? 0);
    const rawEgress = data.data.map(point => point.egressTotal ?? 0);
    const unit = this.resolveUnit(this.bandwidthUnit, Math.max(this.representativeBytes(rawIngress), this.representativeBytes(rawEgress)));
    const ingressValues = rawIngress.map(bytes => this.toUnit(bytes, unit));
    const egressValues = rawEgress.map(bytes => this.toUnit(bytes, unit));
    const ingressDeltas = this.toDeltaSeries(ingressValues);
    const egressDeltas = this.toDeltaSeries(egressValues);
    this.bandwidthChart = {
      ...this.bandwidthChart,
      series: [
        { name: 'Ingress', data: ingressDeltas },
        { name: 'Egress', data: egressDeltas }
      ],
      xaxis: { ...this.bandwidthChart.xaxis, categories: labels },
      tooltip: this.buildSignedTooltip(unit, [
        { name: 'Ingress', values: ingressDeltas, totals: ingressValues },
        { name: 'Egress', values: egressDeltas, totals: egressValues }
      ])
    };
    const totalIngress = this.formatNumber(ingressValues.length ? ingressValues[ingressValues.length - 1] : 0);
    const totalEgress = this.formatNumber(egressValues.length ? egressValues[egressValues.length - 1] : 0);
    this.bandwidthSummary = `Total Ingress: ${totalIngress} ${unit} · Total Egress: ${totalEgress} ${unit}`;
    this.bandwidthIngressTotal = `${totalIngress} ${unit}`;
    this.bandwidthEgressTotal = `${totalEgress} ${unit}`;
    this.bandwidthIngressDeltaText = this.formatDelta(ingressValues, unit);
    this.bandwidthEgressDeltaText = this.formatDelta(egressValues, unit);
  }

  private toDeltaSeries(values: number[]): number[] {
    return values.map((value, index) => index === 0 ? value : value - values[index - 1]);
  }

  private formatDelta(values: number[], unit: string): string {
    if (values.length < 2) return `+${this.formatNumber(0)} ${unit} (+0.00%)`;
    const previous = values[values.length - 2];
    const current = values[values.length - 1];
    const delta = current - previous;
    const percent = previous ? (delta / previous) * 100 : 0;
    const sign = delta >= 0 ? '+' : '';
    return `${sign}${this.formatNumber(delta)} ${unit} (${sign}${percent.toFixed(2)}%)`;
  }

  private updateUptimeChart(): void {
    const data = this.overviewFor(this.uptimeInterval, this.uptimeRange);
    const labels = data.data.map(point => this.formatLabel(point.label, this.uptimeInterval));
    const values = data.data.map(point => point.uptimePercent);
    this.uptimeChart = this.withData(this.uptimeChart, 'Uptime %', values, labels);
    const average = values.length ? values.reduce((sum, value) => sum + value, 0) / values.length : 100;
    this.uptimeSummary = `Average: ${average.toFixed(2)}%`;
    this.uptimeDeltaText = this.formatDelta(values, '%');
  }

  private percentChange(values: number[], index: number): number {
    const first = values[0];
    if (!first) return 0;
    return ((values[index] - first) / first) * 100;
  }

  private buildDeltaTooltip(unit: string, values: number[]): ChartOptions['tooltip'] {
    return {
      theme: 'dark',
      custom: ({ dataPointIndex }: { dataPointIndex: number }) => {
        const value = this.formatNumber(values[dataPointIndex] ?? 0);
        const hasReference = dataPointIndex > 0 && !!values[0];
        const percentLine = !hasReference ? '' : (() => {
          const change = this.percentChange(values, dataPointIndex);
          const sign = change >= 0 ? '+' : '';
          const color = change >= 0 ? '#c7f36b' : '#f4bb61';
          return `<div style="color:${color};margin-top:4px;">${sign}${change.toFixed(2)}% since start of range</div>`;
        })();
        return `<div style="padding:8px 10px;font:11px 'DM Mono',monospace;color:#e7ecee;background:#141b1f;border:1px solid #26323a;border-radius:6px;">`
          + `<div>${value} ${unit}</div>`
          + percentLine
          + `</div>`;
      }
    };
  }

  private buildSignedTooltip(unit: string, series: { name: string; values: number[]; totals?: number[] }[]): ChartOptions['tooltip'] {
    return {
      theme: 'dark',
      custom: ({ dataPointIndex }: { dataPointIndex: number }) => {
        const rows = series.map(({ name, values, totals }) => {
          const delta = values[dataPointIndex] ?? 0;
          const sign = delta >= 0 ? '+' : '';
          const color = delta >= 0 ? '#c7f36b' : '#f4bb61';
          const totalLine = totals ? `<div>${this.formatNumber(totals[dataPointIndex] ?? 0)} ${unit}</div>` : '';
          return `<div style="margin-top:6px;"><strong>${name}:</strong>`
            + totalLine
            + `<span style="color:${color};">${sign}${this.formatNumber(delta)} ${unit}</span></div>`;
        }).join('');
        return `<div style="padding:8px 10px;font:11px 'DM Mono',monospace;color:#e7ecee;background:#141b1f;border:1px solid #26323a;border-radius:6px;">${rows}</div>`;
      }
    };
  }

  public createChart(color: string, name: string): ChartOptions {
    return {
      series: [{ name, data: [] }],
      chart: { height: 260, type: 'line', toolbar: { show: false }, background: 'transparent', zoom: { enabled: false } },
      dataLabels: { enabled: false }, colors: [color], stroke: { curve: 'smooth', width: 2 },
      xaxis: { categories: [], labels: { style: { colors: '#6f7b83' } }, axisBorder: { show: false }, axisTicks: { show: false } },
      yaxis: { labels: { style: { colors: '#6f7b83' }, formatter: (value: number) => this.formatNumber(value) } },
      grid: { borderColor: '#26323a', strokeDashArray: 4 }, legend: { show: true, labels: { colors: '#849197' } },
      tooltip: { theme: 'dark', y: { formatter: (value: number) => this.formatNumber(value) } }
    };
  }

  private formatNumber(value: number): string {
    return value.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 2 });
  }

  public withData(chart: ChartOptions, name: string, data: number[], labels: string[]): ChartOptions {
    return { ...chart, series: [{ name, data }], xaxis: { ...chart.xaxis, categories: labels } };
  }

  public formatLabel(label: string, interval: Interval): string {
    const date = new Date(label);
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    if (interval === '5s' || interval === '15s' || interval === '30s') {
      const seconds = date.getSeconds().toString().padStart(2, '0');
      return `${hours}:${minutes}:${seconds}`;
    }
    return `${hours}:${minutes}`;
  }

  public toUnit(bytes: number | null, unit: ResolvedUnit): number {
    if (bytes === null || bytes === undefined) return 0;
    const divisor = { KB: 1024, MB: 1024 ** 2, GB: 1024 ** 3 }[unit];
    return Number((bytes / divisor).toFixed(2));
  }

  private resolveUnit(unit: Unit, referenceBytes: number): ResolvedUnit {
    if (unit !== 'Auto') return unit;
    const abs = Math.abs(referenceBytes);
    if (abs >= 1024 ** 3) return 'GB';
    if (abs >= 1024 ** 2) return 'MB';
    return 'KB';
  }

  private representativeBytes(rawValues: number[]): number {
    const last = rawValues[rawValues.length - 1];
    return last || Math.max(0, ...rawValues);
  }

  public formatValue(bytes: number | null): string {
    if (bytes === null || bytes === undefined) return 'Not available';
    const unit = this.resolveUnit('Auto', bytes);
    return `${this.toUnit(bytes, unit)} ${unit}`;
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
