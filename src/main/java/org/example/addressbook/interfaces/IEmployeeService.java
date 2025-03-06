package org.example.addressbook.interfaces;

import java.util.*;
import org.example.addressbook.dto.EmployeeDTO;
import org.example.addressbook.dto.ResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface IEmployeeService {



    public EmployeeDTO get(Long id);

    public EmployeeDTO create(EmployeeDTO user);

    public String clear();

    public List<EmployeeDTO> getAll();

    public EmployeeDTO edit(EmployeeDTO user, Long id);

    public String delete(Long id);

    public ResponseDTO response(String message, String status);
}