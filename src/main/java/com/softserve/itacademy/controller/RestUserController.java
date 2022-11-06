package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.Role;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;


@RestController
@RequestMapping("/api/users")
public class RestUserController {

    private final UserService userService;
    private Map<Object, Object> result;

    public RestUserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody Map<String,String> user) {
        try {
            User newUser = new User();
            newUser.setId(Long.parseLong(user.get("id")));
            newUser.setFirstName(user.get("firstName"));
            newUser.setLastName(user.get("lastName"));
            newUser.setEmail(user.get("email"));
            newUser.setPassword(user.get("password"));
            return getResponseEntity(userService.create(newUser), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("User not create");
        }
    }

    @RequestMapping(value = "/{id}/read", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity read(@PathVariable long id) {
        try {
            User user = userService.readById(id);
            return getResponseEntity(user, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("User not found");
        }
    }


    @RequestMapping(value = "/{id}/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity update(@PathVariable long id, @RequestBody User user) {
        try {
            User oldUser = userService.readById(id);
            oldUser.setRole(user.getRole());
            oldUser.setEmail(user.getEmail());
            oldUser.setPassword(user.getPassword());
            userService.update(oldUser);
            return getResponseEntity(oldUser, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("User not update");
        }
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity delete(@PathVariable("id") long id) {
        try {
            userService.delete(id);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("User not delete");
        }
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
            return ResponseEntity.badRequest().body("User not delete");
        }
    }

    private ResponseEntity getResponseEntity(User user, HttpStatus httpStatus) {
        result = new HashMap<>();
        result.put("id", user.getId());
        result.put("firstName", user.getFirstName());
        result.put("lastName", user.getLastName());
        result.put("email", user.getEmail());
        return new ResponseEntity(result, httpStatus);
    }
}
