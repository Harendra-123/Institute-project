package com.example.irp.controller;

import com.example.irp.entity.Resource;
import com.example.irp.repository.SearchRepository;
import com.example.irp.repository.AllocationRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
@RequestMapping("/user") // Class level mapping
public class SearchController {

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private AllocationRepository allocationRepository;

    // ✅ FIXED: Mapping unique kar di (URL: /user/search)
    @GetMapping("/search")
    public String searchDashboard(@RequestParam(value = "search", required = false) String search,
                                  HttpSession session,
                                  Model model) {

        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }

        List<Resource> resources = (search != null && !search.trim().isEmpty())
                ? searchRepository.searchByNameOrId(search.trim())
                : searchRepository.findAll();

        // Live Stock calculation
        for (Resource res : resources) {
            int bookedQty = allocationRepository.getBookedQuantityByResourceId(res.getResource_id());
            res.setAvailableQuantity(Math.max(0, res.getQuantity() - bookedQty));
        }

        model.addAttribute("resources", resources);
        model.addAttribute("keyword", search);
        return "user-dashboard";
    }
}