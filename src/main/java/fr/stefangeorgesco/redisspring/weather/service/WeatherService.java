package fr.stefangeorgesco.redisspring.weather.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Service
public class WeatherService {
    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    private final ExternalServiceClient externalServiceClient;

    public WeatherService(ExternalServiceClient externalServiceClient) {
        this.externalServiceClient = externalServiceClient;
    }

    @SuppressWarnings("unused")
    @Cacheable(value = "weather:info", key = "#zipCode")
    public int getInfo(int zipCode) {
        return 0;
    }

    @Scheduled(fixedRate = 10_000)
    public void update() {
        log.info("Updating weather info");
        IntStream.rangeClosed(1, 5)
                .forEach(externalServiceClient::getWeatherInfo);
    }
}
