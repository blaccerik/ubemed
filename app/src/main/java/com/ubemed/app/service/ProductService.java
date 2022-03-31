package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.model.BidResponse;
import com.ubemed.app.model.Product;
import com.ubemed.app.repository.ProductRepository;
import com.ubemed.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Product> getAll(String username) {
        Optional<DBUser> optionalDBUser = userRepository.findByName(username);
        if (optionalDBUser.isEmpty()) {
            return new ArrayList<>();
        }

        DBUser dbUser = optionalDBUser.get();

        List<Product> list = new ArrayList<>();
        for (DBProduct dbProduct : productRepository.findAll()) {
            if (dbProduct.getDbUser().getId() == dbUser.getId() && !dbProduct.isOnSale()) {
                list.add(new Product(dbProduct));
            }
        }
        return list;
    }
}
