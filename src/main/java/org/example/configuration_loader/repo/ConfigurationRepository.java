package org.example.configuration_loader.repo;

import org.example.configuration_loader.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    List<Configuration> findByModule(String module);
}