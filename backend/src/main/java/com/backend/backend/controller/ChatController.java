package com.backend.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.backend.backend.dto.ChatRequest;
import com.backend.backend.dto.ChatResponse;
import com.backend.backend.model.ChatMessage;
import com.backend.backend.service.OpenAIService;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.HEAD, RequestMethod.PATCH}, exposedHeaders = "*", maxAge = 3600) // Allow all requests from any origin
public class ChatController {

    @Autowired
    private OpenAIService openAIService;
    
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            ChatMessage botResponse = openAIService.generateResponse(request.getMessage());
            return ResponseEntity.ok(new ChatResponse(botResponse, true, null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ChatResponse(null, false, e.getMessage()));
        }
    }
}
