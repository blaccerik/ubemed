package com.ubemed.app.controller;

import com.ubemed.app.config.JwtTokenUtil;
import com.ubemed.app.model.BidResponse;
import com.ubemed.app.model.WheelData;
import com.ubemed.app.model.WheelEnter;
import com.ubemed.app.model.WheelEnterBroadcast;
import com.ubemed.app.service.CasinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequestMapping("/casino")
@RestController
public class CasinoController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CasinoService casinoService;

    @GetMapping("/data")
    public WheelData getData() {
        return casinoService.getData();
    }

    /**
     * -1 = error
     * nr = value of items
     */
    @PostMapping("/wheel")
    public long enter(
            HttpServletRequest httpRequest,
            @RequestHeader(name="Authorization") String token,
            @RequestBody WheelEnter wheelEnter
    ) {
        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));

        if (wheelEnter.getCoins() < 0) {
            return -1;
        }
        // sync if multiple requests
        String tranID = httpRequest.getParameter("tranID");
        synchronized (String.valueOf(tranID).intern()) {
            long value = casinoService.enter(username, wheelEnter.getItems(), wheelEnter.getCoins(), new Date());
            if (value > 0) {
                casinoService.update(username, wheelEnter.getCoins(), value);
            }
            return value;
        }
    }
}
