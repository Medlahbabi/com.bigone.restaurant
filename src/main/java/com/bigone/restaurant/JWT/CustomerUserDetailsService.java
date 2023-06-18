package com.bigone.restaurant.JWT;


import com.bigone.restaurant.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service
public class CustomerUserDetailsService implements UserDetailsService {


    @Autowired
    UserDao userDao;

    private com.bigone.restaurant.POJO.User userDatails;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside loadUserByUsername {}", username);
        userDatails = userDao.findByEmailId(username);
        if (!Objects.isNull(userDatails)) {
            return new User(userDatails.getEmail(), userDatails.getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public com.bigone.restaurant.POJO.User getUserDatails() {
        return userDatails;
    }
}
