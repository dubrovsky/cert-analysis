package org.isc.certanalysis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer, WebServerFactoryCustomizer<WebServerFactory> {

    private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

    private final Environment env;

    public WebConfigurer(Environment env) {

        this.env = env;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
    }

	@Override
	public void customize(WebServerFactory factory) {

	}

	/*@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**")
						.allowedOrigins("http://localhost:4200")
						.allowedMethods("PUT", "DELETE", "GET", "POST", "OPTIONS")
						.allowCredentials(false).maxAge(3600);
			}
		};
	}*/
}
