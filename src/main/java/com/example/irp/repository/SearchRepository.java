package com.example.irp.repository;

import com.example.irp.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Resource, Integer> {

    // Native-friendly JPQL query to search resource_name or cast the resource_id to string for partial matches
    @Query("SELECT r FROM Resource r WHERE LOWER(r.resource_name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR CAST(r.resource_id AS string) LIKE CONCAT('%', :keyword, '%')")
    List<Resource> searchByNameOrId(@Param("keyword") String keyword);
}