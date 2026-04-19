package org.example.configuration_loader.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@AutoConfiguration
@EnableJpaRepositories(
        basePackages = "org.example.configuration_loader.repo",
        entityManagerFactoryRef = "configLoaderEntityManagerFactory",
        transactionManagerRef = "configLoaderTransactionManager"
)
@EntityScan(basePackages = "org.example.configuration_loader.entity")
public class ConfigLoaderAutoconfiguration {

    @Bean(name = "configLoaderDataSourceProperties")
    @ConfigurationProperties(prefix = "config-loader.datasource")
    public DataSourceProperties dataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean(name = "configLoaderDataSource")
    public DataSource dataSource(@Qualifier("configLoaderDataSourceProperties") DataSourceProperties props) {
        // This automatically handles HikariCP and applies standard Spring Boot pooling defaults
        return props.initializeDataSourceBuilder().build();
    }

    @Bean(name = "configLoaderEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("configLoaderDataSource") DataSource dataSource) {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(org.springframework.orm.jpa.vendor.Database.POSTGRESQL);

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);

        // This replaces the need for @EntityScan at the class level
        emf.setPackagesToScan("org.example.configuration_loader.entity");
        emf.setJpaVendorAdapter(vendorAdapter);

        emf.setJpaPropertyMap(Map.of(
                "hibernate.show_sql", "false"
        ));
        return emf;
    }

    @Bean(name = "configLoaderTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("configLoaderEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

}
