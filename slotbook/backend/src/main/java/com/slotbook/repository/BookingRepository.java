package com.slotbook.repository;

import com.slotbook.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Core conflict-detection query (spec §5).
     * Two time ranges overlap if: existing.start < new.end AND existing.end > new.start
     */
    @Query("SELECT b FROM Booking b WHERE b.resource.resourceId = :resourceId " +
           "AND b.bookingDate = :date AND b.status = 'CONFIRMED' " +
           "AND b.startTime < :endTime AND b.endTime > :startTime")
    List<Booking> findConflicts(@Param("resourceId") Long resourceId,
                                @Param("date") LocalDate date,
                                @Param("startTime") LocalTime startTime,
                                @Param("endTime") LocalTime endTime);

    /**
     * Get all bookings for a specific user.
     */
    List<Booking> findByUserUserId(Long userId);

    /**
     * Get all confirmed bookings for a resource on a specific date (for availability view).
     */
    @Query("SELECT b FROM Booking b WHERE b.resource.resourceId = :resourceId " +
           "AND b.bookingDate = :date AND b.status = 'CONFIRMED' " +
           "ORDER BY b.startTime")
    List<Booking> findByResourceAndDate(@Param("resourceId") Long resourceId,
                                        @Param("date") LocalDate date);
}
