import { Component, Input } from '@angular/core';
import { NgApexchartsModule } from 'ng-apexcharts';

@Component({
  selector: 'app-spline-area-chart',
  imports: [NgApexchartsModule], // Adicionando o módulo aqui
  templateUrl: './spline-area-chart.component.html',
})
export class SplineAreaChartComponent {

  //@ts-ignore
  @Input chartOptions: ChartOptions = '';

  constructor() {

  }
}
