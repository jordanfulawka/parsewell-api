package com.jordanfulawka.parsewell.rest;

import com.jordanfulawka.parsewell.entity.User;
import com.jordanfulawka.parsewell.repository.UserRepository;
import com.jordanfulawka.parsewell.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestController {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;

    @Autowired
    public AuthenticationRestController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signin")
    public String authenticateUser(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );
        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User foundUser = userRepository.findByEmail(user.getEmail());
        return jwtUtil.generateToken(foundUser.getId(), foundUser.getFirstName(), foundUser.getEmail());
    }

    @PostMapping("/signup")
    public String registerUser(@RequestBody User user) {
        if(userRepository.existsByEmail(user.getEmail())) {
            return "User already exists";
        }

        final User newUser = new User(
                user.getFirstName(),
                user.getEmail(),
                passwordEncoder.encode(user.getPassword())
        );
        userRepository.save(newUser);
        return "User registered successfully!";
    }
}
