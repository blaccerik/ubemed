package com.ubemed.app.controller;

import com.ubemed.app.config.JwtTokenUtil;
import com.ubemed.app.dbmodel.DBStoreImage;
import com.ubemed.app.model.Product;
import com.ubemed.app.model.ProductImage;
import com.ubemed.app.repository.ImageRepository;
import com.ubemed.app.service.ImageService;
import com.ubemed.app.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequestMapping("/store")
@RestController
public class StoreController {
    private final int maxCategories = 3;
    private final int minCategories = 1;

    private final long maxCost = 10000;

    @Autowired
    private StoreService storeService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ImageService imageService;

    @GetMapping()
    public List<Product> getAll(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "filter", required = false) String filter
    ) {
        return storeService.getAll();
    }
//
//    @GetMapping()
//    public List<>
//




    @PostMapping("/add")
    public boolean saveProduct(
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
        return storeService.save(username, title, cats, cost, file);
    }

    @GetMapping("/{id}")
    public ProductImage getImage(
            @PathVariable Long id
    ) throws InterruptedException {
        return imageService.get(id);
    }
}
