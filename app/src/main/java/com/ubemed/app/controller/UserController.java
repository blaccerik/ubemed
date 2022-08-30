package com.ubemed.app.controller;

import com.ubemed.app.config.JwtTokenUtil;
import com.ubemed.app.dtomodel.JwtRequest;
import com.ubemed.app.dtomodel.JwtResponse;
import com.ubemed.app.dtomodel.UserData;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RequestMapping("/users")
@RestController
public class UserController {

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService userDetailsService;
    private final UserService userService;

    @Autowired
    public UserController(JwtTokenUtil jwtTokenUtil, JwtUserDetailsService userDetailsService, UserService userService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }


    @GetMapping("/check")
    public String check(
      @RequestHeader(name="Authorization") String token
    ) {
        return jwtTokenUtil.getUsernameFromToken(token.substring(7));
    }

    @GetMapping("/coins")
    public long coins(
            @RequestHeader(name="Authorization") String token
    ) {
        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        return userService.getCoins(username);
    }

    @GetMapping("/data")
    public UserData data(
            @RequestHeader(name="Authorization") String token
    ) {
        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        return userService.getData(username);
    }

    @PostMapping("/create")
    public boolean create(
            HttpServletRequest httpRequest,
        @RequestBody JwtRequest authenticationRequest
    ) {
        String name = authenticationRequest.getUsername();
        String pass = authenticationRequest.getPassword();
        if (name == null || pass == null) {
            return false;
        }
        String tranID = httpRequest.getParameter("tranID");
        synchronized (String.valueOf(tranID).intern()) {
            return userService.save(name, pass);
        }
    }

    @GetMapping("/claim")
    public boolean claim(@RequestHeader(name="Authorization") String token) {
        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        return userService.claim(username, new Date());
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
