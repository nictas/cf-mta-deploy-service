package com.sap.cloud.lm.sl.cf.web.configuration;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ThreadFactory;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.push.PushRegistryConfig;
import io.micrometer.dynatrace.DynatraceConfig;
import io.micrometer.dynatrace.DynatraceMeterRegistry;

@Configuration
public class MicrometerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicrometerConfiguration.class);

    @Bean
    public DynatraceMeterRegistry dynatraceMeterRegistry() {
        DynatraceConfig dynatraceConfig = new DynatraceConfig() {

            @Override
            public String apiToken() {
                return "49v3i8TATVCwqlu63ezvY";
            }

            @Override
            public String uri() {
                return "https://apm.cf.sap.hana.ondemand.com/e/4e03096e-5f96-4b02-92c0-848f64fb5e78";
            }

            @Override
            public String deviceId() {
                return "micrometer";
            }

            @Override
            public String technologyType() {
                return "micrometer";
            }

            @Override
            public boolean enabled() {
                return true;
            }

            @Override
            public Duration step() {
                String step = System.getenv("STEP");
                if (step == null) {
                    return Duration.ofSeconds(10);
                }
                int seconds = Integer.parseInt(step);
                return Duration.ofSeconds(seconds);
            }

            @Override
            public int batchSize() {
                String batchSize = System.getenv("BATCH_SIZE");
                if (batchSize == null) {
                    return DynatraceConfig.super.batchSize();
                }
                return Integer.parseInt(batchSize);
            }

            @Override
            @Nullable
            public String get(String k) {
                return null;
            }

        };
        DynatraceMeterRegistry registry = new DynatraceMeterRegistry(dynatraceConfig, Clock.SYSTEM);
        Metrics.globalRegistry.add(registry);
        LOGGER.info("USING THE GLOBAL MICROMETER REGISTRY");
        Gauge.builder("reactor.netty.connections", () -> new Random().nextInt(128))
             .description("The number of active Reactor Netty connections")
             .register(Metrics.globalRegistry);
        return registry;
    }

}
