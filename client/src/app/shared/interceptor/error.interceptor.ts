import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {catchError, tap} from "rxjs/operators";
import {Injectable} from "@angular/core";
import {AlertService} from "../alert/alert.service";
import {AuthenticationService} from "../authentication/authentication.service";
import {Router} from "@angular/router";

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

    constructor(
        private router: Router,
        private alertService: AlertService,
        private authenticationService: AuthenticationService
    ) {}
    
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(
            tap(
                () => {},
                (err: any) => {
                    if (err instanceof HttpErrorResponse){
                        if ([401, 403].indexOf(err.status) !== -1 && err.url && err.url.indexOf('/api/authentication') === -1) {
                            // auto logout if 401 Unauthorized or 403 Forbidden response returned from api
                            this.authenticationService.logout().subscribe( /*() =>location.reload()*/() => this.router.navigate(['/login']));
                            return;
                        }

                        let error = '';
                        if(err.error && typeof err.error === 'object') {
                            error = err.error.message;
                        } else {
                            error = err.error || err.statusText || err.status; 
                        }
                        this.alertService.error(error);
                    }
                }
            )
        );
        /*return next.handle(request).pipe(catchError(err => {
            /!*if (err.status === 401) {
                // auto logout if 401 response returned from api
                // this.authenticationService.logout();
                // location.reload(true);
            }*!/

            if (err instanceof HttpErrorResponse){
                const error = err.error || err.statusText || err.status;
                this.alertService.error(error);
                return throwError(error);
            }
            
        }))*/
    }
}
