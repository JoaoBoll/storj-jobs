import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";

import { SplineAreaChartComponent } from "./spline-area-chart.component";
import { NgApexchartsModule } from "ng-apexcharts";

@NgModule({
  declarations: [],
  imports: [SplineAreaChartComponent, BrowserModule, NgApexchartsModule],
  providers: [],
  bootstrap: []
})
export class SplineAreaChartModule {}
