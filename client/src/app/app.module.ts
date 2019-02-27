import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppRoutingModule} from './app-routing.module';
import {MainComponent} from './layouts/main/main.component';
import {ToolbarModule} from 'primeng/toolbar';
import {ButtonModule} from 'primeng/button';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {ToolbarComponent} from './layouts/toolbar/toolbar.component';
import {SchemeModule} from "./entities/scheme/scheme.module";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {APP_CONFIG, APP_CONFIG_TOKEN} from "./app.config";
import {ErrorInterceptor} from "./shared/interceptor/error.interceptor";
import {SharedModule} from "./shared/shared.module";

@NgModule({
    declarations: [
        MainComponent,
        ToolbarComponent
    ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        HttpClientModule,
        SchemeModule,
        SharedModule,
        ButtonModule,
        ToolbarModule,
        AppRoutingModule
    ],
    providers: [
        {provide: APP_CONFIG_TOKEN, useValue: APP_CONFIG},
        {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true}
    ],
    bootstrap: [MainComponent]
})
export class AppModule {
}
