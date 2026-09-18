package fr.stefangeorgesco.redisspring.geo.service;

import fr.stefangeorgesco.redisspring.geo.dto.GeoLocation;
import fr.stefangeorgesco.redisspring.geo.dto.Restaurant;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.api.geo.GeoUnit;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Service
public class RestaurantLocatorService {

    private final RGeoReactive<Restaurant> geo;
    private final RMapReactive<String, GeoLocation> map;

    public RestaurantLocatorService(RedissonReactiveClient client) {
        this.geo = client.getGeo("usa:restaurants",
                new TypedJsonJacksonCodec(Restaurant.class));
        this.map = client.getMap("usa:restaurants:locations",
                new TypedJsonJacksonCodec(String.class, GeoLocation.class));
    }

    public Flux<Restaurant> getRestaurantsCloseTo(String restaurantId) {
        double radius = 5.0;
        GeoUnit unit = GeoUnit.MILES;
        return map.get(restaurantId)
                .map(geoLocation -> GeoSearchArgs.from(geoLocation.longitude(), geoLocation.latitude())
                        .radius(radius, unit))
                .flatMap(geo::search)
                .flatMapIterable(Function.identity());
    }
}
