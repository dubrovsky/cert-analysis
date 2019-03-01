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

	public Cache getCache() {
		return cache;
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
}
