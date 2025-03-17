package org.example.addressbook.interfaces;

import java.util.*;
import org.example.addressbook.dto.EmployeeDTO;
import org.example.addressbook.dto.ResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface IEmployeeService {
  EmployeeDTO get(Long id) throws Exception;

  EmployeeDTO create(EmployeeDTO user) throws Exception;

  String clear();

  List<EmployeeDTO> getAll();

  EmployeeDTO edit(EmployeeDTO user, Long id) throws Exception;

  String delete(Long id);

  ResponseDTO response(String message, String status);
}