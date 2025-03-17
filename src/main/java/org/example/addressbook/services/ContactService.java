package org.example.addressbook.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.example.addressbook.dto.ContactDTO;
import org.example.addressbook.dto.ResponseDTO;
import org.example.addressbook.entities.ContactEntity;
import org.example.addressbook.interfaces.IContactService;
import org.example.addressbook.repositories.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ContactService implements IContactService {

  ObjectMapper obj = new ObjectMapper();
  @Autowired ContactRepository contactRepository;

  public ResponseDTO response(String message, String status) {
    return new ResponseDTO(message, status);
  }

  public ContactDTO get(Long id) {
    ContactEntity foundEmp =
        contactRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  log.error("Cannot find employee with id {}", id);
                  return new RuntimeException("Cannot find employee with given id");
                });

    ContactDTO resDto =
        new ContactDTO(
            foundEmp.getName(),
            foundEmp.getEmail(),
            foundEmp.getPhoneNumber(),
            foundEmp.getAddress(),
            foundEmp.getId());
    log.info("Employee DTO send for id: {} is : {}", id, getJSON(resDto));
    return resDto;
  }

  public ContactDTO create(ContactDTO user) {
    ContactEntity newUser =
        new ContactEntity(
            user.getName(), user.getEmail(), user.getPhoneNumber(), user.getAddress());

    contactRepository.save(newUser);

    log.info("Employee saved in db: {}", getJSON(newUser));
    ContactDTO resDto =
        new ContactDTO(
            newUser.getName(),
            newUser.getEmail(),
            newUser.getPhoneNumber(),
            newUser.getAddress(),
            newUser.getId());
    log.info("Employee DTO sent: {}", getJSON(resDto));

    return resDto;
  }

  public List<ContactDTO> getAll() {

    return contactRepository.findAll().stream()
        .map(
            entity ->
                new ContactDTO(
                    entity.getName(),
                    entity.getEmail(),
                    entity.getPhoneNumber(),
                    entity.getAddress(),
                    entity.getId()))
        .collect(Collectors.toList());
  }

  public ContactDTO edit(ContactDTO user, Long id) {
    ContactEntity foundEmp =
        contactRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  log.error("cannot find employee with id : {}", id);
                  return new RuntimeException("cannot find employee with given id");
                });

    foundEmp.setName(user.getName());
    foundEmp.setEmail(user.getEmail());

    contactRepository.save(foundEmp);

    log.info("Employee saved after editing in db is : {}", getJSON(foundEmp));
    return new ContactDTO(
        foundEmp.getName(),
        foundEmp.getEmail(),
        foundEmp.getPhoneNumber(),
        foundEmp.getAddress(),
        foundEmp.getId());
  }

  public String delete(Long id) {
    ContactEntity foundUser =
        contactRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  log.error("Cannot find user with id : {}", id);
                  return new RuntimeException("cannot find user with given id");
                });

    contactRepository.delete(foundUser);

    return "employee deleted";
  }

  public String clear() {
    contactRepository.deleteAll();
    return "db cleared";
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