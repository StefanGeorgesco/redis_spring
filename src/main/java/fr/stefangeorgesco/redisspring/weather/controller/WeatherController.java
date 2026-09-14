package fr.stefangeorgesco.redisspring.weather.controller;

import fr.stefangeorgesco.redisspring.weather.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("{zipCode}")
    public Mono<Integer> getWeatherInfo(@PathVariable int zipCode) {
        return Mono.fromSupplier(() -> weatherService.getInfo(zipCode));
    }
}
