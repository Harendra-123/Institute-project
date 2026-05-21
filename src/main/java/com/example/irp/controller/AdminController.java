package com.example.irp.controller;

import com.example.irp.entity.Allocation;
import com.example.irp.entity.Resource;
import com.example.irp.repository.AllocationRepository;
import com.example.irp.repository.ResourceRepository;
import com.example.irp.repository.UserRepository;
import com.example.irp.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AllocationRepository allocationRepository;

    @Autowired
    private EmailService emailService;

    // 1. Admin Analytics Dashboard (Fixed: Correct Live Stock tracking & Status overrides)
    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username != null ? username : "Administrator");

        List<Resource> resources = resourceRepository.findAll();

        long availableResources = 0;
        long notAvailableResources = 0;

        for (Resource res : resources) {
            // ⭐ Loop chalakar individual strict live quantity nikalein
            int bookedQty = allocationRepository.getBookedQuantityByResourceId(res.getResource_id());
            int calculatedAvailable = res.getQuantity() - bookedQty;
            res.setAvailableQuantity(calculatedAvailable < 0 ? 0 : calculatedAvailable);

            // Dynamic dashboard counter status synchronization
            if ("Maintenance".equalsIgnoreCase(res.getStatus())) {
                notAvailableResources++;
            } else if (res.getAvailableQuantity() <= 0) {
                res.setStatus("Not Available"); // Stock khatam hone par hi change hoga
                notAvailableResources++;
            } else {
                res.setStatus("Available");
                availableResources++;
            }
        }

        long totalResources = resourceRepository.count();
        long totalUsers = userRepository.count();

        model.addAttribute("resources", resources);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalResources", totalResources);
        model.addAttribute("availableResources", availableResources);
        model.addAttribute("notAvailableResources", notAvailableResources);

        return "admin-dashboard";
    }

    // Page open Form
    @GetMapping("/add-resources")
    public String addResourcePage() {
        return "add-resources";
    }

    // 2. Admin Resource Inventory Main Dashboard (Fixed)
    @GetMapping("/dashboardmain")
    public String dashboardmain(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username != null ? username : "Administrator");

        List<Resource> resources = resourceRepository.findAll();

        long availableResources = 0;
        long notAvailableResources = 0;

        for (Resource res : resources) {
            // ⭐ Strict target standard query implementation
            int bookedQty = allocationRepository.getBookedQuantityByResourceId(res.getResource_id());
            int calculatedAvailable = res.getQuantity() - bookedQty;
            res.setAvailableQuantity(calculatedAvailable < 0 ? 0 : calculatedAvailable);

            if ("Maintenance".equalsIgnoreCase(res.getStatus())) {
                notAvailableResources++;
            } else if (res.getAvailableQuantity() <= 0) {
                res.setStatus("Not Available");
                notAvailableResources++;
            } else {
                res.setStatus("Available");
                availableResources++;
            }
        }

        long totalResources = resourceRepository.count();
        long totalUsers = userRepository.count();

        model.addAttribute("resources", resources);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalResources", totalResources);
        model.addAttribute("availableResources", availableResources);
        model.addAttribute("notAvailableResources", notAvailableResources);

        return "dashboardmain";
    }

    @GetMapping("/edit-resource")
    public String editResource(@RequestParam("resource_id") int resource_id, Model model) {
        Resource resource = resourceRepository.findById((long) resource_id).orElse(null);
        model.addAttribute("resource", resource);
        return "edit-resource";
    }

    @GetMapping("/delete-resource")
    public String deleteResource(@RequestParam("resource_id") int resource_id) {
        resourceRepository.deleteById((long) resource_id);
        return "redirect:/admin/dashboardmain";
    }

    @PostMapping("/add-resources")
    public String saveResource(@RequestParam String resource_name,
                               @RequestParam String type,
                               @RequestParam int quantity,
                               @RequestParam String status,
                               @RequestParam String location) {
        Resource resource = new Resource();
        resource.setResource_name(resource_name);
        resource.setType(type);
        resource.setQuantity(quantity);
        resource.setStatus(status);
        resource.setLocation(location);

        resourceRepository.save(resource);
        return "redirect:/admin/dashboardmain";
    }

    @PostMapping("/update-resource")
    public String updateResource(@RequestParam int resource_id,
                                 @RequestParam String resource_name,
                                 @RequestParam String type,
                                 @RequestParam int quantity,
                                 @RequestParam String status,
                                 @RequestParam String location) {
        Resource resource = resourceRepository.findById((long) resource_id).orElse(null);
        if (resource != null) {
            resource.setResource_name(resource_name);
            resource.setType(type);
            resource.setQuantity(quantity);
            resource.setStatus(status);
            resource.setLocation(location);
            resourceRepository.save(resource);
        }
        return "redirect:/admin/dashboardmain";
    }

    @GetMapping("/admin-request")
    public String showMyRequests(HttpSession session, Model model) {
        Object sessionUserId = session.getAttribute("userId");
        if (sessionUserId == null) {
            return "redirect:/";
        }

        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username != null ? username : "Administrator");

        List<Allocation> requests = allocationRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("requests", requests);

        return "admin-request";
    }
    @PostMapping("/request/approve")
    public String approveRequest(@RequestParam("id") int id) {
        Allocation allocation = allocationRepository.findById(id).orElse(null);
        if (allocation != null) {
            // 1. Move to the new pending state
            allocation.setStatus("APPROVED_PENDING_DELIVERY");
            allocationRepository.save(allocation);

            // 2. Trigger the new verification email
            if (allocation.getUser() != null) {
                emailService.sendConfirmationEmail(
                        allocation.getUser().getUserEmail(),
                        allocation.getUserName(),
                        allocation.getResourceName(),
                        allocation.getId()
                );
            }
        }
        return "redirect:/admin/admin-request";
    }
}