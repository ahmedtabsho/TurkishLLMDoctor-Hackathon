package com.backend.backend.controller;

import com.backend.backend.dto.GraphicsRequest;
import com.backend.backend.dto.GraphicsResponse;
import com.backend.backend.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.HEAD, RequestMethod.PATCH}, exposedHeaders = "*", maxAge = 3600) // Allow all requests from any origin
public class GraphicsController {

    private final OpenAIService openAIService;

    @Autowired
    public GraphicsController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping("/graphics")
    public ResponseEntity<GraphicsResponse> getGraphicsData(@RequestBody GraphicsRequest request) {
        try {
            GraphicsResponse response = openAIService.generateGraphicsData(request.getDisease());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            GraphicsResponse errorResponse = new GraphicsResponse();
            errorResponse.setSuccess(false);
            errorResponse.setError("Grafik verileri alınırken bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
