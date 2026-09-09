package fr.stefangeorgesco.redisspring.fib.controller;

import fr.stefangeorgesco.redisspring.fib.service.FibService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("fib")
public class FibController {

    private final FibService service;

    public FibController(FibService service) {
        this.service = service;
    }

    @GetMapping("{n}/{name}")
    public Mono<Integer> getFib(@PathVariable int n, @PathVariable String name) {
        return Mono.fromSupplier(() -> service.getFib(n, name));
    }

    @PostMapping("{n}/clear")
    public Mono<Void> clearCache(@PathVariable int n) {
        return Mono.fromRunnable(() -> service.clearCache(n));
    }
}
