import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule} from "@angular/common/http";
import {MatTabsModule} from "@angular/material/tabs";
import {MatIconModule} from "@angular/material/icon";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatSelectModule} from "@angular/material/select";
import { SearchBarComponent } from './components/search-bar/search-bar.component';
import { HomeComponent } from './components/home/home.component';
import {CalculatorComponent} from './components/calculator/calculator.component';
import {MatDividerModule} from "@angular/material/divider";
import {TranslateLoader, TranslateModule} from "@ngx-translate/core";
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import {MatButtonModule} from "@angular/material/button";
import {MatInputModule} from "@angular/material/input";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatMenuModule} from "@angular/material/menu";
import {MatButtonToggleModule} from "@angular/material/button-toggle";
import { SuvepyksComponent } from './components/events/suvepyks/suvepyks.component';
import { ForumComponent } from './components/forum/forum.component';
import { CreatePostComponent } from './components/forum/createpost/create-post.component';
import { PostComponent } from './components/forum/post/post.component';
import { ErrorComponent } from './components/error/error.component';
import { LoginComponent } from './components/login/login.component';
import {JWTInterceptor} from "./components/interceptor/jwt.interceptor";
import {MatDialogModule} from "@angular/material/dialog";

@NgModule({
  declarations: [
    AppComponent,
    SearchBarComponent,
    HomeComponent,
    CalculatorComponent,
    SuvepyksComponent,
    ForumComponent,
    CreatePostComponent,
    PostComponent,
    ErrorComponent,
    LoginComponent
  ],
    imports: [
        BrowserModule,
        FormsModule,
        HttpClientModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient],
            },
            isolate: false
        }),
        AppRoutingModule,
        MatTabsModule,
        MatIconModule,
        MatFormFieldModule,
        MatSelectModule,
        BrowserAnimationsModule,
        MatDividerModule,
        ReactiveFormsModule,
        MatButtonModule,
        MatInputModule,
        MatToolbarModule,
        MatMenuModule,
        MatButtonToggleModule,
        MatDialogModule
    ],
  providers: [{ provide: HTTP_INTERCEPTORS, useClass: JWTInterceptor, multi: true },],
  bootstrap: [AppComponent]
})
export class AppModule { }
// ng serve --host 0.0.0.0
//

// ng build
// docker build -t ng-docker .

// http-server dist/ubemed
// http-server dist/ubemed -P http://localhost:8080
export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http);
}
