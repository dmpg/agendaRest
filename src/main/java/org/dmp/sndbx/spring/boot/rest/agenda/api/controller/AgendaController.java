package org.dmp.sndbx.spring.boot.rest.agenda.api.controller;

import java.util.ArrayList;
import java.util.Collection;

import org.dmp.sndbx.spring.boot.rest.agenda.domain.model.AbstractEntity;
import org.dmp.sndbx.spring.boot.rest.agenda.domain.model.Agenda;
import org.dmp.sndbx.spring.boot.rest.agenda.domain.model.Contacto;
import org.dmp.sndbx.spring.boot.rest.agenda.domain.model.assembler.Ensamblador;
import org.dmp.sndbx.spring.boot.rest.agenda.domain.model.repo.AgendaRepository;
import org.dmp.sndbx.spring.boot.rest.agenda.domain.model.repo.ContactoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgendaController {
   protected final Logger log = LoggerFactory.getLogger(getClass());

   @Autowired
   private AgendaRepository agendaRepo;
   @Autowired
   private ContactoRepository contactoRepo;
   @Autowired
   private Ensamblador ensamblador;

   @RequestMapping(value = "/agendas", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Collection<Resource<Agenda>>> getAgendas() {
      Collection<Agenda> agendas = agendaRepo.findAll();
      log.info("Agendas: {}", agendas);
      log.info("Contactos: {}", contactoRepo.findAll());

      Collection<Resource<Agenda>> agendaList = new ArrayList<Resource<Agenda>>();
      for (Agenda agenda : agendas) {
         agendaList.add(new Resource<>(agenda, ensamblador.ensamblar(agenda).getLinks()));
      }

      log.info("Saliendo de /agendas - getAgendas()...");
      return new ResponseEntity<Collection<Resource<Agenda>>>(agendaList, HttpStatus.OK);
   }

   @RequestMapping(value = "/agendas/{agenda}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Resource<Agenda>> getAgenda(@PathVariable("agenda") Long id) {
      Agenda agenda = agendaRepo.findOne(id);
      log.info("ID Agenda en Respuesta: {}", agenda);

      HttpHeaders responseHeaders = new HttpHeaders();
      // responseHeaders.setLocation(location);
      responseHeaders.set("ETag", agenda.getVersion().toString());

      return new ResponseEntity<>(ensamblador.ensamblar(agenda), responseHeaders, HttpStatus.OK);
   }

   @RequestMapping(value = "/agendas", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Resource<Agenda>> addAgenda(@RequestBody Agenda agenda) {
      log.info("Agenda: {}", agenda.toString());
      Agenda agendaNueva = agendaRepo.saveAndFlush(agenda);
      log.info("Agenda creada: {}", agendaNueva.toString());

      return new ResponseEntity<>(ensamblador.ensamblar(agendaNueva), HttpStatus.OK);
   }

   @RequestMapping(value = "/agendas/{agenda}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
   public ResponseEntity<Resource<Agenda>> updateAgenda(@PathVariable("agenda") Long ownerId, @RequestBody Agenda agenda) {

      String ownerName = agenda.getOwnerName();
      log.info("1.- Agenda PUT id: {} - Owner Name: {} - Body: {}", ownerId, ownerName, agenda);
      agenda = agendaRepo.findOne(ownerId);
      log.info("2.- Agenda PUT id: {} - Owner Name: {} - Retrieved: {}", ownerId, ownerName, agenda);
      agenda.setOwnerName(ownerName);
      log.info("3.- Agenda PUT id: {} - Nueva Agenda: {}", ownerId, agenda);


      agenda = agendaRepo.saveAndFlush(agenda);

      return new ResponseEntity<>(ensamblador.ensamblar(agenda), HttpStatus.OK);
   }

   @RequestMapping(value = "/agendas/{agenda}", method = RequestMethod.DELETE)
   public ResponseEntity<Agenda> deleteAgenda(@PathVariable("agenda") Long id) {

      log.info("Agenda DELETE: {}", id);

      agendaRepo.delete(id);

      log.info("Agenda Eliminada: {}", id);
      return new ResponseEntity<Agenda>(HttpStatus.NO_CONTENT);
   }

   @RequestMapping(value = "/agendas/{agendaId}/contactos", method = RequestMethod.GET)
   public ResponseEntity<Collection<Resource<Contacto>>> getContactos(@PathVariable("agendaId") Long agendaId) {
      Collection<Contacto> contactos = contactoRepo.findByAgendaId(agendaId);
      log.info("GET /agendas/{}/contactos: {}", agendaId, contactos);

      Collection<Resource<Contacto>> contactoList = new ArrayList<Resource<Contacto>>();
      for (Contacto contacto : contactos) {
         contactoList.add(new Resource<>(contacto, ensamblador.ensamblar(contacto).getLinks()));
      }

      return new ResponseEntity<Collection<Resource<Contacto>>>(contactoList, HttpStatus.OK);
   }

   @RequestMapping(value = "/agendas/{agenda}/contactos/{contacto}", method = RequestMethod.GET)
   public ResponseEntity<Resource<Contacto>> getContacto(@PathVariable("agenda") Long agendaId, @PathVariable("contacto") Long contactoId) {
      for (Contacto contacto : contactoRepo.findByAgendaId(agendaId)) {
         if (contacto.getId().equals(contactoId)) {
            HttpHeaders responseHeaders = new HttpHeaders();
            // responseHeaders.setLocation(location);
            responseHeaders.set("ETag", contacto.getVersion().toString());
            return new ResponseEntity<>(ensamblador.ensamblar(contacto), responseHeaders, HttpStatus.OK);
         }
      }

      StringBuilder msg = new StringBuilder("NOT FOUND: GET /agendas/").append(agendaId).append("/contactos/").append(contactoId).append(" - El contacto no existente en agenda.");
      log.info(msg.toString());
      throw new ResourceNotFoundException(msg.toString());
   }

   @ExceptionHandler(ResourceNotFoundException.class)
   public ResponseEntity<Resource<? extends AbstractEntity>> handleResourceNotFoundException(ResourceNotFoundException e) {
      log.info("Handling Not Found Exception: {}", e.getMessage());
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
   }
}
