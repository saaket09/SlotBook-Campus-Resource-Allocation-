package com.slotbook.resource.service;

import com.slotbook.resource.dto.AvailabilityResponseDTO;
import com.slotbook.resource.entity.Resource;
import com.slotbook.resource.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String BOOKING_SERVICE_URL = "http://localhost:8083/api/bookings";

    public List<Resource> getAllResources(String category) {
        if (category != null && !category.isEmpty()) {
            return resourceRepository.findByCategoryAndIsActiveTrue(category);
        }
        return resourceRepository.findByIsActiveTrue();
    }

    public Resource addResource(Resource resource) {
        return resourceRepository.save(resource);
    }

    public Resource getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalStateException("Resource not found with ID: " + resourceId));
    }

    public AvailabilityResponseDTO getAvailability(Long resourceId, LocalDate date) {
        Resource resource = getResourceById(resourceId);

        List<AvailabilityResponseDTO.BookedSlot> bookedSlots = new ArrayList<>();

        // Inter-service REST call to Booking Service (port 8083)
        try {
            String url = BOOKING_SERVICE_URL + "/resource/" + resourceId + "/slots?date=" + date;
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            if (response.getBody() != null) {
                for (Map<String, Object> item : response.getBody()) {
                    LocalTime start = LocalTime.parse((String) item.get("startTime"));
                    LocalTime end = LocalTime.parse((String) item.get("endTime"));
                    String bookedBy = (String) item.get("bookedBy");
                    bookedSlots.add(new AvailabilityResponseDTO.BookedSlot(start, end, bookedBy));
                }
            }
        } catch (Exception e) {
            // Log & fallback to empty if Booking Service is not yet populated
            System.out.println("Notice: Booking Service response: " + e.getMessage());
        }

        return new AvailabilityResponseDTO(resourceId, resource.getName(), date, bookedSlots);
    }
}
