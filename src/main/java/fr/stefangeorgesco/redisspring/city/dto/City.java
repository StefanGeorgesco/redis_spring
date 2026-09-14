package fr.stefangeorgesco.redisspring.city.dto;

public record City(String zip,
                   String city,
                   String stateName,
                   int temperature) {
}
