package io.sqlinsight.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProdProfileEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (environment.getActiveProfiles() != null && Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
            if (!environment.getPropertySources().contains("sqlInsightProdDefaults")) {
                Map<String, Object> defaults = new HashMap<>();
                defaults.put("sql-insight.dashboard-enabled", false);
                environment.getPropertySources().addLast(new MapPropertySource("sqlInsightProdDefaults", defaults));
            }
        }
    }
}
