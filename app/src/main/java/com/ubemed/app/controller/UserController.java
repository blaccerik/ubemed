package com.ubemed.app.controller;

import com.ubemed.app.config.JwtTokenUtil;
import com.ubemed.app.model.JwtRequest;
import com.ubemed.app.model.JwtResponse;
import com.ubemed.app.service.JwtUserDetailsService;
import com.ubemed.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private UserService userService;


    @GetMapping("/check")
    public String check(
      @RequestHeader(name="Authorization") String token
    ) {
        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        return username;
    }

    @GetMapping("/coins")
    public long coins(
            @RequestHeader(name="Authorization") String token
    ) {
        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        return userService.getCoins(username);
    }

    @PostMapping("/create")
    public boolean create(
    @RequestBody JwtRequest authenticationRequest
    ) {
        String name = authenticationRequest.getUsername();
        String pass = authenticationRequest.getPassword();
        if (name == null || pass == null) {
            return false;
        }
        return userService.save(name, pass);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
    @RequestBody JwtRequest authenticationRequest
    ) {
        String name = authenticationRequest.getUsername();
        String pass = authenticationRequest.getPassword();

        if (!userService.checkPass(name, pass)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Wrong name or pass");
        }
        final UserDetails userDetails;
        userDetails = userDetailsService.loadUserByUsername(name);
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
