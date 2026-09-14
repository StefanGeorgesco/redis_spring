package fr.stefangeorgesco.redisspring.city.client;

import fr.stefangeorgesco.redisspring.city.dto.City;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/*
    Check that city api is up and running
    curl -X GET "http://localhost:3030/open-city-api/10001" -H "accept: application/json"
*/

@Service
public class CityClient {

    private final WebClient webClient;

    public CityClient(@Value("${city.api.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<City> getCity(final String zipCode) {
        return webClient.get()
                .uri("/{zipCode}", zipCode)
                .retrieve()
                .bodyToMono(City.class);
    }
}
