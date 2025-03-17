package org.example.addressbook.serviceTest;

import org.example.addressbook.dto.AuthUserDTO;
import org.example.addressbook.dto.LoginDTO;
import org.example.addressbook.dto.PassDTO;
import org.example.addressbook.entities.AuthUser;
import org.example.addressbook.interfaces.IAuthInterface;
import org.example.addressbook.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthenticationServiceTest {

    @Qualifier("authenticationService")
    @Autowired
    IAuthInterface iAuthInterface;

    @Autowired
    UserRepository userRepository;

    @BeforeEach //setup
    public void registerDummyUser(){

        //clear the test db for fresh start
        iAuthInterface.clear();

        //creating a dummy user
        AuthUserDTO dummyUser = new AuthUserDTO("DummyFirstName", "DummyLastName", "DummyEmail@gmail.com", "DummyPass12#");

        //registering a dummy user, so we can implement other test cases
        iAuthInterface.register(dummyUser);

    }

    @AfterEach //clear test db
    public void clearDb(){
        iAuthInterface.clear();
    }

    @Test
    public void registerTest(){
        // arrange --> action --> assert

        //arrange
        AuthUserDTO tempUser = new AuthUserDTO("FirstName", "LastName", "TempEmail@gmail.com", "Temperas12#");

        //action
        String res = iAuthInterface.register(tempUser);

        //assert
        assertEquals("user registered", res, "User registration test failed");

    }

    @Test
    public void loginTest(){

        //arrange
        LoginDTO userLogin = new LoginDTO("DummyEmail@gmail.com", "DummyPass12#");

        MockHttpServletResponse response = new MockHttpServletResponse();

        //act
        String res = iAuthInterface.login(userLogin, response);

        //res should not be null
        assertNotNull(res);

        //user should be logged in
        assertTrue(res.contains("user logged in"), "log in test failure");

        //checking the creation of token in cookies of response
        String jwtCookie = response.getHeader("Set-Cookie");

        System.out.println(jwtCookie);

        assertNotNull(jwtCookie, "JWT cookie should be set in response");

    }

    @Test
    public void forgotPasswordTest(){

        PassDTO newPass = new PassDTO("Hello1@$");

        AuthUserDTO resDto = iAuthInterface.forgotPassword(newPass, "DummyEmail@gmail.com");

        assertNotNull(resDto);

        assertEquals("Hello1@$", resDto.getPassword(), "Password did not match after forgot");
    }

    @Test
    public void resetPasswordTest(){

        String email = "DummyEmail@gmail.com";
        String currentPass = "DummyPass12#";
        String newPass = "NewPass12#";

        String res = iAuthInterface.resetPassword(email, currentPass, newPass);

        //checking response
        assertEquals("Password reset successfully!", res);

        AuthUser foundUser = userRepository.findByEmail("DummyEmail@gmail.com");

        //checking the password update
        assertEquals("NewPass12#", foundUser.getPassword());

    }

}