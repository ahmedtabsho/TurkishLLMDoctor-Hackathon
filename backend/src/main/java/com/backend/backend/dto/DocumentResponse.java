package com.backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponse {
    private boolean success;
    private String disease;
    private List<Document> documents;
    private String error;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Document {
        private String title;
        private String description;
        private String link;
        private String source;
    }
}
