package org.dmp.sndbx.spring.boot.rest.agenda.model.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Collection;

import org.dmp.sndbx.spring.boot.rest.agenda.api.controller.AgendaController;
import org.dmp.sndbx.spring.boot.rest.agenda.model.domain.Agenda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

/**
 * Ensambla los datos de la agenda y sus Hypermedia Controllers
 * 
 * @author dmp
 *
 */
@Component
public class AgendaResourceAssembler implements ResourceAssembler<Agenda, Resource<Agenda>> {

   @Autowired
   private EntityLinks entityLinks;

   @Override
   public Resource<Agenda> toResource(Agenda agenda) throws ResourceNotFoundException {
      Collection<Link> links = new ArrayList<Link>();
      if (null != agenda) {
         Link selfLink = entityLinks.linkFor(Agenda.class).slash(agenda.getId()).withSelfRel();
         links.add(selfLink);
         links.add(linkTo(methodOn(AgendaController.class).getContacts(agenda.getId())).withRel("contactos"));

         return new Resource<>(agenda, links);
      } else {
         throw new ResourceNotFoundException();
      }
   }
}
