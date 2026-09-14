package fr.stefangeorgesco.redisspring.city.service;

import fr.stefangeorgesco.redisspring.city.client.CityClient;
import fr.stefangeorgesco.redisspring.city.dto.City;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CityService {

    private final CityClient cityClient;
    private final RMapReactive<String, City> cityCache;

    public CityService(CityClient cityClient, RedissonReactiveClient redissonClient) {
        this.cityClient = cityClient;
        this.cityCache = redissonClient.getMap("city", new TypedJsonJacksonCodec(String.class, City.class));
    }

    /*
        get from cache
        if not found, get from api, put in cache and return
    */
    public Mono<City> getCity(final String zipCode) {
        return cityCache.get(zipCode)
                .switchIfEmpty(
                        cityClient.getCity(zipCode)
                                .flatMap(city ->
                                        cityCache.fastPut(zipCode, city)
                                                .thenReturn(city))
                );
    }
}
