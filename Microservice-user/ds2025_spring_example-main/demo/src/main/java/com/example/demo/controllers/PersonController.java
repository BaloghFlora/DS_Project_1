package com.example.demo.controllers;

import com.example.demo.dtos.PersonDTO;
import com.example.demo.dtos.PersonDetailsDTO;
import com.example.demo.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/people")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * This method fixes the first build error.
     * It correctly converts the List<PersonDetailsDTO> from the service
     * into the List<PersonDTO> expected by the API.
     */
    @GetMapping()
    public ResponseEntity<List<PersonDTO>> getPersons() {
        List<PersonDetailsDTO> dtos = personService.findPersons();
        List<PersonDTO> dtos1 = new ArrayList<>();
        for (PersonDetailsDTO d : dtos) {
            dtos1.add(new PersonDTO(d.getId(), d.getFullName(), d.getEmail()));
        }
        return new ResponseEntity<>(dtos1, HttpStatus.OK);
    }

    /**
     * This method fixes the second build error.
     * It correctly returns the 'person' DTO it received,
     * not the 'UUID' from the service.
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<PersonDetailsDTO> update(@PathVariable("id") UUID id, @Valid @RequestBody PersonDetailsDTO person) {
        personService.update(id, person);
        return new ResponseEntity<>(person, HttpStatus.OK);
    }

    /**
     * This is the 'create' method we fixed two steps ago
     * to return the UUID for the frontend.
     */
    @PostMapping
    public ResponseEntity<UUID> create(@Valid @RequestBody PersonDetailsDTO person) {
        UUID id = personService.insert(person);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).body(id);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PersonDetailsDTO> getPerson(@PathVariable("id") UUID id) {
        PersonDetailsDTO dto = personService.findPersonById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        personService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}