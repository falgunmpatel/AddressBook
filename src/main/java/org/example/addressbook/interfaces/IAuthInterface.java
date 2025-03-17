package org.example.addressbook.interfaces;

import org.example.addressbook.dto.AuthUserDTO;
import org.example.addressbook.dto.LoginDTO;
import org.example.addressbook.dto.PassDTO;
import org.springframework.stereotype.Service;

@Service
public interface IAuthInterface {

  String register(AuthUserDTO user) throws Exception;

  String login(LoginDTO user);

  AuthUserDTO forgotPassword(PassDTO pass, String email) throws Exception;

  String resetPassword(String email, String currentPass, String newPass) throws Exception;

  String clear();
}