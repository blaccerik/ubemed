package com.ubemed.app.controller;

import com.ubemed.app.config.JwtTokenUtil;
import com.ubemed.app.model.JwtRequest;
import com.ubemed.app.model.JwtResponse;
import com.ubemed.app.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/users")
@RestController
public class UserController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;


    @GetMapping("/check")
    public String check(
            @RequestHeader(name="Authorization") String token
    ) {
        String jwtToken = token.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
        return username;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody JwtRequest authenticationRequest
    ) {
        String name = authenticationRequest.getUsername();
        String pass = authenticationRequest.getPassword();
        final UserDetails userDetails;

        // todo check pass
        userDetails = userDetailsService.loadUserByUsername(name);
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
