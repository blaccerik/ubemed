package com.ubemed.app.controller;

import com.ubemed.app.config.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin")
@RestController
public class AdminController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping()
    public String test(
        @RequestHeader(name="Authorization") String token
    ) {
        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        return username;
    }
}
