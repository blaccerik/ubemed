package com.ubemed.app.controller;

import com.ubemed.app.config.JwtTokenUtil;
import com.ubemed.app.model.Product;
import com.ubemed.app.repository.ProductRepository;
import com.ubemed.app.service.ProductService;
import com.ubemed.app.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/products")
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping()
    public List<Product> getAll(
            @RequestHeader(name="Authorization") String token,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "filter", required = false) String filter
    ) {

        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));

        return productService.getAll(username);
    }

}
