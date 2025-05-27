package com.fatia.inventoryservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Objects;

@Configuration
@PropertySource({"classpath:application.properties"})
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.fatia.inventoryservice.warehouserepositories",
        entityManagerFactoryRef = "warehouseEntityManager",
        transactionManagerRef = "warehouseTransactionManager"
)
public class WarehouseDBConfig {

    @Autowired
    private Environment env;

    @Primary
    @Bean
    @ConfigurationProperties("spring.second-datasource")
    public DataSource warehouseDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("spring.datasource.driver-class-name")));
        dataSource.setUrl(env.getProperty("spring.second-datasource.url"));
        dataSource.setUsername(env.getProperty("spring.second-datasource.username"));
        dataSource.setPassword(env.getProperty("spring.second-datasource.password"));
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean warehouseEntityManager() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(warehouseDataSource());
        emf.setPackagesToScan(
                new String[]{"com.fatia.inventoryservice.warehouseentities"}
        );
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(
                "hibernate.hbm2ddl.auto",
                env.getProperty("spring.jpa.hibernate.ddl-auto"
                ));
        properties.put(
                "hibernate.dialect",
                env.getProperty("spring.jpa.properties.hibernate.dialect"
                ));

        emf.setJpaPropertyMap(properties);
        return emf;
    }

    @Primary
    @Bean
    public PlatformTransactionManager warehouseTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                warehouseEntityManager().getObject()
        );

        return transactionManager;
    }
}
