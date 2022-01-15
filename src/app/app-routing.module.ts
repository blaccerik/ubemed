import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {HomeComponent} from "./components/home/home.component";
import {CalculatorComponent} from "./components/calculator/calculator.component";
import {SuvepyksComponent} from "./components/events/suvepyks/suvepyks.component";

const routes: Routes = [
  { path: '', component: HomeComponent,},
  { path: 'search/:game-search', component: HomeComponent,},
  { path: 'calculator', component: CalculatorComponent,},
  { path: 'suvepyks', component: SuvepyksComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
