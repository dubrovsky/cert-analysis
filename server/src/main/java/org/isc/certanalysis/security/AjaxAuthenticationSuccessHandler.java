package org.isc.certanalysis.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author p.dzeviarylin
 */
public class AjaxAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	public AjaxAuthenticationSuccessHandler() {
	}

	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		response.setStatus(200);
	}
}
