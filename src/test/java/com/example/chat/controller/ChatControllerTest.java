package com.example.chat.controller;

import com.example.chat.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the ChatController.
 * Using @WebMvcTest to focus only on the web layer and mock the service layer.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    ChatService chatService;

    @Test
    void shouldMaintainSeparateConversationsAndHandleSessionClearing() throws Exception {
        // --- Setup: Define two separate user sessions and their data ---
        var session1 = new MockHttpSession();
        String session1Id = session1.getId();
        String user1Name = "xyz";

        var session2 = new MockHttpSession();
        String user2Name = "abcdefg";

        // Step 1: Start session 1 and tell the AI its name
        String user1Intro = "my name is " + user1Name;
        mockMvc.perform(post("/chat").session(session1).param("content", user1Intro))
                .andExpect(status().isOk());

        // Step 2: Start session 2 and tell the AI its name
        String user2Intro = "my name is " + user2Name;
        mockMvc.perform(post("/chat").session(session2).param("content", user2Intro))
                .andExpect(status().isOk());

        // --- Verification ---
        String question = "what is my name";

        // Step 3: In session 1, ask for the name and expect "xyz"

        mockMvc.perform(post("/chat").session(session1).param("content", question))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(user1Name)));

        // Step 4: In session 2, ask for the name and expect "abcdefg"

        mockMvc.perform(post("/chat").session(session2).param("content", question))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(user2Name)));

        // Step 5: In session 1, click "New Chat" button

        mockMvc.perform(post("/new-chat").session(session1))
                .andExpect(status().is3xxRedirection()) // Expect a redirect
                .andExpect(redirectedUrl("/"));

        verify(chatService).clearConversation(session1Id); // Verify the service method was called

        // Step 6: In session 1, ask for the name again and expect it to be forgotten

        mockMvc.perform(post("/chat").session(session1).param("content", question))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(user1Name))));

        // Step 7: In session 2, ask for the name again and confirm it's still remembered
        // The mock from step 4 is still valid and will be used here.
        mockMvc.perform(post("/chat").session(session2).param("content", question))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(user2Name)));
    }
}