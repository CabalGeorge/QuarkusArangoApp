package org.nagarro.com.phonebook.service;

import org.nagarro.com.phonebook.model.Person;
import org.nagarro.com.phonebook.repository.PhonebookRepo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class PhonebookService {

    @Inject
    PhonebookRepo phonebookRepo;

    public Person addPerson(Person person) {
        phonebookRepo.createPerson(person);
        return person;
    }

    public List<Person> getAllPersons() {
        return phonebookRepo.getAllPersons();
    }
}
