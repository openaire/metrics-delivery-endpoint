package eu.openaire.mas.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring boot main application class.
 * @author mhorst
 *
 */
@SpringBootApplication
@EnableScheduling
public class MetricsDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetricsDeliveryApplication.class, args);
    }

    @Profile("eosc")
    @ComponentScan("eu.openaire.mas.eosc")
    private static class EoscIntegration {}
}
