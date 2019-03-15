import {InjectionToken} from "@angular/core";

export interface AppConfig {
    apiUrl: string;
}

export const APP_CONFIG_TOKEN = new InjectionToken<AppConfig>('app.config');

export const APP_CONFIG: AppConfig = {
    apiUrl: './api'
};