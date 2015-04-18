package org.dmp.sndbx.spring.boot.rest.agenda.domain.model.assembler;

import org.dmp.sndbx.spring.boot.rest.agenda.domain.model.Agenda;
import org.dmp.sndbx.spring.boot.rest.agenda.domain.model.Contacto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;

@Component
public class Ensamblador {
   @Autowired
   private AgendaResourceAssembler agendaResourceAssembler;
   @Autowired
   private ContactoResourceAssembler contactoResourceAssembler;

   public Resource<Agenda> ensamblar(Agenda agenda) throws ResourceNotFoundException {
      return agendaResourceAssembler.toResource(agenda);
   }

   public Resource<Contacto> ensamblar(Contacto contacto) throws ResourceNotFoundException {
      return contactoResourceAssembler.toResource(contacto);
   }
}