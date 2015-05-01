package org.dmp.sndbx.spring.boot.rest.agenda.model.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Collection;

import org.dmp.sndbx.spring.boot.rest.agenda.api.controller.AgendaController;
import org.dmp.sndbx.spring.boot.rest.agenda.model.domain.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

/**
 * Assembles Contact data and Hypermedia Controllers
 * 
 * @author dmp
 *
 */
@Component
public class ContactResourceAssembler implements ResourceAssembler<Contact, Resource<Contact>> {
   protected final Logger log = LoggerFactory.getLogger(getClass());

   @Override
   public Resource<Contact> toResource(Contact contact) throws ResourceNotFoundException {
      Collection<Link> links = new ArrayList<Link>();
      if (null != contact) {
         log.info("ContactResource Assembler: Contact = {}", contact);
         log.info("ContactResource Assembler: Agenda = {}", contact.getAgenda());
         log.info("ContactResource Assembler: Agenda ID = {}", contact.getAgenda().getId());
         links.add(linkTo(methodOn(AgendaController.class).getContact(contact.getAgenda().getId(), contact.getId())).withSelfRel());

         return new Resource<>(contact, links);
      } else {
         String msg = String.format("Contact ('%d') not found.", contact);
         log.info(msg);
         throw new ResourceNotFoundException(msg);
      }
   }
}
