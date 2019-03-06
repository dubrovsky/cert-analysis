package org.isc.certanalysis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author p.dzeviarylin
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "cert-analysis", ignoreUnknownFields = false)
public class ApplicationProperties {

	private final Cache cache = new Cache();
	private final Mail mail = new Mail();

	public Cache getCache() {
		return cache;
	}

	public Mail getMail() {
		return mail;
	}

	public static class Cache {

		private final Ehcache ehcache = new Ehcache();

		public Ehcache getEhcache() {
			return ehcache;
		}

		public static class Ehcache {
			
			private int timeToLiveSeconds;
			private long maxEntries;

			public int getTimeToLiveSeconds() {
				return timeToLiveSeconds;
			}

			public void setTimeToLiveSeconds(int timeToLiveSeconds) {
				this.timeToLiveSeconds = timeToLiveSeconds;
			}

			public long getMaxEntries() {
				return maxEntries;
			}

			public void setMaxEntries(long maxEntries) {
				this.maxEntries = maxEntries;
			}
		}

	}

	public static class Mail {
		private String from;
		private String baseUrl;

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getBaseUrl() {
			return baseUrl;
		}

		public void setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
		}
	}
}
