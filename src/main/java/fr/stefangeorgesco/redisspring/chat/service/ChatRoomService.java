package fr.stefangeorgesco.redisspring.chat.service;

import org.jspecify.annotations.NullMarked;
import org.redisson.api.RListReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class ChatRoomService implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatRoomService.class);

    private final RedissonReactiveClient client;

    public ChatRoomService(RedissonReactiveClient client) {
        this.client = client;
    }

    @Override
    @NullMarked
    public Mono<Void> handle(WebSocketSession session) {
        String room = getChatRoomName(session);
        RTopicReactive topic = client.getTopic(room, StringCodec.INSTANCE);
        RListReactive<String> history = client.getList("history:" + room, StringCodec.INSTANCE);

        Mono<Void> inbound = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(msg -> history.add(msg).then(topic.publish(msg)))
                .doOnError(error -> log.error("Error while publishing message to topic: {}",
                        error.getMessage()))
                .then();

        Flux<WebSocketMessage> outbound = topic.getMessages(String.class)
                .startWith(history.iterator())
                .map(session::textMessage)
                .doOnError(error -> log.error("Error while receiving message from topic: {}",
                        error.getMessage()));

        return session.send(outbound)
                .and(inbound)
                .doFinally(signal -> log.info("WebSocket session closed with signal: {}", signal));
    }

    private static String getChatRoomName(WebSocketSession session) {
        URI uri = session.getHandshakeInfo().getUri();
        return UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .toSingleValueMap()
                .getOrDefault("room", "default");
    }
}
