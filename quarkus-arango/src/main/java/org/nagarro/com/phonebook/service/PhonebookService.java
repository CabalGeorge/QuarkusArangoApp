package org.nagarro.com.phonebook.service;

import org.nagarro.com.phonebook.model.Person;
import org.nagarro.com.phonebook.repository.PhonebookRepo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PhonebookService {

    @Inject
    PhonebookRepo phonebookRepo;

    public Optional<Person> getPersonByFirstname(String firstname) {
        return phonebookRepo.findByFirstname(firstname);
    }

    public List<Person> getAllPersons() {
        return phonebookRepo.getAllPersons();
    }

    public Person addPerson(Person person) {
        phonebookRepo.createPerson(person);
        return person;
    }

    public Person updatePerson(Person person) {
        phonebookRepo.updatePerson(person);
        return person;
    }

    public void deletePerson(String firstname) {
        phonebookRepo.deletePersonByFirstname(firstname);
    }

    public List<String> getFullNames() {
        return phonebookRepo.getPersonsFullName();
    }

}
