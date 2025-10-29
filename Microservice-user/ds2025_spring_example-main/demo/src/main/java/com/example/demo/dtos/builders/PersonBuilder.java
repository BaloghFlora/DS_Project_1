package com.example.demo.dtos.builders;

import com.example.demo.dtos.PersonDTO;
import com.example.demo.dtos.PersonDetailsDTO;
import com.example.demo.entities.Person;

public class PersonBuilder {

    private PersonBuilder() {
    }

    public static PersonDTO toPersonDTO(Person person) {
        return new PersonDTO(person.getId(), person.getFullName(), person.getEmail());
    }

    public static PersonDetailsDTO toPersonDetailsDTO(Person person) {
        return new PersonDetailsDTO(
                person.getId(),
                person.getFullName(),
                person.getEmail(),
                person.getPassword()
        );
    }

    public static Person toEntity(PersonDetailsDTO personDetailsDTO) {
        return new Person(
                personDetailsDTO.getFullName(),
                personDetailsDTO.getEmail(),
                personDetailsDTO.getPassword()
        );
    }
}
