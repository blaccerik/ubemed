import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {HomeComponent} from "./components/home/home.component";
import {CalculatorComponent} from "./components/calculator/calculator.component";
import {SuvepyksComponent} from "./components/events/suvepyks/suvepyks.component";
import {ForumComponent} from "./components/forum/forum.component";
import {CreatePostComponent} from "./components/forum/createpost/create-post.component";
import {PostComponent} from "./components/forum/post/post.component";
import {ErrorComponent} from "./components/error/error.component";
import {StoreComponent} from "./components/store/store.component";
import {StoreNewComponent} from "./components/store/store-new/store-new.component";
import {InventoryComponent} from "./components/inventory/inventory.component";
import {CasinoComponent} from "./components/casino/casino.component";

const routes: Routes = [
  { path: '', component: HomeComponent,},
  { path: 'search/:game-search', component: HomeComponent,},
  { path: 'calculator', component: CalculatorComponent,},
  { path: 'suvepyks', component: SuvepyksComponent},
  { path: 'forum', component: ForumComponent},
  { path: 'forum/new', component: CreatePostComponent},
  { path: 'forum/:id', component: PostComponent},
  { path: 'error', component: ErrorComponent},
  { path: 'store', component: StoreComponent},
  { path: 'store/new', component: StoreNewComponent},
  { path: 'inventory', component: InventoryComponent},
  { path: 'casino', component: CasinoComponent},
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
