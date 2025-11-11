package com.example.demo.services;

import com.example.demo.dtos.PersonDetailsDTO;
import com.example.demo.dtos.builders.PersonBuilder;
import com.example.demo.entities.Person;
import com.example.demo.handlers.exceptions.model.ResourceNotFoundException;
import com.example.demo.repositories.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;
    private final RestTemplate restTemplate; // <-- ADD THIS
    private final String AUTH_SERVICE_URL = "http://auth-service:8081"; // <-- ADD THIS

    @Autowired
    public PersonService(PersonRepository personRepository, RestTemplate restTemplate) { // <-- UPDATE THIS
        this.personRepository = personRepository;
        this.restTemplate = restTemplate; // <-- UPDATE THIS
    }

    /**
     * Get all persons from the database.
     */
    public List<PersonDetailsDTO> findPersons() {
        List<Person> personList = personRepository.findAll();
        return personList.stream()
                .map(PersonBuilder::toPersonDetailsDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get person details by ID.
     */
    public PersonDetailsDTO findPersonById(UUID id) {
        Optional<Person> prosumerOptional = personRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
        }
        return PersonBuilder.toPersonDetailsDTO(prosumerOptional.get());
    }

    /**
     * Insert new person into the database.
     */
    public UUID insert(PersonDetailsDTO personDTO) {
        Person person = PersonBuilder.toEntity(personDTO);
        person = personRepository.save(person);
        LOGGER.debug("Person with id {} was inserted in db", person.getId());

        // --- START NEW CODE ---
        // Now, register this user with the auth service
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("username", personDTO.getEmail()); // Use email as username
            map.add("password", personDTO.getPassword());
            map.add("role", "ROLE_USER"); // Default role

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            ResponseEntity<Void> response = restTemplate.postForEntity(
                    AUTH_SERVICE_URL + "/auth/register",
                    request,
                    Void.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                LOGGER.debug("Credential created for user {}", personDTO.getEmail());
            } else {
                LOGGER.warn("Could not create credential for user {}. Status: {}", personDTO.getEmail(),
                        response.getStatusCode());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to register credential for user {}: {}", personDTO.getEmail(), e.getMessage());
        }
        // --- END NEW CODE ---

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

    /**
     * Update an existing person.
     */
    public UUID update(UUID id, PersonDetailsDTO personDTO) {
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
        }
        Person person = personOptional.get();
        person.setFullName(personDTO.getFullName());
        person.setEmail(personDTO.getEmail());
        person.setPassword(personDTO.getPassword()); // Note: Passwords should be hashed in a real app
        person = personRepository.save(person);
        LOGGER.debug("Person with id {} was updated in db", person.getId());
        return person.getId();
    }

    /**
     * Delete a person by ID.
     */
    public void delete(UUID id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person with id " + id + " not found."));

        personRepository.delete(person);
        LOGGER.debug("Person with id {} was deleted from db", id);

        // --- START NEW DELETE CODE ---
        try {
            // Use email as username
            String username = person.getEmail();
            // We must URL-encode the username (e.g., "user@test.com")
            String urlEncodedUsername = java.net.URLEncoder.encode(username,
                    java.nio.charset.StandardCharsets.UTF_8.toString());

            restTemplate.delete(AUTH_SERVICE_URL + "/auth/delete?username=" + urlEncodedUsername);
            LOGGER.debug("Credential deleted for user {}", username);
        } catch (Exception e) {
            LOGGER.error("Failed to delete credential for user {}: {}", person.getEmail(), e.getMessage());
        }
        // --- END NEW DELETE CODE ---
    }
}
