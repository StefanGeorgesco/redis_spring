package fr.stefangeorgesco.redisspring.fib.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class FibService {

    private static final Logger log = LoggerFactory.getLogger(FibService.class);

    // GET
    @Cacheable(value="math:fib", key = "#n")
    public int getFib(int n, String name) {
        log.info("Calculating Fibonacci number for n={}, name={}", n, name);
        return fib(n);
    }

    // Would be POST / PUT / PATCH / DELETE
    @CacheEvict(value="math:fib", key = "#n")
    public void clearCache(int n) {
        log.info("Clearing Fibonacci cache for n={}", n);
    }

    // Intentional 2^n complexity
    private int fib(int n) {
        if (n < 2) {
            return n;
        }
        return fib(n - 1) + fib(n - 2);
    }
}
