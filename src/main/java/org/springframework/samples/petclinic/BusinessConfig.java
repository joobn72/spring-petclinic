package org.springframework.samples.petclinic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * @author YongKwon Park
 */
@Configuration
@Import(DataAccessConfig.class)
@ComponentScan(basePackages = "org.springframework.samples.petclinic.service")
public class BusinessConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}