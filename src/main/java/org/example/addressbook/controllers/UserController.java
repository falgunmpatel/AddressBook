package org.example.addressbook.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.addressbook.dto.*;
import org.example.addressbook.interfaces.IAuthInterface;
import org.example.addressbook.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class UserController {
  ObjectMapper obj = new ObjectMapper();

  @Autowired EmailService emailService;

  @Qualifier("IAuthInterface")
  @Autowired
  IAuthInterface iAuthInterface;

  @PostMapping(path = "/register")
  public String register(@Valid @RequestBody AuthUserDTO user) throws Exception{
    log.info("Employee tried to register with body: {}", obj.writeValueAsString(user));
    return iAuthInterface.register(user);
  }

  @PostMapping(path = "/login")
  public String login(@Valid @RequestBody LoginDTO user) throws Exception {
    log.info("Employee tried to login with body: {}", obj.writeValueAsString(user));
    return iAuthInterface.login(user);
  }

  @PostMapping(path = "/sendMail")
  public String sendMail(@Valid @RequestBody MailDTO message) throws Exception {
    log.info("Employee tried to send email with body: {}", obj.writeValueAsString(message));
    emailService.sendEmail(message.getTo(), message.getSubject(), message.getBody());
    return "Mail sent";
  }

  @PutMapping("/forgotPassword/{email}")
  public AuthUserDTO forgotPassword(@Valid @RequestBody PassDTO pass, @Valid @PathVariable String email) throws Exception{
    log.info("Employee applied for forgot password with body: {}", obj.writeValueAsString(pass));
    return iAuthInterface.forgotPassword(pass, email);
  }

  @PutMapping("/resetPassword/{email}")
  public String resetPassword(
      @Valid @PathVariable String email,
      @Valid @RequestParam String currentPass,
      @Valid @RequestParam String newPass) throws Exception {
    log.info("Employee applied for forgot password with email: {}", email);
    return iAuthInterface.resetPassword(email, currentPass, newPass);
  }

  @GetMapping("/clear")
  public String clear() {

    log.info("Database clear request is made");
    return iAuthInterface.clear();
  }

  public String getJSON(Object object) {
    try {
      ObjectMapper obj = new ObjectMapper();
      return obj.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.error(
          "Reason : {} Exception : {}",
          "Conversion error from Java Object to JSON",
          e.getMessage());
    }
    return null;
  }
}