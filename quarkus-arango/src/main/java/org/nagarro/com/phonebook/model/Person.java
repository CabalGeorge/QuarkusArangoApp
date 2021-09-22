package org.nagarro.com.phonebook.model;

import java.time.LocalDate;
import java.util.Objects;

public class Person {
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private LocalDate dateOfBirth;

    private Person(PersonBuilder personBuilder) {
        this.firstname = personBuilder.firstname;
        this.lastname = personBuilder.lastname;
        this.phoneNumber = personBuilder.phoneNumber;
        this.dateOfBirth = personBuilder.dateOfBirth;
    }

    public Person() {
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(firstname, person.firstname) && Objects.equals(lastname, person.lastname) && Objects.equals(phoneNumber, person.phoneNumber) && Objects.equals(dateOfBirth, person.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstname, lastname, phoneNumber, dateOfBirth);
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }

    public static class PersonBuilder {
        private String firstname;
        private String lastname;
        private String phoneNumber;
        private LocalDate dateOfBirth;

        public PersonBuilder firstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public PersonBuilder lastname(String lastname) {
            this.lastname = lastname;
            return this;
        }

        public PersonBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public PersonBuilder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Person build() {
            return new Person(this);
        }
    }
}
