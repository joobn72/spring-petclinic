package org.springframework.samples.petclinic;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.samples.petclinic.util.CallMonitoringAspect;

/**
 * @author YongKwon Park
 */
@Configuration
@EnableAspectJAutoProxy
@EnableMBeanExport
@EnableCaching
public class ToolsConfig {

    @Bean
    public CallMonitoringAspect callMonitor() {
        return new CallMonitoringAspect();
    }

    @Bean
    public EhCacheManagerFactoryBean ehcache() {
        EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
        factoryBean.setConfigLocation(new ClassPathResource("cache/ehcache.xml"));

        return factoryBean;
    }

    @Bean
    public CacheManager cacheManager(net.sf.ehcache.CacheManager ehcacheManager) {
        return new EhCacheCacheManager(ehcacheManager);
    }

}