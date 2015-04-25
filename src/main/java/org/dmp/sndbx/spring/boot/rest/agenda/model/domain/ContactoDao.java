package org.dmp.sndbx.spring.boot.rest.agenda.model.domain;

import java.util.ArrayList;
import java.util.Collection;

import org.dmp.sndbx.spring.boot.rest.agenda.model.repo.ContactoRepository;
import org.dmp.sndbx.spring.boot.rest.agenda.model.service.ContactoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactoDao implements ContactoService {
   protected final Logger log = LoggerFactory.getLogger(getClass());

   @Autowired
   private ContactoRepository contactoRepo;

   @Override
   public Contacto findOne(Long contactoId) {
      return contactoRepo.findOne(contactoId);
   }

   @Override
   public Collection<Contacto> findAll() {
      return contactoRepo.findAll();
   }

   @Override
   public Contacto create(Contacto contacto) {
      return contactoRepo.saveAndFlush(contacto);
   }

   @Override
   public Contacto update(Long contactoId, Contacto contacto) {
      Contacto contactoAux = contactoRepo.findOne(contactoId);
      contactoAux.setContactName(contacto.getContactName());
      contactoAux.setEmail(contacto.getEmail());
      contactoAux.setTel(contacto.getTel());

      return create(contacto);
   }

   @Override
   public void delete(Long contactoId) {
      contactoRepo.delete(contactoId);
   }

   @Override
   public Collection<Contacto> findContactoListByOwnerId(Long ownerId) {
      Collection<Contacto> contactos = new ArrayList<Contacto>();
      for (Contacto contacto : contactoRepo.findAll()) {
         log.info("Find Contacto List By Owner Id - Contacto: {}", contacto);
         if (contacto.getAgenda().getId().equals(ownerId)) {
            contactos.add(contacto);
         }
      }

      return contactos;
   }

   @Override
   public Contacto findOwnerByContactId(Long ownerId, Long contactoId) {
      Contacto contacto = contactoRepo.findOne(contactoId);

      log.info(
            "Find Contacto By Owner Id - ownerId: {} - contactoId: {} - contacto.agenda.id: {}/nContacto encontrado: {}",
            ownerId, contactoId, contacto.getAgenda().getId(), contacto);
      if (contacto != null && ownerId.equals(contacto.getAgenda().getId())) {
         return contacto;
      }

      log.info(
            "Retornando null - Owner no coincide: ownerId {} - agendaId: {}",
            ownerId, contacto.getAgenda().getId());
      return null;
   }

}