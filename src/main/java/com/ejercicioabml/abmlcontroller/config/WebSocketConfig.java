package com.ejercicioabml.abmlcontroller.config;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint al que se conecta Angular
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefijo para los mensajes que envía el servidor
        registry.enableSimpleBroker("/topic");
        // Prefijo para los mensajes que envía el cliente
        registry.setApplicationDestinationPrefixes("/app");
    }
}
