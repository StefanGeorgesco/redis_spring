package fr.stefangeorgesco.redisspring.chat.config;

import fr.stefangeorgesco.redisspring.chat.service.ChatRoomService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.Map;

@Configuration
public class ChatRoomSocketConfig {

    private final ChatRoomService chatRoomService;

    public ChatRoomSocketConfig(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, WebSocketHandler> roomServiceMap = Map.of(
                "/chat", chatRoomService
        );
        // order -1 to ensure this mapping is checked before any other mappings (controllers, etc.)
        return new SimpleUrlHandlerMapping(roomServiceMap, -1);
    }
}
