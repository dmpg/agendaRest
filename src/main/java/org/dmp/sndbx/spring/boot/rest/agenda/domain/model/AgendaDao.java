package org.dmp.sndbx.spring.boot.rest.agenda.domain.model;

import java.util.Collection;

import org.dmp.sndbx.spring.boot.rest.agenda.domain.model.repo.AgendaRepository;
import org.dmp.sndbx.spring.boot.rest.agenda.domain.model.service.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgendaDao implements AgendaService {

   @Autowired
   private AgendaRepository agendaRepo;

   @Override
   public Agenda findOne(Long agendaId) {
      return agendaRepo.findOne(agendaId);
   }

   @Override
   public Collection<Agenda> findAll() {
      return agendaRepo.findAll();
   }

   @Override
   public Agenda create(Agenda agenda) {
      return agendaRepo.saveAndFlush(agenda);
   }

   @Override
   public Agenda update(Long agendaId, Agenda agenda) {
      String ownerName = agenda.getOwnerName();
      agenda = agendaRepo.findOne(agendaId);

      if (agenda != null) {
         agenda.setOwnerName(ownerName);
         return create(agenda);
      }

      return agenda;
   }

   @Override
   public void delete(Long agendaId) {
      agendaRepo.delete(agendaId);
   }
}
