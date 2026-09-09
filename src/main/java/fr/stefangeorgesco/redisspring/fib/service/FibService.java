package fr.stefangeorgesco.redisspring.fib.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class FibService {

    private static final Logger log = LoggerFactory.getLogger(FibService.class);

    @Cacheable("math:fib")
    public int getFib(int n) {
        log.info("Calculating Fibonacci number for n={}", n);
        return fib(n);
    }

    // Intentional 2^n complexity
    private int fib(int n) {
        if (n < 2) {
            return n;
        }
        return fib(n - 1) + fib(n - 2);
    }
}
