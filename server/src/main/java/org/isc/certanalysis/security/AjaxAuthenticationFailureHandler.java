package org.isc.certanalysis.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author p.dzeviarylin
 */
public class AjaxAuthenticationFailureHandler implements AuthenticationFailureHandler {

	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        request.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}
}
