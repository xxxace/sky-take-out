package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@Slf4j
public class WebSocketConfiguration {

    @Bean
    public ServerEndpointExporter ServerEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
