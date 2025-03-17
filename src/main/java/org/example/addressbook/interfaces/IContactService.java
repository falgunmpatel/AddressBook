package org.example.addressbook.interfaces;

import java.util.*;
import org.example.addressbook.dto.ContactDTO;
import org.example.addressbook.dto.ResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface IContactService {
  ContactDTO get(Long id);

  ContactDTO create(ContactDTO user);

  String clear();

  List<ContactDTO> getAll();

  ContactDTO edit(ContactDTO user, Long id);

  String delete(Long id);

  ResponseDTO response(String message, String status);
}