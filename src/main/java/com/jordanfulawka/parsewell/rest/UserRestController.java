package com.jordanfulawka.parsewell.rest;


import com.jordanfulawka.parsewell.entity.User;
import com.jordanfulawka.parsewell.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    private UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("")
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }
}
