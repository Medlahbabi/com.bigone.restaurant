package com.bigone.restaurant.serviceImpl;
import com.google.common.base.Strings;
import com.bigone.restaurant.JWT.CustomerUserDetailsService;
import com.bigone.restaurant.JWT.JwtFilter;
import com.bigone.restaurant.JWT.JwtUtil;
import com.bigone.restaurant.POJO.User;
import com.bigone.restaurant.constents.RestaurantConstants;
import com.bigone.restaurant.dao.UserDao;
import com.bigone.restaurant.service.UserService;
import com.bigone.restaurant.utils.EmailUtil;
import com.bigone.restaurant.utils.RestaurantUtils;
import com.bigone.restaurant.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
@Autowired
    UserDao userDao;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    EmailUtil emailUtil;
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
      log.info("Inside signup {}",requestMap);
      try {


      if (validateSignUpMap(requestMap)){
          //System.out.println("inside validaSignUpMap");
          User user=userDao.findByEmailId(requestMap.get("email"));
          if (Objects.isNull(user)){
            userDao.save(getUserFromMap(requestMap));
              //System.out.println("Successfully  Registered.");
            return RestaurantUtils.getResponseEntity("Successfully Registered.",HttpStatus.OK);
          }
          else {
              return RestaurantUtils.getResponseEntity("Email already exits.",HttpStatus.BAD_REQUEST);
          }
      }
      else{
          //System.out.println(RestaurantConstants.INVALID_DATA);
          return RestaurantUtils.getResponseEntity(RestaurantConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
      }
      }catch (Exception ex){
          ex.printStackTrace();
      }
        //System.out.println(RestaurantConstants.SOMETHING_WENT_WRONG);
      return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login {}", requestMap);
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));
            if (auth.isAuthenticated()) {
                if (customerUserDetailsService.getUserDatails().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\"" + jwtUtil.generateToken(
                            customerUserDetailsService.getUserDatails().getEmail(), customerUserDetailsService.getUserDatails().getRole()) + "\"}",
                            HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"message\":\"" + "Wait for Admin Approvel." + "\"}",
                            HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception ex) {
            log.error("{}", ex);
        }
        return new ResponseEntity<String>("{\"message\":\"" + "Bad Credentials." + "\"}",
                HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try{
          if(jwtFilter.isAdmin()){
              return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);
          }else {
              return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
          }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
         if (jwtFilter.isAdmin()){
             Optional<User> optional= userDao.findById(Integer.parseInt(requestMap.get("id")));
             if (!optional.isEmpty()){
               userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                 sendMailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userDao.getAllAdmin());
               return RestaurantUtils.getResponseEntity("User Status is updated Successfully", HttpStatus.OK);
             }
             else {
                return RestaurantUtils.getResponseEntity("User Status is updated Successfully", HttpStatus.OK);
             }
         }else{
             return RestaurantUtils.getResponseEntity(RestaurantConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
         }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return RestaurantUtils.getResponseEntity("true",HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(jwtFilter.getCurrentUsername());
            if (!user.equals(null)) {
                if (user.getPassword().equals(requestMap.get("oldPassword"))) {
                    user.setPassword(requestMap.get("newPassword"));
                    userDao.save(user);
                    return RestaurantUtils.getResponseEntity("Password Updated Successfully", HttpStatus.OK);
                }
                return RestaurantUtils.getResponseEntity("Incorrect Old Password", HttpStatus.BAD_REQUEST);
            }
            return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgetPassword(Map<String, String> requestMap) {
        try {
           User user=userDao.findByEmail(requestMap.get("email"));
           if(!Objects.isNull(user)&&!Strings.isNullOrEmpty(user.getEmail()))
              emailUtil.forgetMail(user.getEmail(), "Credentials by Restaurant Mohamed Lahbabi", user.getPassword());
           return RestaurantUtils.getResponseEntity("Check Your mail for Credentials", HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
    allAdmin.remove(jwtFilter.getCurrentUsername());
    if(status!=null && status.equalsIgnoreCase("true")){
     emailUtil.sendSimpleMessage(jwtFilter.getCurrentUsername(),"Account Approved", "USER:- " + user + "\n is approved by\nADMIN:-" + jwtFilter.getCurrentUsername(), allAdmin);
    }else {
        emailUtil.sendSimpleMessage(jwtFilter.getCurrentUsername(),"Account Disabled", "USER:- " + user + "\n is disabled by\nADMIN:-" + jwtFilter.getCurrentUsername(), allAdmin);
    }
    }

    private boolean validateSignUpMap(Map<String,String>requestMap){
      if(requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
              && requestMap.containsKey("email") && requestMap.containsKey("password")){
          return true;
      }
      return false;
    }
    private User getUserFromMap(Map<String,String>requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus(requestMap.get("status"));
        user.setRole("user");
        return user;
    }
}
