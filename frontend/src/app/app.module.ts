import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';

@NgModule({
  declarations: [], // Agora pode ser declarado aqui
  imports: [AppComponent, BrowserModule], // Outros módulos podem ser adicionados aqui
  bootstrap: [] // Define o componente principal do app
})
export class AppModule {}
