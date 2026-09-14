package fr.stefangeorgesco.redisspring.city.controller;

import fr.stefangeorgesco.redisspring.city.dto.City;
import fr.stefangeorgesco.redisspring.city.service.CityService;
import fr.stefangeorgesco.redisspring.city.service.UpdateCityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("city")
public class CityController {

    private final CityService cityService;
    private final UpdateCityService updateCityService;

    public CityController(CityService cityService, UpdateCityService updateCityService) {
        this.cityService = cityService;
        this.updateCityService = updateCityService;
    }

    @GetMapping("{zipCode}")
    public Mono<City> getCity(@PathVariable final String zipCode) {
        return cityService.getCity(zipCode);
    }

    @GetMapping("update/{zipCode}")
    public Mono<City> updateCity(@PathVariable final String zipCode) {
        return updateCityService.getCity(zipCode);
    }
}
