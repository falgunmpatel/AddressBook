package org.example.addressbook.interfaces;

import java.util.*;

import jakarta.servlet.http.HttpServletRequest;
import org.example.addressbook.dto.ContactDTO;
import org.example.addressbook.dto.ResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface IContactService {
  ContactDTO get(Long id, HttpServletRequest request);

  ContactDTO create(ContactDTO user, HttpServletRequest request);

  String clear();

  List<ContactDTO> getAll(HttpServletRequest request);

  ContactDTO edit(ContactDTO user, Long id, HttpServletRequest request);

  String delete(Long id, HttpServletRequest request);

  ResponseDTO response(String message, String status);
}