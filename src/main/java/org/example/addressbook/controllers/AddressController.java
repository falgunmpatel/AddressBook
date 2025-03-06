package org.example.addressbook.controllers;

import java.util.*;
import org.example.addressbook.dto.EmployeeDTO;
import org.example.addressbook.dto.ResponseDTO;
import org.example.addressbook.interfaces.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/addressbook")
public class AddressController {

    IEmployeeService iEmployeeService;

    @GetMapping("/res/get/{id}")
    public ResponseDTO get1(@PathVariable Long id){
        return new ResponseDTO("API triggered at /res/get/{id}", "Success");
    }

    @PostMapping("/res/create")
    public ResponseDTO create1(@RequestBody EmployeeDTO user){
        return new ResponseDTO("API triggered at /res/create", "Success");
    }

    @GetMapping("/res/getAll")
    public ResponseDTO getAll1(){
        return new ResponseDTO("API triggered at /res/getAll", "Success");
    }

    @PutMapping("/res/edit/{id}")
    public ResponseDTO edit1(@RequestBody EmployeeDTO user, @PathVariable Long id){
        return new ResponseDTO("API triggered at /res/edit/{id}", "Success");
    }

    @DeleteMapping("/res/delete/{id}")
    public ResponseDTO delete1(@PathVariable Long id){
        return new ResponseDTO("API triggered at /res/delete/{id}", "Success");
    }

    //-----------------------------------//

    @GetMapping("/res2/get/{id}")
    public ResponseDTO get2(@PathVariable Long id){
        return iEmployeeService.response("API triggered at /res/get/{id}", "Success");
    }

    @PostMapping("/res2/create")
    public ResponseDTO create2(@RequestBody EmployeeDTO user){
        return iEmployeeService.response("API triggered at /res/create", "Success");
    }

    @GetMapping("/res2/getAll")
    public ResponseDTO getAll2(){
        return iEmployeeService.response("API triggered at /res/getAll", "Success");
    }

    @PutMapping("/res2/edit/{id}")
    public ResponseDTO edit2(@RequestBody EmployeeDTO user, @PathVariable Long id){
        return iEmployeeService.response("API triggered at /res/edit/{id}", "Success");
    }

    @DeleteMapping("/res2/delete/{id}")
    public ResponseDTO delete2(@PathVariable Long id){
        return iEmployeeService.response("API triggered at /res/delete/{id}", "Success");
    }

    //-----------------------------------//
    @GetMapping("/get/{id}")
    public EmployeeDTO get3(@PathVariable Long id){
        return iEmployeeService.get(id);
    }

    @PostMapping("/create")
    public EmployeeDTO create3(@RequestBody EmployeeDTO user){
        return iEmployeeService.create(user);
    }

    @GetMapping("/getAll")
    public List<EmployeeDTO> getAll3(){
        return iEmployeeService.getAll();
    }

    @PutMapping("/edit/{id}")
    public EmployeeDTO edit3(@RequestBody EmployeeDTO user, @PathVariable Long id){
        return iEmployeeService.edit(user, id);
    }

    @DeleteMapping("/delete/{id}")
    public String delete3(@PathVariable Long id){
        return iEmployeeService.delete(id);
    }

    @GetMapping("/clear")
    public String clear(){
        return iEmployeeService.clear();
    }
}