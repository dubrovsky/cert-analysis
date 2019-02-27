package org.isc.certanalysis.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author p.dzeviarylin
 */
public class AjaxAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	public static final String UNAUTHORIZED_MESSAGE = "Authentication failed";

	public AjaxAuthenticationFailureHandler() {
	}

	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		response.sendError(401, "Authentication failed");
	}
}
