package com.example.demo.repositories;

import com.example.demo.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {

    /**
     * Example: JPA generated query by existing field
     */
    List<Person> findByFullName(String fullName);

    /**
     * Example: Find by email (unique)
     */
    Optional<Person> findByEmail(String email);

    /**
     * Example: Custom query (you can adapt it to your needs)
     * For instance, find users with a specific domain in their email.
     */
    @Query("SELECT p FROM Person p WHERE p.email LIKE CONCAT('%', :domain)")
    List<Person> findByEmailDomain(@Param("domain") String domain);
}
