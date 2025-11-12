package com.example.demo.controllers;

import com.example.demo.dtos.PersonDTO;
import com.example.demo.dtos.PersonDetailsDTO;
import com.example.demo.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.processing.SupportedOptions;

@RestController
@CrossOrigin
@RequestMapping(value = "/people")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    
    @Operation(summary = "Retrieves all people",
               description = "Fetches a list of all people with their basic details.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = PersonDTO.class)))
    @GetMapping()
    public ResponseEntity<List<PersonDTO>> getPersons() {
        List<PersonDetailsDTO> dtos = personService.findPersons();
        List<PersonDTO> dtos1 = new ArrayList<>();
        for (PersonDetailsDTO d : dtos) {
            dtos1.add(new PersonDTO(d.getId(), d.getFullName(), d.getEmail()));
        }
        return new ResponseEntity<>(dtos1, HttpStatus.OK);
    }

   @Operation(summary = "Updates an existing person",
               description = "Updates the details of a person specified by their ID.")
    @ApiResponse(responseCode = "200", description = "Person updated successfully")
    @ApiResponse(responseCode = "404", description = "Person not found")
    @PutMapping(value = "/{id}")
    public ResponseEntity<PersonDetailsDTO> update(@Parameter(description = "The UUID of the person to update")
            @PathVariable("id") UUID id, @Valid @RequestBody PersonDetailsDTO person) {
        personService.update(id, person);
        return new ResponseEntity<>(person, HttpStatus.OK);
    }

    @Operation(summary = "Creates a new person",
               description = "Creates a new person and registers them in the auth service.")
    @ApiResponse(responseCode = "201", description = "Person created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid person data supplied")
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

    @Operation(summary = "Retrieves a person by ID",
               description = "Fetches the details of a person specified by their ID.")
    @ApiResponse(responseCode = "200", description = "Person retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Person not found")
    @GetMapping(value = "/{id}")
    public ResponseEntity<PersonDetailsDTO> getPerson(@Parameter(description = "The UUID of the person to retrieve") 
        @PathVariable("id") UUID id) {
        PersonDetailsDTO dto = personService.findPersonById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
    @Operation(summary = "Deletes a person by ID",
               description = "Deletes the person specified by their ID from the system.")
    @ApiResponse(responseCode = "200", description = "Person deleted successfully")
    @ApiResponse(responseCode = "404", description = "Person not found")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "The UUID of the person to delete") 
    @PathVariable("id") UUID id) {
        personService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}