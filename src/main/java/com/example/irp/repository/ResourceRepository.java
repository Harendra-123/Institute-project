package com.example.irp.repository;

import com.example.irp.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    // 🔹 Naya Search Method: Yeh name ya type dono me se kisi se bhi search karega
    @Query("SELECT r FROM Resource r WHERE LOWER(r.resource_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.type) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Resource> searchResources(@Param("keyword") String keyword);
}