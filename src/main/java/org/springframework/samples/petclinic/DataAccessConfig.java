package org.springframework.samples.petclinic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * @author YongKwon Park
 */
@Configuration
@Import({ DataSourceConfig.class
        , DataAccessConfig.JPAConfig.class
        , DataAccessConfig.JdbcConfig.class })
@PropertySource("classpath:spring/data-access.properties")
@EnableTransactionManagement
public class DataAccessConfig {


    @Configuration
    @Profile({Profiles.JPA, Profiles.SPRING_DATA_JPA})
    @Import({ DataAccessConfig.JPARepositoryConfig.class
            , DataAccessConfig.SpringDataJPARepositoryConfig.class })
    public static class JPAConfig {

        @Autowired
        private Environment environment;

        @Autowired
        private DataSource dataSource;

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            Database database = environment.getRequiredProperty("jpa.database", Database.class);
            boolean showSql = environment.getRequiredProperty("jpa.showSql", Boolean.class);

            HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
            jpaVendorAdapter.setDatabase(database);
            jpaVendorAdapter.setShowSql(showSql);

            LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
            factoryBean.setDataSource(dataSource);
            factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
            factoryBean.setPersistenceUnitName("petclinic");
            factoryBean.setPackagesToScan("org.springframework.samples.petclinic");

            return factoryBean;
        }

        @Bean
        public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
            return new JpaTransactionManager(entityManagerFactory);
        }

        @Bean
        public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
            return new PersistenceExceptionTranslationPostProcessor();
        }

    }

    @Configuration
    @Profile(Profiles.JPA)
    @ComponentScan(basePackages = "org.springframework.samples.petclinic.repository.jpa")
    public static class JPARepositoryConfig {
    }

    @Configuration
    @Profile(Profiles.SPRING_DATA_JPA)
    @EnableJpaRepositories(basePackages = "org.springframework.samples.petclinic.repository.springdatajpa")
    public static class SpringDataJPARepositoryConfig {
    }

    @Configuration
    @Profile(Profiles.JDBC)
    @ComponentScan(basePackages = "org.springframework.samples.petclinic.repository.jdbc")
    public static class JdbcConfig {

        @Autowired
        private DataSource dataSource;

        @Bean
        public PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        public JdbcTemplate jdbcTemplate() {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
            return new NamedParameterJdbcTemplate(dataSource);
        }

    }

}