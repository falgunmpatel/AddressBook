package org.example.addressbook.controllers;

import java.util.*;
import org.example.addressbook.dto.ContactDTO;
import org.example.addressbook.dto.ResponseDTO;
import org.example.addressbook.interfaces.IContactService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/addressbook")
@Slf4j
public class AddressController {
    @Qualifier("contactService")
    @Autowired
    IContactService iContactService;

    @GetMapping("/res/get/{id}")
    public ResponseDTO get1(@Valid @PathVariable Long id){

        log.info("1 User tried to get with id: {}", id);

        return new ResponseDTO("API triggered at /res/get/{id}", "Success");
    }

    @PostMapping("/res/create")
    public ResponseDTO create1(@Valid @RequestBody ContactDTO user){

        log.info("1 User tried to create with body: {}", getJSON(user));

        return new ResponseDTO("API triggered at /res/create", "Success");
    }

    @GetMapping("/res/getAll")
    public ResponseDTO getAll1(){

        log.info("1 User tried to getAll");

        return new ResponseDTO("API triggered at /res/getAll", "Success");
    }

    @PutMapping("/res/edit/{id}")
    public ResponseDTO edit1(@Valid @RequestBody ContactDTO user, @Valid @PathVariable Long id){

        log.info("1 User tried to edit with id : {} and body : {}", id, getJSON(user));

        return new ResponseDTO("API triggered at /res/edit/{id}", "Success");
    }

    @DeleteMapping("/res/delete/{id}")
    public ResponseDTO delete1(@Valid @PathVariable Long id){

        log.info("1 User tried to delete with id: {}", id);

        return new ResponseDTO("API triggered at /res/delete/{id}", "Success");
    }

//    -----------------------------------------------------------------------

    @GetMapping("/res2/get/{id}")
    public ResponseDTO get2(@Valid @PathVariable Long id){

        log.info("2 User tried to get with id: {}", id);

        return iContactService.response("API triggered at /res/get/{id}", "Success");
    }

    @PostMapping("/res2/create")
    public ResponseDTO create2(@Valid @RequestBody ContactDTO user){

        log.info("2 User tried to create with body: {}", getJSON(user));

        return iContactService.response("API triggered at /res/create", "Success");
    }

    @GetMapping("/res2/getAll")
    public ResponseDTO getAll2(){

        log.info("2 User tried to getAll");

        return iContactService.response("API triggered at /res/getAll", "Success");
    }

    @PutMapping("/res2/edit/{id}")
    public ResponseDTO edit2(@Valid @RequestBody ContactDTO user,@Valid @PathVariable Long id){

        log.info("2 User tried to edit with id : {} and body : {}", id, getJSON(user));

        return iContactService.response("API triggered at /res/edit/{id}", "Success");
    }

    @DeleteMapping("/res2/delete/{id}")
    public ResponseDTO delete2(@Valid @PathVariable Long id){

        log.info("2 User tried to delete with id: {}", id);

        return iContactService.response("API triggered at /res/delete/{id}", "Success");
    }


    //------------------------------------------------------------------

    @GetMapping("/get/{id}")
    public ContactDTO get3(@Valid @PathVariable Long id, HttpServletRequest request){

        log.info("3 User tried to get with id: {}", id);

        return iContactService.get(id, request);
    }

    @PostMapping("/create")
    public ContactDTO create3(@Valid @RequestBody ContactDTO user, HttpServletRequest request){

        log.info("3 User tried to create with body: {}", getJSON(user));

        return iContactService.create(user, request);
    }

    @GetMapping("/getAll")
    public List<ContactDTO> getAll3(HttpServletRequest request){

        log.info("3 User tried to getAll");

        return iContactService.getAll(request);
    }

    @PutMapping("/edit/{id}")
    public ContactDTO edit3(@Valid @RequestBody ContactDTO user,@Valid @PathVariable Long id, HttpServletRequest request){

        log.info("3 User tried to edit with id : {} and body : {}", id, getJSON(user));

        return iContactService.edit(user, id, request);
    }

    @DeleteMapping("/delete/{id}")
    public String delete3(@Valid @PathVariable Long id, HttpServletRequest request){

        log.info("3 User tried to delete with id: {}", id);

        return iContactService.delete(id, request);
    }

    @GetMapping("/clear")
    public String clear(){
        return iContactService.clear();
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