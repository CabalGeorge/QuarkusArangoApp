package org.nagarro.com.phonebook.repository;

import org.nagarro.com.phonebook.model.Person;

import java.util.List;
import java.util.Optional;

public interface PhonebookRepo {

    Optional<Person> findByFirstname(String firstname);

    List<Person> getAllPersons();

    Person createPerson(Person person);

    Person updatePerson(Person person);

    void deletePersonByFirstname(String firstname);
}
