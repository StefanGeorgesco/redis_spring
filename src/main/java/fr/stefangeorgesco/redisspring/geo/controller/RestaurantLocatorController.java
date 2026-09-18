package fr.stefangeorgesco.redisspring.geo.controller;

import fr.stefangeorgesco.redisspring.geo.dto.Restaurant;
import fr.stefangeorgesco.redisspring.geo.service.RestaurantLocatorService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("geo")
@CrossOrigin(origins = "http://localhost:4200")
public class RestaurantLocatorController {

    private final RestaurantLocatorService service;

    public RestaurantLocatorController(RestaurantLocatorService service) {
        this.service = service;
    }

    @GetMapping("{restaurantId}")
    public Flux<Restaurant> getRestaurantsCloseTo(@PathVariable String restaurantId) {
        return service.getRestaurantsCloseTo(restaurantId);
    }
}
