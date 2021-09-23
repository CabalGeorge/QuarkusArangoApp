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
    @Path("/{firstname}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByFirstname(@PathParam("firstname") String firstname) {
        return Response.ok(phonebookService.getPersonByFirstname(firstname)).build();
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

    @GET
    @Path("/fullnames")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFullNames() {
        return Response.ok(phonebookService.getFullNames()).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePerson(Person person) {
        return Response.ok(phonebookService.updatePerson(person)).build();
    }

    @DELETE
    @Path("/{firstname}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePerson(@PathParam("firstname") String firstname) {
        phonebookService.deletePerson(firstname);
        return Response.noContent().build();
    }
}