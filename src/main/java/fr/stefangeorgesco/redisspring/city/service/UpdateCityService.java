package fr.stefangeorgesco.redisspring.city.service;

import fr.stefangeorgesco.redisspring.city.client.CityClient;
import fr.stefangeorgesco.redisspring.city.dto.City;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UpdateCityService {

    private static final Logger log = LoggerFactory.getLogger(UpdateCityService.class);

    private final CityClient cityClient;
    private final RMapReactive<String, City> cityCache;

    public UpdateCityService(CityClient cityClient, RedissonReactiveClient redissonClient) {
        this.cityClient = cityClient;
        this.cityCache = redissonClient.getMap("update-city", new TypedJsonJacksonCodec(String.class, City.class));
    }

    public Mono<City> getCity(final String zipCode) {
        return cityCache.get(zipCode).onErrorResume(t -> cityClient.getCity(zipCode));
    }

    @Scheduled(fixedRate = 10_000)
    public void update() {
        cityClient.getAllCities()
                .doFirst(() -> log.info("Updating city cache..."))
                .doOnComplete(() -> log.info("City cache update terminated."))
                .doOnError(t -> log.warn("Error while updating city cache: {}", t.getMessage()))
                .onErrorResume(t -> Flux.empty())
                .collectList()
                .map(cities -> cities.stream().collect(Collectors.toMap(City::zip, Function.identity())))
                .flatMap(cityCache::putAll)
                .subscribe();
    }
}
