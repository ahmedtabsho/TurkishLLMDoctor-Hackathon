package com.backend.backend.dto;

import com.backend.backend.model.ChatMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private ChatMessage message;
    private boolean success;
    private String errorMessage;
}
