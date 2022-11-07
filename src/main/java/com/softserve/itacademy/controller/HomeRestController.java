package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class HomeRestController {
    private final UserService userService;
    public HomeRestController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/api/home", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAll() {
        try {
            List<Map<Object, Object>> users = new ArrayList<>();
            Map<Object, Object> user;
            for (User oneOfUsers : userService.getAll()) {
                user = new HashMap<>();
                user.put("id", oneOfUsers.getId());
                user.put("firstName", oneOfUsers.getFirstName());
                user.put("lastName", oneOfUsers.getLastName());
                user.put("email", oneOfUsers.getEmail());
                users.add(user);
            }
            return new ResponseEntity(users, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Can't get all users");
        }
    }


}
