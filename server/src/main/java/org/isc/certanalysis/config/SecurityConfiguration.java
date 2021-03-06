package org.isc.certanalysis.config;

import org.isc.certanalysis.security.AjaxAuthenticationFailureHandler;
import org.isc.certanalysis.security.AjaxAuthenticationSuccessHandler;
import org.isc.certanalysis.security.AjaxLogoutSuccessHandler;
import org.isc.certanalysis.security.AjaxAuthenticationEntryPoint;
import org.isc.certanalysis.security.AuthoritiesConstants;
import org.isc.certanalysis.security.Sha256PasswordEncoder;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import javax.annotation.PostConstruct;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	private final UserDetailsService userDetailsService;

	private final SecurityProblemSupport problemSupport;

	public SecurityConfiguration(AuthenticationManagerBuilder authenticationManagerBuilder, UserDetailsService userDetailsService,
	                             SecurityProblemSupport problemSupport) {
		this.authenticationManagerBuilder = authenticationManagerBuilder;
		this.userDetailsService = userDetailsService;
		this.problemSupport = problemSupport;
	}

	@PostConstruct
	public void init() {
		try {
			authenticationManagerBuilder
                    .authenticationProvider(authenticationProvider())
					/*.userDetailsService(userDetailsService)
					.passwordEncoder(passwordEncoder())*/;
		} catch (Exception e) {
			throw new BeanInitializationException("Security configuration failed", e);
		}
	}

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setHideUserNotFoundExceptions(false);
        return authenticationProvider;
    }

    @Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler() {
		return new AjaxAuthenticationSuccessHandler();
	}

	@Bean
	public AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler() {
		return new AjaxAuthenticationFailureHandler();
	}

	@Bean
	public AuthenticationEntryPoint httpStatusEntryPoint() {
//		return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
		return new AjaxAuthenticationEntryPoint();
	}

	@Bean
	public AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler() {
		return new AjaxLogoutSuccessHandler();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new Sha256PasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring()
				.antMatchers(HttpMethod.OPTIONS, "/**")
				.antMatchers("/*.{js,html,json,gif,png,ico,svg,woff2,eot,ttf}");
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
//				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//				.and()
				.exceptionHandling()
//					.authenticationEntryPoint(problemSupport)
					.authenticationEntryPoint(httpStatusEntryPoint())
					.accessDeniedHandler(problemSupport)
				.and()
					.formLogin()
					.loginProcessingUrl("/api/authentication")
					.successHandler(ajaxAuthenticationSuccessHandler())
					.failureHandler(ajaxAuthenticationFailureHandler())
					.permitAll()
				.and()
					.logout()
					.logoutUrl("/api/logout")
					.logoutSuccessHandler(ajaxLogoutSuccessHandler())
					.permitAll()
				.and()
					.headers()
					.frameOptions()
					.disable()
				.and()
					.authorizeRequests()
					.antMatchers("/api/authenticate").permitAll()
					.antMatchers("/api/logout").permitAll()
					.antMatchers("/api/user/account").authenticated()
					.antMatchers("/api/user*/**").hasAuthority(AuthoritiesConstants.ADMIN)
					.antMatchers("/api/**").authenticated()
					.anyRequest().authenticated();

	}
}
