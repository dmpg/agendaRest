package org.dmp.sndbx.spring.boot.rest.agenda.domain.model.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Collection;

import org.dmp.sndbx.spring.boot.rest.agenda.api.controller.AgendaController;
import org.dmp.sndbx.spring.boot.rest.agenda.domain.model.Contacto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

/**
 * Ensambla los datos del Contacto y sus Hypermedia Controllers
 * 
 * @author dmp
 *
 */
@Component
public class ContactoResourceAssembler implements ResourceAssembler<Contacto, Resource<Contacto>> {
   protected final Logger log = LoggerFactory.getLogger(getClass());

   @Override
   public Resource<Contacto> toResource(Contacto contacto) throws ResourceNotFoundException {
      Collection<Link> links = new ArrayList<Link>();
      if (null != contacto) {
         log.info("ContactoResource Assembler: Contacto = {}", contacto);
         log.info("ContactoResource Assembler: Agenda = {}", contacto.getAgenda());
         log.info("ContactoResource Assembler: Agenda ID = {}", contacto.getAgenda().getId());
         links.add(linkTo(methodOn(AgendaController.class).getContacto(contacto.getAgenda().getId(), contacto.getId())).withSelfRel());

         return new Resource<>(contacto, links);
      } else {
         log.info("Lanzando Resource Not Found Exception: contacto: {}", contacto);
         throw new ResourceNotFoundException();
      }
   }
}
