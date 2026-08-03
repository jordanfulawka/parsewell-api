package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.entity.User;

import java.util.List;

public interface UserService {

    List<User> findAll();
    User addUser(User user);

}
