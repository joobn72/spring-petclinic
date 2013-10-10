package org.springframework.samples.petclinic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;

/**
 * @author YongKwon Park
 */
@Configuration
@Import({ DataSourceConfig.JavaeeDataSourceConfig.class })
public class DataSourceConfig {

    @Autowired
    private Environment environment;

    @Bean
    public DataSource dataSource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
        dataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
        dataSource.setPassword(environment.getRequiredProperty("jdbc.password"));

        return dataSource;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer() {
        String initLocation = environment.getRequiredProperty("jdbc.initLocation");
        String dataLocation = environment.getRequiredProperty("jdbc.dataLocation");

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource(initLocation));
        populator.addScript(new ClassPathResource(dataLocation));

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource());
        initializer.setDatabasePopulator(populator);

        return initializer;
    }


    @Configuration
    @Profile(Profiles.JAVAEE)
    public static class JavaeeDataSourceConfig {

        @Bean
        public DataSource dataSource() {
            return new JndiDataSourceLookup().getDataSource("java:comp/env/jdbc/petclinic");
        }

    }

}