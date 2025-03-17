package org.example.addressbook.interfaces;

import jakarta.servlet.http.HttpServletResponse;
import org.example.addressbook.dto.AuthUserDTO;
import org.example.addressbook.dto.LoginDTO;
import org.example.addressbook.dto.PassDTO;
import org.springframework.stereotype.Service;

@Service
public interface IAuthInterface {

  String register(AuthUserDTO user);

  public String login(LoginDTO user, HttpServletResponse response);

  AuthUserDTO forgotPassword(PassDTO pass, String email);

  String resetPassword(String email, String currentPass, String newPass);

  String clear();
}