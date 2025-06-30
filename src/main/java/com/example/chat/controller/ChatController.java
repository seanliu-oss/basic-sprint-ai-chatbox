package com.example.chat.controller;

import com.example.chat.service.ChatService;
import jakarta.servlet.http.HttpSession;
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
        return "chat";
    }

    @PostMapping("/chat")
    @ResponseBody
    public String chatSubmit(@RequestParam("content") String content,
                             HttpSession session) {
        String sessionId = session.getId();

        return chatService.getResponse(content, sessionId);
    }

    @PostMapping("/new-chat")
    public String newChat(HttpSession session) {
        String sessionId = session.getId();
        chatService.clearConversation(sessionId);
        return "redirect:/";
    }
}