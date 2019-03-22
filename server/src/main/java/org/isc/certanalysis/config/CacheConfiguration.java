package org.isc.certanalysis.config;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.isc.certanalysis.repository.CrlRepository;
import org.isc.certanalysis.repository.UserRepository;
import org.isc.certanalysis.service.NotificationGroupService;
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
			cm.createCache(UserRepository.USERS_BY_ID_CACHE, jcacheConfiguration);
			cm.createCache(CrlRepository.CRL_BY_ISSUER_AND_SCHEME_ID, jcacheConfiguration);
			cm.createCache(NotificationGroupService.NOTIFICATION_GROUPS_ALL, jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.Certificate", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.Crl", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.Crl.crlRevokeds", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.CrlRevoked", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.CrlUrl", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.File", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.File.notificationGroups", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.File.crls", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.NotificationGroup", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.Privilege", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.Role", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.Role.privileges", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.Scheme", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.Scheme.crlUrls", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.User", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.User.roles", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.User.notificationGroups", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.CrlMailLog", jcacheConfiguration);
			cm.createCache("org.isc.certanalysis.domain.CertificateMailLog", jcacheConfiguration);
		};
	}
}
