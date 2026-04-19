package org.example.configuration_loader;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.configuration_loader.entity.Configuration;
import org.example.configuration_loader.repo.ConfigurationRepository;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractConfigurationLoader implements ApplicationContextAware {

    protected final Map<String, String> configuration = new HashMap<>();
    protected final String module;

    private ApplicationContext context;

    public AbstractConfigurationLoader(String module) {
        this.module = module;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @PostConstruct
    public void initialize(){
        loadConfig();
    }

    void loadConfig(){

        ConfigurationRepository repo = context.getBean(ConfigurationRepository.class);

        List<Configuration> all = repo.findByModule(module);

        all.forEach(config -> {
            configuration.put(config.getName(), config.getValue());
        });

        log.info("DB Fetch configuration module: {}, total keys: {}", module, all.size());
    }

    protected abstract void init();
}
