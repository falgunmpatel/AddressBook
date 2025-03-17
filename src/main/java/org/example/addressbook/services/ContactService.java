package org.example.addressbook.services;


import org.example.addressbook.dto.ContactDTO;
import org.example.addressbook.dto.ResponseDTO;
import org.example.addressbook.entities.ContactEntity;
import org.example.addressbook.interfaces.IContactService;
import org.example.addressbook.repositories.ContactRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContactService implements IContactService {

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    RedisTemplate<String, ContactDTO> cacheContacts; //for single contact fetching

    @Autowired
    RedisTemplate<String, List<ContactDTO>> cacheContactList;// for all contacts of a single user

    public ResponseDTO response(String message, String status){
        return new ResponseDTO(message, status);
    }

    public ContactDTO get(Long id, HttpServletRequest request){
        try {

            Long userId = getUserId(request);

            if(cacheContacts.opsForValue().get("Contact"+userId+":"+id) != null) {
                System.out.println("Done through caching");
                return cacheContacts.opsForValue().get("Contact" + userId + ":" + id);
            }

            List<ContactEntity> contacts = contactRepository.findByUserId(userId).stream().filter(entity -> entity.getId().equals(id)).toList();

            if(contacts.isEmpty())
                throw new RuntimeException();

            ContactEntity foundContact = contacts.getFirst();

            ContactDTO resDto = new ContactDTO(foundContact.getName(), foundContact.getEmail(), foundContact.getPhoneNumber(), foundContact.getAddress(), foundContact.getId());

            log.info("Contact DTO send for id: {} is : {}", id, getJSON(resDto));

            //save contact dto in the redis cache
            cacheContacts.opsForValue().set("Contact" + userId + ":" + id, resDto);

            return resDto;
        }
        catch(RuntimeException e){
            log.error("Cannot find contact with id {}", id);
        }
        return null;
    }

    public ContactDTO create(ContactDTO user, HttpServletRequest request){
        try {

            ContactEntity foundEntity = contactRepository.findByEmail(user.getEmail());

            if(foundEntity != null)
                throw new RuntimeException("User already created with given email");
            //fetching userId from token in cookies of user
            Long userId = getUserId(request);

            ContactEntity newUser = new ContactEntity(user.getName(), user.getEmail(), user.getPhoneNumber(), user.getAddress(), userId);

            contactRepository.save(newUser);

            log.info("Contact saved in db: {}", getJSON(newUser));

            ContactDTO resDto = new ContactDTO(newUser.getName(), newUser.getEmail(), newUser.getPhoneNumber(), newUser.getAddress(), newUser.getId());

            log.info("Contact DTO sent: {}", getJSON(resDto));

            //add the new contact in the cached contact list
            List<ContactDTO> l1 = cacheContactList.opsForValue().get("Contact"+userId);

            if(l1 == null)
                l1 = new ArrayList<>();

            l1.add(resDto);

            cacheContactList.opsForValue().set("Contact"+userId, l1);

            return resDto;
        }
        catch(RuntimeException e){
            log.error("Exception : {} Reason : {}", e, "User already created with given email");
        }
        return null;
    }

    public List<ContactDTO> getAll(HttpServletRequest request){

        //fetching userId from token in cookies of user
        Long userId = getUserId(request);

        if(cacheContactList.opsForValue().get("Contact"+userId) != null) {
            System.out.println("Done through caching");
            return cacheContactList.opsForValue().get("Contact" + userId);
        }

        List<ContactDTO> result = contactRepository.findByUserId(userId).stream().map(entity -> new ContactDTO(entity.getName(), entity.getEmail(), entity.getPhoneNumber(), entity.getAddress(), entity.getId())).collect(Collectors.toList());

        //save list in cache memory of redis
        cacheContactList.opsForValue().set("Contact"+userId, result);

        return result;
    }

    public ContactDTO edit(ContactDTO user, Long id, HttpServletRequest request) {

        Long userId = getUserId(request);

        List<ContactEntity> contacts = contactRepository.findByUserId(userId).stream().filter(entity -> entity.getId().equals(id)).toList();

        if(contacts.isEmpty())
            throw new RuntimeException("No contact with given id found");

        ContactEntity foundContact = contacts.getFirst();

        foundContact.setName(user.getName());
        foundContact.setEmail(user.getEmail());
        foundContact.setAddress(user.getAddress());
        foundContact.setPhoneNumber(user.getPhoneNumber());

        contactRepository.save(foundContact);

        log.info("Contact saved after editing in db is : {}", getJSON(foundContact));

        ContactDTO resDto = new ContactDTO(foundContact.getName(), foundContact.getEmail(),foundContact.getPhoneNumber(), foundContact.getAddress(), foundContact.getId());

        //reset the contacts list and the contact cache(to freshly search the updated contact next time from db)
        cacheContacts.delete("Contact"+userId+":"+id);
        cacheContactList.delete("Contact"+userId);

        return resDto;
    }

    public String delete(Long id, HttpServletRequest request){

        Long userId = getUserId(request);

        List<ContactEntity> contacts = contactRepository.findByUserId(userId).stream().filter(entity -> entity.getId().equals(id)).toList();

        if(contacts.isEmpty())
            throw new RuntimeException("No contact with given id found");

        ContactEntity foundUser = contacts.getFirst();

        contactRepository.delete(foundUser);

        //reset the contacts list and the contact cache(to freshly search the updated contacts next time from db)
        cacheContacts.delete("Contact"+userId+":"+id);
        cacheContactList.delete("Contact"+userId);

        return "contact deleted";
    }

    public String clear(){

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

    public Long getUserId(HttpServletRequest request){

        //fetching token of logged in user
        Cookie foundCookie = null;

        for(Cookie c : request.getCookies()){
            if(c.getName().equals("jwt")){
                foundCookie = c;
                break;
            }
        }
        if(foundCookie == null)
            throw new RuntimeException("Cannot find the login cookie");

        //decode the user id from token in cookie using jwtTokenService

        return jwtTokenService.decodeToken(foundCookie.getValue());
    }


}