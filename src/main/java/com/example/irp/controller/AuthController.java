package com.example.irp.controller;


import com.example.irp.entity.Resource;
import com.example.irp.entity.User;
import com.example.irp.repository.ResourceRepository;
import com.example.irp.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @GetMapping("/home")
    public String home() {
        return "index";
    }



//    @GetMapping("/login")
//    public String loginPage() {
//        return "login";
//    }

//    @GetMapping("/register")
//    public String registerPage() {
//        return "register";
//    }



    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
//        return "redirect:/index";
        return "index";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String user_name,
                               @RequestParam String user_email,
                               @RequestParam String password,
//                               @RequestParam String role,
                               RedirectAttributes redirectAttributes ) {

        User user = new User();
//      Naye camelCase methods use karein:
        user.setUserName(user_name);
        user.setUserEmail(user_email);
        user.setUserPassword(password);
        user.setRole("USER");

        userRepository.save(user);


        redirectAttributes.addFlashAttribute("success", "Registration Successful!");
//        return "redirect:/?login=true";
        return "redirect:/?registered=true";
    }

    @PostMapping("/login")
    public String login(@RequestParam String user_email,
                        @RequestParam String password,
                        HttpSession session) {

        // Method name match karein jo Repository mein define kiya hai
        Optional<User> user = userRepository
                .findByUserEmailAndPassword(user_email, password);

        if (user.isEmpty()) {
            return "index";
        }

        // ✅ Database se username session me store
        session.setAttribute("userId", user.get().getUserId());
        session.setAttribute("username", user.get().getUserName());

        if (user.get().getRole().equalsIgnoreCase("admin")) {
            // ✅ YAHAN STORE KARNA HAI
            // ✅ Correct (use getUserId)
            session.setAttribute("userId", user.get().getUserId());
            return "redirect:/admin/dashboard";
        }

        return "redirect:/user/dashboard";
    }
}