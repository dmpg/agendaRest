package org.dmp.sndbx.spring.boot.rest.agenda.model.assembler;

import org.dmp.sndbx.spring.boot.rest.agenda.model.domain.Agenda;
import org.dmp.sndbx.spring.boot.rest.agenda.model.domain.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;

@Component
public class AgendaAssembler {
   @Autowired
   private AgendaResourceAssembler agendaResourceAssembler;
   @Autowired
   private ContactResourceAssembler contactResourceAssembler;

   public Resource<Agenda> assemble(Agenda agenda) throws ResourceNotFoundException {
      return agendaResourceAssembler.toResource(agenda);
   }

   public Resource<Contact> assemble(Contact contact) throws ResourceNotFoundException {
      return contactResourceAssembler.toResource(contact);
   }
}