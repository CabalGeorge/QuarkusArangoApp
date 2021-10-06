package org.nagarro.com.phonebook;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.mapping.ArangoJack;
import io.quarkus.runtime.StartupEvent;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.nagarro.com.phonebook.model.Person;
import org.nagarro.com.phonebook.repository.PhonebookRepo;
import org.nagarro.com.phonebook.repository.PhonebookRepoImpl;

import javax.cache.Cache;
import javax.enterprise.event.Observes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HelloWorld {
    private static IgniteCache<String, Person> cache;

    public void onStart(@Observes StartupEvent ev) {
        IgniteConfiguration cfg = new IgniteConfiguration();

        // The node will be started as a client node.
        cfg.setClientMode(true);

        // Classes of custom Java logic will be transferred over the wire from this app.
        cfg.setPeerClassLoadingEnabled(true);

        // Setting up an IP Finder to ensure the client can locate the servers.
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

        // Starting the node
        Ignite ignite = Ignition.start(cfg);

        // Create an IgniteCache and put some values in it.
        cache = ignite.getOrCreateCache("personCache");
        loadCache();

        System.out.println(">> Created the cache and added the values.");

        System.out.println(">> Executing the compute task");

        System.out.println(
                "   Node ID: " + ignite.cluster().localNode().id() + "\n" +
                        "   OS: " + System.getProperty("os.name") +
                        "   JRE: " + System.getProperty("java.runtime.name"));

        System.out.println(">> Size: " + cache.size());
        if (cache.containsKey("Marcel")) {
            System.out.println("Has key Marcel");
        }
        if (!cache.containsKey("Ionel")) {
            System.out.println("Doesn't have key Ionel");
        }
        IgniteCache<String, BinaryObject> binaryCache = ignite.cache("personCache").withKeepBinary();
        for (Cache.Entry<String, BinaryObject> entry : binaryCache) {
            System.out.println("\n+++" + entry.getValue().field("lastname") + "++++\n");
            //TODO: Reconstruct Contact from Binary Object.
        }
    }

    private void loadCache() {
        String DB_NAME = "phonebookDB";
        String COLLECTION_NAME = "personCollection";

        ArangoDB arangoDB = new ArangoDB.Builder()
                .serializer(new ArangoJack())
                .user("root")
                .password("root")
                .build();

        String getAllQuery = "FOR p IN " + COLLECTION_NAME + " RETURN p";
        List<Person> persons = new ArrayList<>();
        try {
            ArangoCursor<BaseDocument> cursor = arangoDB.db(DB_NAME).query(getAllQuery, BaseDocument.class);
            cursor.forEachRemaining(baseDocument -> persons.add(PhonebookRepoImpl.createPersonFromDocument(baseDocument)));
            persons.forEach(person -> {
                System.out.println(person);
                cache.put(person.getFirstname(), person);
            });
        } catch (ArangoDBException exception) {
            System.err.println("Failed to execute query " + exception.getMessage());
        }
    }
}
