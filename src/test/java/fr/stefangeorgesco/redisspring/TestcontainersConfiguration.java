package fr.stefangeorgesco.redisspring;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    RedisContainer redisContainer() {
        return new RedisContainer(DockerImageName.parse("redis:latest"));
    }

    @Bean
    @SuppressWarnings("resource")
    GenericContainer<?> cityApiExternalServicesContainer() {
        return new GenericContainer<>(
                DockerImageName.parse("city-api-external-services:latest")
        )
                .withExposedPorts(3030);
    }

    @Bean
    @SuppressWarnings("HttpUrlsUsage")
    DynamicPropertyRegistrar cityApiProperties(
            GenericContainer<?> cityApiExternalServicesContainer) {

        return registry -> registry.add(
                "city.api.base-url",
                () -> String.format("http://%s:%d/open-city-api",
                        cityApiExternalServicesContainer.getHost(),
                        cityApiExternalServicesContainer.getMappedPort(3030))
        );
    }
}
