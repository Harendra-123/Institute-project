package com.example.irp.controller;

import com.example.irp.entity.Allocation; // Import add kiya
import com.example.irp.entity.Resource;
import com.example.irp.repository.AllocationRepository;
import com.example.irp.repository.ResourceRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired private ResourceRepository resourceRepository;
    @Autowired private AllocationRepository allocationRepository;

    // 1. Dashboard Mapping
    @RequestMapping(value = {"/dashboard", "/main-dashboard"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userDashboard(@RequestParam(value = "search", required = false) String search, HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) return "redirect:/login";

        model.addAttribute("username", session.getAttribute("username"));
        List<Resource> resources = (search != null && !search.trim().isEmpty())
                ? resourceRepository.searchResources(search.trim())
                : resourceRepository.findAll();

        LocalDateTime now = LocalDateTime.now();
        for (Resource res : resources) {
            int bookedQty = allocationRepository.getBookedQuantityByResourceId(res.getResource_id());
            res.setAvailableQuantity(Math.max(0, res.getQuantity() - bookedQty));
            if (!"Maintenance".equalsIgnoreCase(res.getStatus())) {
                res.setStatus(allocationRepository.countActiveBookingsNow(res.getResource_id(), now) > 0 ? "Not Available" : "Available");
            }
        }
        model.addAttribute("resources", resources);
        return "user-dashboard";
    }

    // 2. My Requests Mapping (FIXED: Data Fetching)
    @GetMapping("/my-requests")
    public String myRequests(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        // Yahan database se data fetch karke model mein daala gaya hai
        List<Allocation> myRequests = allocationRepository.findByUserId(userId);
        model.addAttribute("requests", myRequests);

        return "my-requests";
    }

    // 3. Request Resource Form Display
    @GetMapping("/request-resource/{id}")
    public String requestResourceForm(@PathVariable("id") int id, Model model, HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        model.addAttribute("resourceId", id);
        return "request-resource";
    }

    // 4. Handle Form Submission (FIXED: Data Saving)
    @PostMapping("/request")
    public String handleResourceRequest(
            @RequestParam("resource_id") int resourceId,
            @RequestParam("quantity") int quantity,
            @RequestParam("start_time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam("end_time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam("reason") String reason,
            HttpSession session, Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        if (endTime.isBefore(startTime)) {
            model.addAttribute("errorMessage", "End time cannot be before Start time!");
            model.addAttribute("resourceId", resourceId);
            return "request-resource";
        }

        // Database logic implement kiya
        Allocation newAllocation = new Allocation();
        newAllocation.setUserId(userId);
        newAllocation.setResourceId(resourceId);
        newAllocation.setQuantity(quantity);
        newAllocation.setStartTime(startTime);
        newAllocation.setEndTime(endTime);
        newAllocation.setReason(reason);
        newAllocation.setStatus("Pending");

        allocationRepository.save(newAllocation);

        return "redirect:/user/my-requests";
    }
}