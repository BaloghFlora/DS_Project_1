package com.example.demo.controllers;

import com.example.demo.dtos.PersonDTO;
import com.example.demo.dtos.PersonDetailsDTO;
import com.example.demo.services.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/people")
@Validated
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Get all people.
     */
    @GetMapping
    public ResponseEntity<List<PersonDTO>> getPeople() {
        return ResponseEntity.ok(personService.findPersons());
    }

    /**
     * Create a new person (register).
     */
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody PersonDetailsDTO person) {
        UUID id = personService.insert(person);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build(); // 201 + Location header
    }

    /**
     * Get person by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonDetailsDTO> getPerson(@PathVariable UUID id) {
        return ResponseEntity.ok(personService.findPersonById(id));
    }

    /**
     * (Optional) Get person by email.
     * Useful for login or lookups.
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<PersonDetailsDTO> getPersonByEmail(@PathVariable String email) {
        return ResponseEntity.ok(personService.findPersonByEmail(email));
    }
}
