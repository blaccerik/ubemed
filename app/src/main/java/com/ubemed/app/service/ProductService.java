package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBProductState;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.model.Product;
import com.ubemed.app.repository.CatRepository;
import com.ubemed.app.repository.ProductRepository;
import com.ubemed.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ProductService {

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<Product> getAll(String username) {
        Optional<DBUser> optionalDBUser = userRepository.findByName(username);
        if (optionalDBUser.isEmpty()) {
            return new ArrayList<>();
        }

        DBUser dbUser = optionalDBUser.get();

        List<Product> list = new ArrayList<>();
        for (DBProduct dbProduct : productRepository.findAll()) {
            if (dbProduct.getDbUser().getId() == dbUser.getId() && dbProduct.getDbProductState().getState().equals(DBProductState.states.inventory)) {
                list.add(new Product(dbProduct));
            }
        }
        return list;
    }
}
