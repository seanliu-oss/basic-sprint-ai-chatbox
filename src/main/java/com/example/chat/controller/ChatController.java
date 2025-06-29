package com.example.chat.controller;

import com.example.chat.model.ChatMessage;
import com.example.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/")
    public String chat(Model model) {
        // This correctly sets up the initial page load
        model.addAttribute("message", new ChatMessage());
        return "chat";
    }

    /**
     * Handles the AJAX request from the JavaScript.
     * @ResponseBody ensures the String response is sent back as the body,
     * not interpreted as a view name.
     * @RequestParam("content") binds the 'content' parameter from the
     * form-urlencoded request body to the method parameter.
     */
    @PostMapping("/chat")
    @ResponseBody
    public String chatSubmit(@RequestParam("content") String content) {
        return chatService.getResponse(content);
    }

    @PostMapping("/new-chat")
    public String newChat() {
        chatService.clearConversation();
        return "redirect:/";
    }
}