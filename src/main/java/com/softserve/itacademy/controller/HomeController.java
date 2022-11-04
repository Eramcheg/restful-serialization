package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.security.CustomUserDetails;
import com.softserve.itacademy.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class HomeController {
    private final UserService userService;
    public HomeController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping({"/", "home"})
    public String home(Model model) {
        List<User> users;
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (customUserDetails.getAuthorities().stream().anyMatch(u -> u.getAuthority().equals("ROLE_ADMIN"))) {
            users = userService.getAll();
        } else {
            users = Collections.singletonList(userService.readById(customUserDetails.getId()));
        }

        model.addAttribute("users", users);
        return "home";
    }


}
