import { Component, Input } from '@angular/core';
import { NgApexchartsModule } from 'ng-apexcharts';
import { ChartOptions } from '../../../models/chart-options.model';

@Component({
  selector: 'app-spline-area-chart',
  imports: [NgApexchartsModule], // Adicionando o módulo aqui
  templateUrl: './spline-area-chart.component.html',
})
export class SplineAreaChartComponent {

  @Input() chartOptions!: ChartOptions;
}
