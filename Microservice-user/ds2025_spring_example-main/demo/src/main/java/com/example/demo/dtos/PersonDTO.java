package com.example.demo.dtos;

import java.util.Objects;
import java.util.UUID;

public class PersonDTO {
    private UUID id;
    private String fullName;
    private String email;

    public PersonDTO() {
    }

    public PersonDTO(UUID id, String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonDTO that = (PersonDTO) o;
        return Objects.equals(fullName, that.fullName) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, email);
    }
}