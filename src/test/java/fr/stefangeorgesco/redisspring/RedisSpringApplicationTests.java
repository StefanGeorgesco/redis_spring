package fr.stefangeorgesco.redisspring;

import org.junit.jupiter.api.RepeatedTest;
import org.redisson.api.RAtomicLongReactive;
import org.redisson.api.RedissonReactiveClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class RedisSpringApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(RedisSpringApplicationTests.class);

	@Autowired
	private ReactiveStringRedisTemplate template;

	@Autowired
	private RedissonReactiveClient client;

	@RepeatedTest(3)
	void springDataRedisTest() {
		ReactiveValueOperations<String, String> valueOperations = template.opsForValue();

		long before = System.currentTimeMillis();

		Mono<Void> mono = Flux.range(1, 500_000)
				.flatMap(i -> valueOperations.increment("user:1:visit"))
				.then();

		StepVerifier.create(mono)
				.verifyComplete();

		long after = System.currentTimeMillis();

		log.info("Time taken by Spring Data Redis for 500,000 increments: {} ms", (after - before));
	}

	@RepeatedTest(3)
	void redissonTest() {
		RAtomicLongReactive atomicLong = client.getAtomicLong("user:2:visit");

		long before = System.currentTimeMillis();

		Mono<Void> mono = Flux.range(1, 500_000)
				.flatMap(i -> atomicLong.incrementAndGet())
				.then();

		StepVerifier.create(mono)
				.verifyComplete();

		long after = System.currentTimeMillis();

		log.info("Time taken by Redis for 500,000 increments: {} ms", (after - before));
	}

}
