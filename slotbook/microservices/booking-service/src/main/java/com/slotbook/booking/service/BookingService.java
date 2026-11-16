package com.slotbook.booking.service;

import com.slotbook.booking.dto.BookingRequestDTO;
import com.slotbook.booking.entity.Booking;
import com.slotbook.booking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String RESOURCE_SERVICE_URL = "http://localhost:8082/api/resources";
    private static final String USER_SERVICE_URL = "http://localhost:8081/api/users";

    public Booking createBooking(BookingRequestDTO request) {
        // 1. Conflict detection
        List<Booking> conflicts = bookingRepository.findConflicts(
                request.getResourceId(),
                request.getBookingDate(),
                request.getStartTime(),
                request.getEndTime());

        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Slot already booked — choose another time.");
        }

        // 2. Fetch resource details from Resource Service (Port 8082)
        String resourceName = "Resource #" + request.getResourceId();
        String resourceCategory = "GENERAL";
        String resourceLocation = "";

        try {
            Map<?, ?> resMap = restTemplate.getForObject(RESOURCE_SERVICE_URL + "/" + request.getResourceId(), Map.class);
            if (resMap != null) {
                if (resMap.get("name") != null) resourceName = (String) resMap.get("name");
                if (resMap.get("category") != null) resourceCategory = (String) resMap.get("category");
                if (resMap.get("location") != null) resourceLocation = (String) resMap.get("location");
            }
        } catch (Exception e) {
            System.out.println("Warning: Resource Service call failed: " + e.getMessage());
        }

        // 3. Fetch user details from User Service (Port 8081)
        String userName = "User #" + request.getUserId();
        try {
            Map<?, ?> userMap = restTemplate.getForObject(USER_SERVICE_URL + "/" + request.getUserId(), Map.class);
            if (userMap != null && userMap.get("name") != null) {
                userName = (String) userMap.get("name");
            }
        } catch (Exception e) {
            System.out.println("Warning: User Service call failed: " + e.getMessage());
        }

        // 4. Save booking
        Booking booking = new Booking();
        booking.setResourceId(request.getResourceId());
        booking.setResourceName(resourceName);
        booking.setResourceCategory(resourceCategory);
        booking.setResourceLocation(resourceLocation);
        booking.setUserId(request.getUserId());
        booking.setUserName(userName);
        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setStatus("CONFIRMED");

        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalStateException("Booking not found with ID: " + bookingId));
        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }

    public List<Map<String, Object>> getSlotsForResource(Long resourceId, LocalDate date) {
        List<Booking> bookings = bookingRepository.findByResourceIdAndBookingDateAndStatus(resourceId, date, "CONFIRMED");
        List<Map<String, Object>> result = new ArrayList<>();
        for (Booking b : bookings) {
            Map<String, Object> map = new HashMap<>();
            map.put("startTime", b.getStartTime().toString());
            map.put("endTime", b.getEndTime().toString());
            map.put("bookedBy", b.getUserName());
            result.add(map);
        }
        return result;
    }
}
