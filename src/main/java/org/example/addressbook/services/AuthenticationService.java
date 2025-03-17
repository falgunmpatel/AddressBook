package org.example.addressbook.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.addressbook.dto.*;
import org.example.addressbook.entities.AuthUser;
import org.example.addressbook.interfaces.IAuthInterface;
import org.example.addressbook.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

@Service
@Slf4j
public class AuthenticationService implements IAuthInterface {

  @Autowired UserRepository userRepository;

  @Autowired MessageProducer messageProducer;

  @Autowired JwtTokenService jwtTokenService;

  @Autowired RedisTokenService redisTokenService;

  BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

  public String register(AuthUserDTO user) {
    try {
      List<AuthUser> l1 = userRepository.findAll().stream().filter(authuser -> user.getEmail().equals(authuser.getEmail())).toList();

      if (!l1.isEmpty()) {
        log.error("User already registered with email: {} ", user.getEmail());
        throw new RuntimeException();
      }

      String hashPassword = bCryptPasswordEncoder.encode(user.getPassword());

      AuthUser newUser = new AuthUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), hashPassword);

      newUser.setHashPass(hashPassword);

      userRepository.save(newUser);
      log.info("User saved in database : {}", getJSON(newUser));

      log.info("User saved in database : {}", getJSON(newUser));

      String customMessage = "REGISTER|"+user.getEmail()+"|"+user.getFirstName();
      messageProducer.sendMessage(customMessage);

      return "user registered";
    }
    catch(RuntimeException e){
      log.error("User already registered with email: {} Exception : {}", user.getEmail(), e.getMessage());
    }
    return null;
  }


  public String login(LoginDTO user, HttpServletResponse response){
    try {
      List<AuthUser> l1 = userRepository.findAll().stream().filter(authuser -> authuser.getEmail().equals(user.getEmail())).toList();
      if (l1.isEmpty()) {
        log.error("User not registered with email: {} ", user.getEmail());
        throw new RuntimeException();
      }
      AuthUser foundUser = l1.getFirst();

      if (!bCryptPasswordEncoder.matches(user.getPassword(), foundUser.getHashPass())) {
        log.error("Invalid password entered for email {} where entered password is {}", user.getEmail(), user.getPassword());
        return "Invalid password";
      }

      String token = jwtTokenService.createToken(foundUser.getId());

      ResponseCookie resCookie = ResponseCookie.from("jwt", token)
              .httpOnly(true)
              .secure(false)
              .path("/")
              .maxAge(3600)
              .sameSite("Strict")
              .build();

      response.addHeader(HttpHeaders.SET_COOKIE, resCookie.toString());

      redisTokenService.saveToken(foundUser.getId().toString(), token);

      foundUser.setToken(token);

      userRepository.save(foundUser);

      log.info("User logged in with email {}", user.getEmail());
      return "user logged in" + "\ntoken : " + token;
    } catch(RuntimeException e){
      log.error("Exception : {}", e.getMessage());
    }
    return null;

  }

  public AuthUserDTO forgotPassword(PassDTO pass, String email) {
    try {
      AuthUser foundUser = userRepository.findByEmail(email);

      if (foundUser == null) {
        log.error("user not registered with email: {}", email);
        throw new RuntimeException();
      }
      String hashPassword = bCryptPasswordEncoder.encode(pass.getPassword());

      foundUser.setPassword(pass.getPassword());
      foundUser.setHashPass(hashPassword);

      log.info("Hashed Password : {} for password : {} saved for user: {}", hashPassword, pass.getPassword(), getJSON(foundUser));
      userRepository.save(foundUser);

      String customMessage = "FORGOT|"+foundUser.getEmail()+"|"+foundUser.getFirstName();
      messageProducer.sendMessage(customMessage);

      return new AuthUserDTO(foundUser.getFirstName(), foundUser.getLastName(), foundUser.getEmail(), foundUser.getPassword(), foundUser.getId());
    }
    catch(RuntimeException e){
      log.error("user not registered with email: {} Exception : {}", email, e.getMessage());
    }
    return null;
  }

  public String resetPassword(String email, String currentPass, String newPass) {
    AuthUser foundUser = userRepository.findByEmail(email);
    if (foundUser == null)
      return "user not registered!";

    if (!bCryptPasswordEncoder.matches(currentPass, foundUser.getHashPass()))
      return "incorrect password!";

    String hashPassword = bCryptPasswordEncoder.encode(newPass);

    foundUser.setHashPass(hashPassword);
    foundUser.setPassword(newPass);

    userRepository.save(foundUser);

    log.info("Hashed Password : {} for password : {} saved for user : {}", hashPassword, newPass, getJSON(foundUser));
    String customMessage = "RESET|"+foundUser.getEmail()+"|"+foundUser.getFirstName();
    messageProducer.sendMessage(customMessage);

    return "Password reset successfully!";
  }

  public String logout(HttpServletRequest request, HttpServletResponse response){

    Cookie foundCookie = null;

    if(request.getCookies() ==  null)
      return "user not logged in";

    for(Cookie c : request.getCookies()){
      if(c.getName().equals("jwt")){
        foundCookie = c;
        break;
      }
    }
    if(foundCookie == null)
      return "user not logged in";

    ResponseCookie expiredCookie = ResponseCookie.from("jwt", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0)
            .sameSite("Lax")
            .build();

    response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

    redisTokenService.deleteToken(jwtTokenService.decodeToken(foundCookie.getValue()).toString());

    return "You are logged out";
  }

  public String clear(){
    userRepository.deleteAll();
    log.info("all data inside db is deleted");
    return "Database cleared";
  }

  public String getJSON(Object object){
    try {
      ObjectMapper obj = new ObjectMapper();
      return obj.writeValueAsString(object);
    }
    catch(JsonProcessingException e){
      log.error("Reason : {} Exception : {}", "Conversion error from Java Object to JSON", e.getMessage());
    }
    return null;
  }
}