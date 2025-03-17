package org.example.addressbook.repositories;

import org.example.addressbook.entities.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<ContactEntity, Long> {
  ContactEntity findByEmail(String email);
  List<ContactEntity> findByUserId(Long userId);
}