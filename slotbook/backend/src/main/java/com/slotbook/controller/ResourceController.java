package com.slotbook.controller;

import com.slotbook.dto.AvailabilityResponseDTO;
import com.slotbook.entity.Resource;
import com.slotbook.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    /**
     * GET /api/resources — List all active resources.
     * Optional query param: ?category=HALL|SPORTS|LAB|EQUIPMENT
     */
    @GetMapping
    public ResponseEntity<List<Resource>> getAllResources(
            @RequestParam(required = false) String category) {
        List<Resource> resources = resourceService.getAllResources(category);
        return ResponseEntity.ok(resources);
    }

    /**
     * GET /api/resources/{id}/availability?date=2026-08-15
     * Get booked slots for a resource on a given date.
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponseDTO> getAvailability(
            @PathVariable("id") Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        AvailabilityResponseDTO availability = resourceService.getAvailability(resourceId, date);
        return ResponseEntity.ok(availability);
    }

    /**
     * POST /api/resources — (Admin) Add a new resource.
     */
    @PostMapping
    public ResponseEntity<Resource> addResource(@RequestBody Resource resource) {
        Resource saved = resourceService.addResource(resource);
        return ResponseEntity.ok(saved);
    }
}
