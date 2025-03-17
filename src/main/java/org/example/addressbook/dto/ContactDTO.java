package org.example.addressbook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContactDTO {
  String name;
  @Email
  @NotBlank
  String email;
  Long phoneNumber;
  String address;
  Long userId;

  public ContactDTO(String name, String email, Long phoneNumber, String address) {
    this.name = name;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.address = address;
  }
}