package com.ubemed.app.controller;

import com.ubemed.app.config.JwtTokenUtil;
import com.ubemed.app.model.WheelEnter;
import com.ubemed.app.service.CasinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/casino")
@RestController
public class CasinoController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CasinoService casinoService;

    /**
     * -1 = error
     * nr = value of items
     */
    @PostMapping("/wheel")
    public long enter(
        @RequestHeader(name="Authorization") String token,
        @RequestBody WheelEnter wheelEnter
    ) {
        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));

        if (wheelEnter.getCoins() < 0) {
            return -1;
        }

        return casinoService.enter(username, wheelEnter.getItems(), wheelEnter.getCoins());
    }
}
