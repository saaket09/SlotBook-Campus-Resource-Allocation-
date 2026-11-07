package com.slotbook.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AvailabilityResponseDTO {

    private Long resourceId;
    private String resourceName;
    private LocalDate date;
    private List<BookedSlot> bookedSlots;

    // Default constructor
    public AvailabilityResponseDTO() {}

    public AvailabilityResponseDTO(Long resourceId, String resourceName, LocalDate date, List<BookedSlot> bookedSlots) {
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.date = date;
        this.bookedSlots = bookedSlots;
    }

    // Getters and Setters
    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<BookedSlot> getBookedSlots() {
        return bookedSlots;
    }

    public void setBookedSlots(List<BookedSlot> bookedSlots) {
        this.bookedSlots = bookedSlots;
    }

    /**
     * Inner class representing a single booked time slot.
     */
    public static class BookedSlot {
        private LocalTime startTime;
        private LocalTime endTime;
        private String bookedBy;

        public BookedSlot() {}

        public BookedSlot(LocalTime startTime, LocalTime endTime, String bookedBy) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.bookedBy = bookedBy;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalTime startTime) {
            this.startTime = startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalTime endTime) {
            this.endTime = endTime;
        }

        public String getBookedBy() {
            return bookedBy;
        }

        public void setBookedBy(String bookedBy) {
            this.bookedBy = bookedBy;
        }
    }
}
