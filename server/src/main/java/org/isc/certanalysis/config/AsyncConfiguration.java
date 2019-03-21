package org.isc.certanalysis.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author p.dzeviarylin
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfiguration implements AsyncConfigurer, SchedulingConfigurer {

	private final ApplicationProperties applicationProperties;

	public AsyncConfiguration(ApplicationProperties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}


	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(scheduledTaskExecutor());
	}

	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(applicationProperties.getAsync().getCorePoolSize());
		executor.setMaxPoolSize(applicationProperties.getAsync().getMaxPoolSize());
		executor.setQueueCapacity(applicationProperties.getAsync().getQueueCapacity());
		executor.setThreadNamePrefix("cert-analysis-Executor-");
		executor.initialize();
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new SimpleAsyncUncaughtExceptionHandler();
	}

	@Bean
	public Executor scheduledTaskExecutor() {
		return Executors.newScheduledThreadPool(applicationProperties.getAsync().getCorePoolSize());
	}
}
