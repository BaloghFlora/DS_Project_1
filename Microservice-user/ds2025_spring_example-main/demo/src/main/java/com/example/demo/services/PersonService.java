package com.example.demo.services;

import com.example.demo.dtos.PersonDTO;
import com.example.demo.dtos.PersonDetailsDTO;
import com.example.demo.dtos.builders.PersonBuilder;
import com.example.demo.entities.Person;
import com.example.demo.handlers.exceptions.model.ResourceNotFoundException;
import com.example.demo.repositories.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Get all persons from the database.
     */
    public List<PersonDTO> findPersons() {
        List<Person> personList = personRepository.findAll();
        return personList.stream()
                .map(PersonBuilder::toPersonDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get person details by ID.
     */
    public PersonDetailsDTO findPersonById(UUID id) {
        Optional<Person> personOptional = personRepository.findById(id);
        if (personOptional.isEmpty()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
        }
        return PersonBuilder.toPersonDetailsDTO(personOptional.get());
    }

    /**
     * Insert new person into the database.
     */
    public UUID insert(PersonDetailsDTO personDTO) {
        Person person = PersonBuilder.toEntity(personDTO);
        person = personRepository.save(person);
        LOGGER.debug("Person with id {} was inserted in db", person.getId());
        return person.getId();
    }

    /**
     * (Optional) Find person by email.
     */
    public PersonDetailsDTO findPersonByEmail(String email) {
        Optional<Person> personOptional = personRepository.findByEmail(email);
        if (personOptional.isEmpty()) {
            LOGGER.error("Person with email {} was not found in db", email);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with email: " + email);
        }
        return PersonBuilder.toPersonDetailsDTO(personOptional.get());
    }
}
