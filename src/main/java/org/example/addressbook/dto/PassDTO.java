package org.example.addressbook.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PassDTO {
  String password;

  public PassDTO(String password) {
    this.password = password;
  }
}