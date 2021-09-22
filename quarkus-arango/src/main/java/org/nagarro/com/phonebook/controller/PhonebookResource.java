package org.nagarro.com.phonebook.controller;

import org.nagarro.com.phonebook.model.Person;
import org.nagarro.com.phonebook.service.PhonebookService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/persons")
public class PhonebookResource {

    @Inject
    PhonebookService phonebookService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello() {
        return "Hello RESTEasy";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPerson(Person person) {
        return Response.ok(phonebookService.addPerson(person)).build();
    }

    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPersons() {
        return Response.ok(phonebookService.getAllPersons()).build();
    }
}