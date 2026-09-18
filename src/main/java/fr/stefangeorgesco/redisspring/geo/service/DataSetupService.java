package fr.stefangeorgesco.redisspring.geo.service;

import fr.stefangeorgesco.redisspring.geo.dto.GeoLocation;
import fr.stefangeorgesco.redisspring.geo.dto.Restaurant;
import fr.stefangeorgesco.redisspring.geo.util.RestaurantUtil;
import org.jspecify.annotations.NonNull;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class DataSetupService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSetupService.class);

    private final RGeoReactive<Restaurant> geo;
    private final RMapReactive<String, GeoLocation> map;

    public DataSetupService(RedissonReactiveClient client) {
        this.geo = client.getGeo("usa:restaurants",
                new TypedJsonJacksonCodec(Restaurant.class));
        this.map = client.getMap("usa:restaurants:locations",
                new TypedJsonJacksonCodec(String.class, GeoLocation.class));
    }

    @Override
    public void run(String @NonNull ... args) {
        List<Restaurant> restaurants = RestaurantUtil.getRestaurants();

        log.info("Found {} restaurant entries in file",
                restaurants.size());
        log.info("Found {} distinct restaurant ids",
                restaurants
                        .stream()
                        .map(Restaurant::id)
                        .distinct()
                        .count());
        log.info("Found {} distinct restaurant datasets",
                restaurants
                        .stream()
                        .map(Restaurant::toString)
                        .distinct()
                        .count());

        Flux.fromIterable(restaurants)
                .flatMap(r -> geo.add(r.longitude(), r.latitude(), r).thenReturn(r))
                .flatMap(r -> map.fastPut(r.id(), GeoLocation.of(r.latitude(), r.longitude())))
                .then()
                .doFinally(signalType -> log.info("Restaurant data setup ended with signalType '{}'",
                        signalType))
                .subscribe();
    }
}
