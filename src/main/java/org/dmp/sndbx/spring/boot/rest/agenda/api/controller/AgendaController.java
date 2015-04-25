package org.dmp.sndbx.spring.boot.rest.agenda.api.controller;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.Valid;

import org.dmp.sndbx.spring.boot.rest.agenda.model.assembler.Ensamblador;
import org.dmp.sndbx.spring.boot.rest.agenda.model.domain.AbstractEntity;
import org.dmp.sndbx.spring.boot.rest.agenda.model.domain.Agenda;
import org.dmp.sndbx.spring.boot.rest.agenda.model.domain.Contacto;
import org.dmp.sndbx.spring.boot.rest.agenda.model.repo.AgendaRepository;
import org.dmp.sndbx.spring.boot.rest.agenda.model.repo.ContactoRepository;
import org.dmp.sndbx.spring.boot.rest.agenda.model.validator.ContactoValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "/agendas")
public class AgendaController {
   protected final Logger log = LoggerFactory.getLogger(getClass());

   private AgendaRepository agendaRepo;
   private ContactoRepository contactoRepo;
   private Ensamblador ensamblador;
   private ContactoValidator contactoValidator;

   @Autowired
   AgendaController(AgendaRepository agendaRepo, ContactoRepository contactoRepo, Ensamblador ensamblador, ContactoValidator contactoValidator) {
      super();
      this.agendaRepo = agendaRepo;
      this.contactoRepo = contactoRepo;
      this.ensamblador = ensamblador;
      this.contactoValidator = contactoValidator;
   }

   @InitBinder
   protected void initBinder(WebDataBinder binder) {
      binder.addValidators(contactoValidator);
   }

   @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Collection<Resource<Agenda>>> getAgendas() {
      Collection<Agenda> agendas = agendaRepo.findAll();
      log.info("Agendas: {} \nContactos: {}", agendas, contactoRepo.findAll());

      Collection<Resource<Agenda>> agendaList = new ArrayList<Resource<Agenda>>();
      for (Agenda agenda : agendas) {
         agendaList.add(new Resource<>(agenda, ensamblador.ensamblar(agenda).getLinks()));
      }

      return new ResponseEntity<Collection<Resource<Agenda>>>(agendaList, HttpStatus.OK);
   }

   @RequestMapping(value = "/{agenda}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Resource<Agenda>> getAgenda(@PathVariable("agenda") Long id) {
      Agenda agenda = agendaRepo.findOne(id);
      log.info("ID Agenda en Respuesta: {}", agenda);

      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set(HttpHeaders.ETAG, agenda.getVersion().toString());

      Resource<Agenda> response = ensamblador.ensamblar(agenda);
      log.info("ID Agenda HEADERS: {}", HttpHeaders.readOnlyHttpHeaders(responseHeaders));

      return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
   }

   @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Resource<Agenda>> addAgenda(@RequestBody Agenda agenda) {
      log.info("Agenda: {}", agenda.toString());
      agenda = agendaRepo.saveAndFlush(agenda);
      log.info("Agenda creada: {}", agenda);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(agenda.getId()).toUri());

      return new ResponseEntity<>(ensamblador.ensamblar(agenda), httpHeaders, HttpStatus.OK);
   }

   @RequestMapping(value = "/{agenda}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
   public ResponseEntity<Resource<Agenda>> updateAgenda(@PathVariable("agenda") Long ownerId, @RequestBody Agenda agenda) {

      String ownerName = agenda.getOwnerName();
      agenda = agendaRepo.findOne(ownerId);
      log.info("Agenda PUT id: {} - Owner Name: {} - Retrieved: {}", ownerId, ownerName, agenda);
      agenda.setOwnerName(ownerName);

      agenda = agendaRepo.saveAndFlush(agenda);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(agenda.getId()).toUri());

      return new ResponseEntity<>(ensamblador.ensamblar(agenda), httpHeaders, HttpStatus.OK);
   }

   @RequestMapping(value = "/{agenda}", method = RequestMethod.DELETE)
   public ResponseEntity<Agenda> deleteAgenda(@PathVariable("agenda") Long id) {

      log.info("Agenda DELETE: {}", id);

      agendaRepo.delete(id);

      log.info("Agenda Eliminada: {}", id);
      return new ResponseEntity<Agenda>(HttpStatus.NO_CONTENT);
   }

   @RequestMapping(value = "/{agendaId}/contactos", method = RequestMethod.GET)
   public ResponseEntity<Collection<Resource<Contacto>>> getContactos(@PathVariable("agendaId") Long agendaId) {
      Collection<Contacto> contactos = contactoRepo.findByAgendaId(agendaId);
      log.info("GET /agendas/{}/contactos: {}", agendaId, contactos);

      Collection<Resource<Contacto>> contactoList = new ArrayList<Resource<Contacto>>();
      for (Contacto contacto : contactos) {
         contactoList.add(new Resource<>(contacto, ensamblador.ensamblar(contacto).getLinks()));
      }

      return new ResponseEntity<Collection<Resource<Contacto>>>(contactoList, HttpStatus.OK);
   }

   @RequestMapping(value = "/{agenda}/contactos/{contacto}", method = RequestMethod.GET)
   public ResponseEntity<Resource<Contacto>> getContacto(@PathVariable("agenda") Long agendaId, @PathVariable("contacto") Long contactoId) throws ResourceNotFoundException {
      for (Contacto contacto : contactoRepo.findByAgendaId(agendaId)) {
         if (contacto.getId().equals(contactoId)) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add(HttpHeaders.ETAG, contacto.getVersion().toString());
            log.info("getContacto Contacto ETag: {}", responseHeaders.getETag());

            Resource<Contacto> recurso = ensamblador.ensamblar(contacto);
            return new ResponseEntity<>(recurso, responseHeaders, HttpStatus.OK);
         }
      }

      String msg = String.format("NOT FOUND: GET /agendas/'%d'/contactos/'%d' - El contacto no existente en agenda.", agendaId, contactoId);
      log.info(msg.toString());
      throw new ResourceNotFoundException(msg.toString());
   }

   @RequestMapping(value = "/{agenda}/contactos", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Resource<Contacto>> addContacto(@PathVariable("agenda") Long agendaId, @RequestBody @Valid Contacto contacto) {
      log.info("POST Contacto: {}", contacto);

      // Forzamos que la agenda siga siendo la que corresponde
      Agenda agenda = agendaRepo.findOne(agendaId);
      contacto.setAgenda(agenda);

      contacto = contactoRepo.saveAndFlush(contacto);
      log.info("Contacto creado: {}", contacto.toString());

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(contacto.getId()).toUri());

      return new ResponseEntity<>(ensamblador.ensamblar(contacto), httpHeaders, HttpStatus.OK);
   }

   @RequestMapping(value = "/{agenda}/contactos/{contacto}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
   public ResponseEntity<Resource<Contacto>> updateContacto(@PathVariable("agenda") Long ownerId, @PathVariable("contacto") Long contactId, @RequestBody @Valid Contacto contacto) {
      String contactName = contacto.getContactName();
      String email = contacto.getEmail();
      String tel = contacto.getTel();

      contacto = contactoRepo.findOne(contactId);

      // Forzamos que la agenda siga siendo la que corresponde
      Agenda agenda = agendaRepo.findOne(ownerId);
      contacto.setAgenda(agenda);

      contacto.setContactName(contactName);
      contacto.setEmail(email);
      contacto.setTel(tel);

      log.info("Contacto PUT:  Agenda id: {} - Contacto: {}", ownerId, contacto);
      contacto = contactoRepo.saveAndFlush(contacto);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(contacto.getId()).toUri());

      return new ResponseEntity<>(ensamblador.ensamblar(contacto), httpHeaders, HttpStatus.OK);
   }

   @ExceptionHandler(ResourceNotFoundException.class)
   public ResponseEntity<Resource<? extends AbstractEntity>> handleResourceNotFoundException(ResourceNotFoundException e) {
      log.info("Handling Not Found Exception: {}", e.getMessage());
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
   }
}
