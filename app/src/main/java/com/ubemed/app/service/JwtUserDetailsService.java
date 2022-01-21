package com.ubemed.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

//    @Autowired
//    private Repository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        return new User(username, "test2", authorities);

//        List<DBUser> list = repository.findByNameIsLike(username);
//        if (list.isEmpty()) {
//            throw new UsernameNotFoundException("User not found with username: " + username);
//        } else {
//            DBUser dbUser = list.get(0);
//
//            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
////            authorities.add(new SimpleGrantedAuthority(dbUser.getRole()));
//
//            return new User("test", "test2", authorities);
//        }
    }
}
