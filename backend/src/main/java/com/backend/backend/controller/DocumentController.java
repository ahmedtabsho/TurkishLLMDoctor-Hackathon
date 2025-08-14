package com.backend.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.backend.backend.dto.DocumentRequest;
import com.backend.backend.dto.DocumentResponse;
import com.backend.backend.service.OpenAIService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.HEAD, RequestMethod.PATCH}, exposedHeaders = "*", maxAge = 3600) // Allow all requests from any origin
public class DocumentController {

    @Autowired
    private OpenAIService openAIService;
    
    @PostMapping("/documents")
    public ResponseEntity<DocumentResponse> getDocuments(@RequestBody DocumentRequest request) {
        if (request.getDisease() == null || request.getDisease().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                DocumentResponse.builder()
                    .success(false)
                    .error("Disease parameter is required")
                    .build()
            );
        }
        
        DocumentResponse response = openAIService.generateDocuments(request.getDisease());
        return ResponseEntity.ok(response);
    }
}
