package org.example.addressbook.serviceTest;


import org.example.addressbook.dto.AuthUserDTO;
import org.example.addressbook.dto.ContactDTO;
import org.example.addressbook.dto.LoginDTO;
import org.example.addressbook.entities.ContactEntity;
import org.example.addressbook.interfaces.IAuthInterface;
import org.example.addressbook.interfaces.IContactService;
import org.example.addressbook.repositories.ContactRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ContactServiceTest {

    @Qualifier("authenticationService")
    @Autowired
    IAuthInterface iAuthInterface;

    @Qualifier("contactService")
    @Autowired
    IContactService iContactService;

    @Autowired
    ContactRepository contactRepository;

    MockHttpServletRequest request = new MockHttpServletRequest();

    Long id = -1L;

    @BeforeEach //setup
    public void registerAndLoginDummyUser(){

        //clear the test db for fresh start
        iAuthInterface.clear();
        iContactService.clear();

        //creating a dummy user
        AuthUserDTO dummyUser = new AuthUserDTO("DummyFirstName", "DummyLastName", "DummyEmail@gmail.com", "DummyPass12#");

        //registering a dummy user, so we can implement other test cases
        iAuthInterface.register(dummyUser);

        //logging in the dummy user
        LoginDTO userLogin = new LoginDTO("DummyEmail@gmail.com", "DummyPass12#");

        MockHttpServletResponse response = new MockHttpServletResponse();

        iAuthInterface.login(userLogin, response);

        Cookie dummyCookie = new Cookie("jwt", response.getCookie("jwt").getValue());

        request.setCookies(dummyCookie);


    }

    @AfterEach
    public void clearDb(){
        iAuthInterface.clear();
        iContactService.clear();
    }

    @Test
    public void createTest(){

        ContactDTO newContact = new ContactDTO("User01", "user01@gmail.com", 635497564L, "Agra");

        ContactDTO resDto = iContactService.create(newContact, request);

        assertNotNull(resDto);

        //finding the contact in db
        ContactEntity foundContact = contactRepository.findByEmail("kushagrasharma@gmail.com");

        assertNotNull(foundContact);

        assertEquals(982749202L, foundContact.getPhoneNumber());

    }

    @Test
    public void getTest(){

        //creating a contact
        ContactDTO newContact = new ContactDTO("User01", "user01@gmail.com", 457546546L, "Agra");

        ContactDTO resDto = iContactService.create(newContact, request);

        assertNotNull(resDto);

        //action
        ContactDTO getDto = iContactService.get(resDto.getId(), request);

        //assert
        assertEquals(getJSON(resDto), getJSON(getDto));

    }

    @Test
    public void getAllTest(){

        //creating a list of contacts to add in the contact db one at a time
        List<ContactDTO> l1 = new ArrayList<>();

        l1.add(new ContactDTO("User01", "user01@gmail.com", 7684754995L, "Agra"));
        l1.add(new ContactDTO("User02", "user02@gmail.com", 982749202L, "Mathura"));
        l1.add(new ContactDTO("User03", "user03@gmail.com", 982749202L, "Delhi"));

        //adding the contacts to test db and setting the response dtos in the original list
        for (int i=0 ; i<l1.size() ; i++) {
            l1.set(i,iContactService.create(l1.get(i), request));
        }

        List<ContactDTO> resList = iContactService.getAll(request);

        //check all the contacts are received or not
        for(int i = 0;i<l1.size();i++){
            assertEquals(getJSON(l1.get(i)), getJSON(resList.get(i)));
        }

    }

    @Test
    public void editTest(){

        //creating a contact
        ContactDTO newContact = new ContactDTO("Kushagra Sharma", "kushagrasharma@gmail.com", 982749202L, "Agra");

        ContactDTO resDto1 = iContactService.create(newContact, request);

        assertNotNull(resDto1);

        //edit the previous contact wit new one
        ContactDTO editContact = new ContactDTO("Naman Agarwal", "naman@gmail.com", 982755502L, "Agra", resDto1.getId());

        ContactDTO resDto2 = iContactService.edit(editContact, resDto1.getId(), request);

        //assert the changes made with intended changes
        assertEquals(getJSON(editContact), getJSON(resDto2));

    }

    @Test
    public void deleteTest(){

        //creating a contact
        ContactDTO newContact = new ContactDTO("Kushagra Sharma", "kushagrasharma@gmail.com", 982749202L, "Agra");

        ContactDTO resDto1 = iContactService.create(newContact, request);

        assertNotNull(resDto1);

        //delete the contact created
        String res = iContactService.delete(resDto1.getId(), request);

        //match the res with expected response
        assertEquals("contact deleted", res, "response did not match");

        //double check in db if removed or not
        ContactEntity foundContact = contactRepository.findByEmail("kushagrasharma@gmail.com");

        //foundContact should be null if removed from test db
        assertNull(foundContact);
    }

    public String getJSON(Object object){
        try {
            ObjectMapper obj = new ObjectMapper();
            return obj.writeValueAsString(object);
        }
        catch(JsonProcessingException e){
            System.out.println("Exception : Conversion error from Java Object to JSON");
        }
        return null;
    }

}