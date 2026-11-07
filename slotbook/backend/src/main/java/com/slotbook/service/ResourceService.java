package com.slotbook.service;

import com.slotbook.dto.AvailabilityResponseDTO;
import com.slotbook.entity.Booking;
import com.slotbook.entity.Resource;
import com.slotbook.repository.BookingRepository;
import com.slotbook.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private BookingRepository bookingRepository;

    /**
     * Get all active resources, optionally filtered by category.
     */
    public List<Resource> getAllResources(String category) {
        if (category != null && !category.isEmpty()) {
            return resourceRepository.findByCategoryAndIsActiveTrue(category);
        }
        return resourceRepository.findByIsActiveTrue();
    }

    /**
     * Get availability for a resource on a specific date.
     * Returns the resource info and list of booked time slots.
     */
    public AvailabilityResponseDTO getAvailability(Long resourceId, LocalDate date) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalStateException("Resource not found with ID: " + resourceId));

        List<Booking> bookings = bookingRepository.findByResourceAndDate(resourceId, date);

        List<AvailabilityResponseDTO.BookedSlot> bookedSlots = bookings.stream()
                .map(b -> new AvailabilityResponseDTO.BookedSlot(
                        b.getStartTime(),
                        b.getEndTime(),
                        b.getUser().getName()))
                .collect(Collectors.toList());

        return new AvailabilityResponseDTO(resourceId, resource.getName(), date, bookedSlots);
    }

    /**
     * Add a new resource (admin operation).
     */
    public Resource addResource(Resource resource) {
        return resourceRepository.save(resource);
    }

    /**
     * Get a resource by ID.
     */
    public Resource getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalStateException("Resource not found with ID: " + resourceId));
    }
}
