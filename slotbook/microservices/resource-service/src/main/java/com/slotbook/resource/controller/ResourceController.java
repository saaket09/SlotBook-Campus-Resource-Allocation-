package com.slotbook.resource.controller;

import com.slotbook.resource.dto.AvailabilityResponseDTO;
import com.slotbook.resource.entity.Resource;
import com.slotbook.resource.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resources")
@CrossOrigin(origins = "*")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @GetMapping
    public ResponseEntity<List<Resource>> getAllResources(
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(resourceService.getAllResources(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getResourceById(@PathVariable Long id) {
        return ResponseEntity.ok(resourceService.getResourceById(id));
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponseDTO> getAvailability(
            @PathVariable("id") Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(resourceService.getAvailability(resourceId, date));
    }

    @PostMapping
    public ResponseEntity<?> addResource(@RequestBody Resource resource) {
        try {
            Resource saved = resourceService.addResource(resource);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
