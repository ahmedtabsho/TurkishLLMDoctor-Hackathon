package com.backend.backend.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String id;
    private String text;
    private String sender; // "user" or "bot"
    private LocalDateTime timestamp;
}
