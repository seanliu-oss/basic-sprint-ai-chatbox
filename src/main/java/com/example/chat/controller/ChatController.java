package com.example.chat.controller;

import com.example.chat.model.ChatMessage;
import com.example.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;


    @GetMapping("/")
    public String chat(Model model) {
        model.addAttribute("message", new ChatMessage());
        return "chat";
    }

    @PostMapping("/chat")
    public String chatSubmit(@ModelAttribute ChatMessage message, Model model) {
        String response = chatService.getResponse(message.getContent());
        model.addAttribute("response", response);
        model.addAttribute("message", new ChatMessage());
        return "chat";
    }

    @PostMapping("/new-chat")
    public String newChat() {
        chatService.clearConversation();
        return "redirect:/";
    }
}
