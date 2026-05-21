package com.example.irp.controller;

import com.example.irp.entity.Allocation;
import com.example.irp.repository.AllocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // 1. Ye import add karein
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/verify")
public class UserVerificationController {

    @Autowired
    private AllocationRepository allocationRepository;

    @GetMapping("/{action}/{id}")
    @Transactional // 2. Ye annotation zaroor lagayein
    public String verify(@PathVariable String action, @PathVariable int id) {
        Allocation alloc = allocationRepository.findById(id).orElse(null);

        if (alloc != null) {
            if ("yes".equalsIgnoreCase(action)) {
                alloc.setStatus("ISSUED");
            } else {
                alloc.setStatus("REJECTED_BY_USER");
            }
            allocationRepository.save(alloc);
            // 3. Optional: Console mein check karne ke liye
            System.out.println("Status updated for ID " + id + " to " + alloc.getStatus());
        }

        // 4. Testing ke liye admin page par redirect karein taaki aap status live dekh sakein
        return "redirect:/admin/admin-request";
    }
}