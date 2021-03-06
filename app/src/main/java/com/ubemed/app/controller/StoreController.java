package com.ubemed.app.controller;

import com.ubemed.app.config.JwtTokenUtil;
import com.ubemed.app.model.Bid;
import com.ubemed.app.model.BidResponse;
import com.ubemed.app.model.Product;
import com.ubemed.app.model.ProductImage;
import com.ubemed.app.model.User;
import com.ubemed.app.model.UserResponse;
import com.ubemed.app.service.ImageService;
import com.ubemed.app.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.messaging.handler.annotation.MessageMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RequestMapping("/store")
@RestController
public class StoreController {
    private static final int maxCategories = 3;
    private static final int minCategories = 1;
    private static final long maxCost = 10000;

    @Autowired
    private StoreService storeService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ImageService imageService;

    @Autowired
    SimpMessagingTemplate template;

    @GetMapping()
    public List<Product> getAll(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "search", required = false) String search
    ) {
        return storeService.getAll(page, filter, search);
    }


    @PostMapping("/add")
    public boolean saveProduct(HttpServletRequest httpRequest,
            @RequestHeader(name="Authorization") String token,
            @RequestPart(value = "file") MultipartFile file,
            @RequestPart(value = "title") String title,
            @RequestPart(value = "cats") String cats_string,
            // All numbers must be strings or else it wont work
            @RequestPart(value = "cost") String cost_string
    ) {

        List<String> list = Arrays.asList(cats_string.split(","));
        if (list.size() > maxCategories || list.size() < minCategories) {
            return false;
        }
        List<Long> cats = new ArrayList<>();
        for (String string : list) {
            try {
                long aLong = Long.parseLong(string);
                if (cats.contains(aLong)) {
                    return false;
                }
                cats.add(aLong);
            } catch (NumberFormatException exception) {
                return false;
            }
        }

        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        long cost;
        try {
            cost = Long.parseLong(cost_string);
            if (cost > maxCost || cost < 1) {
                return false;
            }
        } catch (NumberFormatException exception) {
           return false;
        }

        // sync if multiple requests
        String tranID = httpRequest.getParameter("tranID");
        synchronized (String.valueOf(tranID).intern()) {
            return storeService.save(username, title, cats, cost, file, new Date(), StoreService.strategy.normal);
        }
    }

    @GetMapping("/{id}/image")
    public ProductImage getImage(
            @PathVariable Long id
    ) {
        return imageService.get(id);
    }

    @PutMapping("/{id}")
    public boolean sell(
        HttpServletRequest httpRequest,
        @RequestHeader(name="Authorization") String token,
        @PathVariable long id,
        @RequestBody Bid bid
    ) {
        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));

        long amount = bid.getAmount();
        if (amount <= 0 || amount > maxCost) {
            return false;
        }

        String tranID = httpRequest.getParameter("tranID");
        synchronized (String.valueOf(tranID).intern()) {
            return storeService.sell(username, id, amount, new Date());
        }
    }

//    @GetMapping("/{id}/bids")
//    public List<BidResponse> getBids(
//            @PathVariable Long id
//    ) {
//        return storeService.getBids(id);
//    }

    @PostMapping("/{id}")
    public boolean makeBid(
            HttpServletRequest httpRequest,
            @RequestHeader(name="Authorization") String token,
            @PathVariable long id,
            @RequestBody Bid bid
    ) {
        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));

        String tranID = httpRequest.getParameter("tranID");
        synchronized (String.valueOf(tranID).intern()) {
            boolean value = storeService.makeBid(username, id, bid.getAmount());
            if (value) {
                updateBids(username, id, bid.getAmount());
            }
            return value;
        }
    }

    private void updateBids(String username, long id, long amount) {
        template.convertAndSend("/bids", new BidResponse(username, id, amount));
    }
}
