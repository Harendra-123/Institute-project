package com.example.irp.repository;

import com.example.irp.entity.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AllocationRepository extends JpaRepository<Allocation, Integer> {

    @Query("SELECT a FROM Allocation a WHERE a.userId = :userId")
    List<Allocation> findByUserId(@Param("userId") int userId);

    // ✅ UPDATED: Only count resources where the user has confirmed receipt (STATUS = 'ISSUED')
    // This prevents "fake" approvals from reducing your available inventory.
    @Query(value = "SELECT COALESCE(SUM(quantity), 0) FROM allocation WHERE resource_id = :resourceId AND status = 'ISSUED'", nativeQuery = true)
    int getBookedQuantityByResourceId(@Param("resourceId") int resourceId);

    // 🔹 UPDATED: Overlapping bookings should only consider active/confirmed allocations
    @Query(value = "SELECT COUNT(*) FROM allocation WHERE resource_id = :resourceId " +
            "AND status != 'Rejected' " +
            "AND status != 'REJECTED_BY_USER' " +
            "AND (:startTime < end_time AND :endTime > start_time)", nativeQuery = true)
    long countOverlappingBookings(@Param("resourceId") int resourceId,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);

    // 🔹 UPDATED: Active bookings now only include confirmed 'ISSUED' items
    @Query(value = "SELECT COUNT(*) FROM allocation WHERE resource_id = :resourceId " +
            "AND status = 'ISSUED' " +
            "AND (:now BETWEEN start_time AND end_time)", nativeQuery = true)
    long countActiveBookingsNow(@Param("resourceId") int resourceId, @Param("now") LocalDateTime now);
}