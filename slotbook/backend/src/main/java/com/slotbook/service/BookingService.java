package com.slotbook.service;

import com.slotbook.dto.BookingRequestDTO;
import com.slotbook.entity.Booking;
import com.slotbook.entity.Resource;
import com.slotbook.entity.User;
import com.slotbook.repository.BookingRepository;
import com.slotbook.repository.ResourceRepository;
import com.slotbook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new booking with conflict detection (spec §5).
     * Two time ranges overlap if: existing.start < new.end AND existing.end > new.start
     */
    public Booking createBooking(BookingRequestDTO request) {
        // Check for time-slot conflicts
        List<Booking> conflicts = bookingRepository.findConflicts(
                request.getResourceId(),
                request.getBookingDate(),
                request.getStartTime(),
                request.getEndTime());

        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Slot already booked — choose another time.");
        }

        // Validate resource exists
        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new IllegalStateException("Resource not found with ID: " + request.getResourceId()));

        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found with ID: " + request.getUserId()));

        // Build and save the booking
        Booking booking = new Booking();
        booking.setResource(resource);
        booking.setUser(user);
        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setStatus("CONFIRMED");

        return bookingRepository.save(booking);
    }

    /**
     * Get all bookings for a specific user.
     */
    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserUserId(userId);
    }

    /**
     * Cancel a booking by setting its status to CANCELLED.
     */
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalStateException("Booking not found with ID: " + bookingId));

        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }
}
