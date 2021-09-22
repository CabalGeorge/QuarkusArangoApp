package org.nagarro.com.phonebook.repository;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.mapping.ArangoJack;
import org.nagarro.com.phonebook.model.Person;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PhonebookRepoImpl implements PhonebookRepo {

    private final static String DB_NAME = "Phonebook";
    private final static String COLLECTION_NAME = "PersonCollection";
    private final static String GET_ALL_QUERY = "FOR p IN PersonCollection RETURN p";
    private final ArangoDB arangoDB = new ArangoDB.Builder().serializer(new ArangoJack())
            .user("root")
            .password("root")
            .build();

    @Override
    public Optional<Person> findByFirstname(String firstname) {
        return Optional.empty();
    }

    @Override
    public List<Person> getAllPersons() {
        List<Person> personList = new ArrayList<>();
        try {
            ArangoCursor<BaseDocument> cursor = arangoDB.db(DB_NAME).query(GET_ALL_QUERY, BaseDocument.class);
            cursor.forEachRemaining(baseDocument -> {
                personList.add(createPersonFromDocument(baseDocument));
            });
        } catch (ArangoDBException exception) {
            System.err.println("Failed to execute query " + exception.getMessage());
        }
        return personList;
    }

    @Override
    public Person createPerson(Person person) {
        BaseDocument myDocument = createDocumentFromPerson(person);
        try {
            arangoDB.db(DB_NAME).collection(COLLECTION_NAME).insertDocument(myDocument);
            System.out.println("Person added to phonebook!");
        } catch (ArangoDBException exception) {
            System.err.println("Failed to create person " + exception.getMessage());
        }
        return person;
    }

    @Override
    public Person updatePerson(Person person) {
        BaseDocument baseDocument = createDocumentFromPerson(person);
        return null;
    }

    @Override
    public void deletePersonByFirstname(String firstname) {

    }

    private BaseDocument createDocumentFromPerson(Person person) {
        BaseDocument document = new BaseDocument();
        document.addAttribute("firstname", person.getFirstname());
        document.addAttribute("lastname", person.getLastname());
        document.addAttribute("phoneNumber", person.getPhoneNumber());
        document.addAttribute("dateOfBirth", person.getDateOfBirth().toString());

        return document;
    }

    private Person createPersonFromDocument(BaseDocument baseDocument) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return new Person.PersonBuilder()
                .firstname(baseDocument.getAttribute("firstname").toString())
                .lastname(baseDocument.getAttribute("lastname").toString())
                .phoneNumber(baseDocument.getAttribute("phoneNumber").toString())
                .dateOfBirth(LocalDate.parse(baseDocument.getAttribute("dateOfBirth").toString(), formatter))
                .build();
    }
}
