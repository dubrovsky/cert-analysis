package org.isc.certanalysis.config;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.isc.certanalysis.repository.CrlRepository;
import org.isc.certanalysis.repository.UserRepository;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @author p.dzeviarylin
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

	private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

	public CacheConfiguration(ApplicationProperties applicationProperties) {
//		BeanClassLoaderAwareJCacheRegionFactory.setBeanClassLoader(this.getClass().getClassLoader());
		final ApplicationProperties.Cache.Ehcache ehcache = applicationProperties.getCache().getEhcache();

		jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
				CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
						ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
						.withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
						.build());
	}

	@Bean
	public JCacheManagerCustomizer cacheManagerCustomizer() {
		return cm -> {
			cm.createCache(UserRepository.USERS_BY_LOGIN_CACHE, jcacheConfiguration);
			cm.createCache(CrlRepository.CRL_BY_ISSUER_AND_SCHEME_ID, jcacheConfiguration);
		};
	}
}
