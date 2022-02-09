package com.ubemed.app.controller;

import com.ubemed.app.config.JwtTokenUtil;
import com.ubemed.app.dbmodel.DBStoreImage;
import com.ubemed.app.model.Post;
import com.ubemed.app.model.Product;
import com.ubemed.app.service.StoreService;
import com.ubemed.app.service.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RequestMapping("/store")
@RestController
public class StoreController {

    @Autowired
    StoreService storeService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping()
    public List<Product> getAll() {
        return storeService.getAll();
    }

    @PostMapping("/add")
    public boolean saveProduct(
            @RequestHeader(name="Authorization") String token,
            @RequestPart(value = "file") MultipartFile file,
            @RequestPart(value = "title") String title,
            @RequestPart(value = "cats") List<String> cats,
            // All numbers must be strings or else it wont work
            @RequestPart(value = "cost") String cost_string
    ) {

        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        long cost;
        try {
            cost = Long.parseLong(cost_string);
        } catch (NumberFormatException exception) {
           return false;
        }
        System.out.println(cats);
        return storeService.save(username, title, "e", cost, file);
    }

    @GetMapping("/get")
    public DBStoreImage getImage() {
        DBStoreImage dbStoreImage = new DBStoreImage();
        dbStoreImage.setPicByte(storeService.getAll().get(0).getPicByte());
        return dbStoreImage;

//        ByteArrayInputStream bis = new ByteArrayInputStream(decompressBytes(image.getPicByte()));
//        BufferedImage bImage2 = ImageIO.read(bis);
//        ImageIO.write(bImage2, "jpg", new File("output.jpg") );
//        System.out.println("image created");

//        return storeService.get(imageName);
    }
}
