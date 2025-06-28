package com.example.chat.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {
    private final ChatClient chatClient;
    private List<Message> conversationHistory = new ArrayList<>();

    @Autowired
    public ChatService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String getResponse(String userMessage) {
        // Add user message to conversation history
        conversationHistory.add(new UserMessage(userMessage));

        // Create prompt with conversation history
        Prompt prompt = new Prompt(conversationHistory);

        // Get AI response
        String response = chatClient.prompt(prompt).call().content();

        // Add AI response to conversation history
        conversationHistory.add(new org.springframework.ai.chat.messages.AssistantMessage(response));

        return response;
    }

    public void clearConversation() {
        conversationHistory.clear();
    }
}
