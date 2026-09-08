package com.zenika.zenikia.shared.infrastructure;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Single {@link ChatClient} bean shared by every AI-backed adapter
 * (cv, interview, assessment, communication, coaching infrastructure
 * packages). Keeps provider wiring in one place so swapping the underlying
 * {@link ChatModel} later only touches this class.
 */
@Configuration
class ChatClientConfig {

    @Bean
    ChatClient zenikiaChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
