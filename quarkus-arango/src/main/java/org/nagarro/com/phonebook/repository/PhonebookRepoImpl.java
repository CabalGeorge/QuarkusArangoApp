package org.nagarro.com.phonebook.repository;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.mapping.ArangoJack;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.nagarro.com.phonebook.model.Person;

import javax.cache.Cache.Entry;
import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@ApplicationScoped
public class PhonebookRepoImpl implements PhonebookRepo {
    private final static String CACHE_NAME = "PersonCache";
    private final static String DB_NAME = "Phonebook";
    private final static String COLLECTION_NAME = "PersonCollection";
    private final static String GET_ALL_QUERY = "FOR p IN PersonCollection RETURN p";
    private final static String FIND_BY_FIRSTNAME_QUERY = "FOR p IN PersonCollection FILTER p.firstname == @firstname RETURN p";
    private final static String GET_FULL_NAME_QUERY = "FOR p IN PersonCollection RETURN { [\"fullname\"] : " +
            "CONCAT(p.firstname,\" \", p.lastname)}";

    private final ArangoDB arangoDB = new ArangoDB.Builder().serializer(new ArangoJack())
            .user("root")
            .password("root")
            .build();

    @IgniteInstanceResource
    private Ignite ignite;
    private final IgniteCache<String, Person> personCache = ignite.getOrCreateCache(CACHE_NAME);


    @Override
    public Optional<Person> findByFirstname(String firstname) {
        Person person = personCache.get(firstname);
//        Person person = createPersonFromDocument(getDocumentByPersonFirstname(firstname));
        return Optional.of(person);
    }

    @Override
    public List<Person> getAllPersons() {
        List<Person> personList = new ArrayList<>();
        for (Entry<String, Person> stringPersonEntry : personCache) {
            Person person = stringPersonEntry.getValue();
            personList.add(person);
        }
//        try {
//            ArangoCursor<BaseDocument> cursor = arangoDB.db(DB_NAME).query(GET_ALL_QUERY, BaseDocument.class);
//            cursor.forEachRemaining(baseDocument -> {
//                personList.add(createPersonFromDocument(baseDocument));
//            });
//        } catch (ArangoDBException exception) {
//            System.err.println("Failed to execute query " + exception.getMessage());
//        }
        return personList;
    }

    @Override
    public Person createPerson(Person person) {
        BaseDocument myDocument = createDocumentFromPerson(person);
        try {
            arangoDB.db(DB_NAME).collection(COLLECTION_NAME).insertDocument(myDocument);
            personCache.put(person.getFirstname(), person);
            System.out.println("Person added to phonebook!");
        } catch (ArangoDBException exception) {
            System.err.println("Failed to create person " + exception.getMessage());
        }
        return person;
    }

    @Override
    public Person updatePerson(Person person) {
        BaseDocument dbDocument = getDocumentByPersonFirstname(person.getFirstname());
        dbDocument.updateAttribute("lastname", person.getLastname());
        dbDocument.updateAttribute("phoneNumber", person.getPhoneNumber());
        dbDocument.updateAttribute("dateOfBirth", person.getDateOfBirth().toString());
        try {
            arangoDB.db(DB_NAME).collection(COLLECTION_NAME).updateDocument(dbDocument.getKey(), dbDocument);
            personCache.get(person.getFirstname()).setLastname(person.getLastname());
            personCache.get(person.getFirstname()).setCity(person.getCity());
            personCache.get(person.getFirstname()).setPhoneNumber(person.getPhoneNumber());
            personCache.get(person.getFirstname()).setDateOfBirth(person.getDateOfBirth());
        } catch (ArangoDBException exception) {
            System.err.println("Failed to update person " + exception.getMessage());
        }
        return null;
    }

    @Override
    public void deletePersonByFirstname(String firstname) {
        BaseDocument dbDocument = getDocumentByPersonFirstname(firstname);
        try {
            arangoDB.db(DB_NAME).collection(COLLECTION_NAME).deleteDocument(dbDocument.getKey());
            personCache.remove(firstname);
        } catch (ArangoDBException exception) {
            System.err.println("Failed to delete person " + exception.getMessage());
        }
    }

    @Override
    public List<String> getPersonsFullName() {
        List<String> nameList = new ArrayList<>();
        for (Entry<String, Person> stringPersonEntry : personCache) {
            Person person = stringPersonEntry.getValue();
            nameList.add(person.getFirstname() + person.getLastname());
        }
//        try {
//            ArangoCursor<BaseDocument> cursor = arangoDB.db(DB_NAME).query(GET_FULL_NAME_QUERY, BaseDocument.class);
//            cursor.forEachRemaining(baseDocument -> {
//                nameList.add(baseDocument.getAttribute("fullname").toString());
//            });
//        } catch (ArangoDBException exception) {
//            System.err.println("Failed to execute query " + exception.getMessage());
//        }
        return nameList;
    }

    private BaseDocument createDocumentFromPerson(Person person) {
        BaseDocument document = new BaseDocument();
        document.addAttribute("firstname", person.getFirstname());
        document.addAttribute("lastname", person.getLastname());
        document.addAttribute("phoneNumber", person.getPhoneNumber());
        document.addAttribute("dateOfBirth", person.getDateOfBirth().toString());
        document.addAttribute("city", person.getCity());

        return document;
    }

    public static Person createPersonFromDocument(BaseDocument baseDocument) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return new Person.PersonBuilder()
                .firstname(baseDocument.getAttribute("firstname").toString())
                .lastname(baseDocument.getAttribute("lastname").toString())
                .phoneNumber(baseDocument.getAttribute("phoneNumber").toString())
                .dateOfBirth(LocalDate.parse(baseDocument.getAttribute("dateOfBirth").toString(), formatter))
                .city(baseDocument.getAttribute("city").toString())
                .build();
    }

    private BaseDocument getDocumentByPersonFirstname(String firstname) {
        BaseDocument baseDocument = null;
        try {
            Map<String, Object> bindVars = Collections.singletonMap("firstname", firstname);
            ArangoCursor<BaseDocument> cursor = arangoDB.db(DB_NAME).query(FIND_BY_FIRSTNAME_QUERY, bindVars, BaseDocument.class);
            baseDocument = cursor.next();
        } catch (ArangoDBException exception) {
            System.err.println("Error executing query " + exception.getMessage());
        }
        return baseDocument;
    }

    private CacheConfiguration<String, Person> getCacheConfig() {
        return new CacheConfiguration<String, Person>()
                .setReadThrough(true)
                .setWriteThrough(true)
                .setCacheMode(CacheMode.PARTITIONED)
                .setName(CACHE_NAME)
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setBackups(1);
    }

}
