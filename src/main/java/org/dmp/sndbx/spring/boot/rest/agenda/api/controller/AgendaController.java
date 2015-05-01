package org.dmp.sndbx.spring.boot.rest.agenda.api.controller;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.Valid;

import org.dmp.sndbx.spring.boot.rest.agenda.model.assembler.AgendaAssembler;
import org.dmp.sndbx.spring.boot.rest.agenda.model.domain.AbstractEntity;
import org.dmp.sndbx.spring.boot.rest.agenda.model.domain.Agenda;
import org.dmp.sndbx.spring.boot.rest.agenda.model.domain.Contact;
import org.dmp.sndbx.spring.boot.rest.agenda.model.repo.AgendaRepository;
import org.dmp.sndbx.spring.boot.rest.agenda.model.repo.ContactRepository;
import org.dmp.sndbx.spring.boot.rest.agenda.model.validator.ContactValidator;
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
   private ContactRepository contactRepo;
   private AgendaAssembler assembler;
   private ContactValidator contactValidator;

   @Autowired
   AgendaController(AgendaRepository agendaRepo, ContactRepository contactRepo, AgendaAssembler ensamblador, ContactValidator contactValidator) {
      super();
      this.agendaRepo = agendaRepo;
      this.contactRepo = contactRepo;
      this.assembler = ensamblador;
      this.contactValidator = contactValidator;
   }

   @InitBinder
   protected void initBinder(WebDataBinder binder) {
      binder.addValidators(contactValidator);
   }

   @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Collection<Resource<Agenda>>> getAgendas() {
      Collection<Agenda> agendas = agendaRepo.findAll();
      log.info("Agendas: {} \nContacts: {}", agendas, contactRepo.findAll());

      Collection<Resource<Agenda>> agendaList = new ArrayList<Resource<Agenda>>();
      for (Agenda agenda : agendas) {
         agendaList.add(new Resource<>(agenda, assembler.assemble(agenda).getLinks()));
      }

      return new ResponseEntity<Collection<Resource<Agenda>>>(agendaList, HttpStatus.OK);
   }

   @RequestMapping(value = "/{agenda}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Resource<Agenda>> getAgenda(@PathVariable("agenda") Long id) {
      Agenda agenda = agendaRepo.findOne(id);
      log.info("ID Agenda retrived: {}", agenda);

      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set(HttpHeaders.ETAG, agenda.getVersion().toString());

      Resource<Agenda> response = assembler.assemble(agenda);
      log.info("ID Agenda HEADERS: {}", HttpHeaders.readOnlyHttpHeaders(responseHeaders));

      return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
   }

   @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Resource<Agenda>> addAgenda(@RequestBody Agenda agenda) {
      log.info("Agenda: {}", agenda.toString());
      agenda = agendaRepo.saveAndFlush(agenda);
      log.info("Agenda created: {}", agenda);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(agenda.getId()).toUri());

      return new ResponseEntity<>(assembler.assemble(agenda), httpHeaders, HttpStatus.OK);
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

      return new ResponseEntity<>(assembler.assemble(agenda), httpHeaders, HttpStatus.OK);
   }

   @RequestMapping(value = "/{agenda}", method = RequestMethod.DELETE)
   public ResponseEntity<Agenda> deleteAgenda(@PathVariable("agenda") Long id) {

      log.info("Agenda DELETE: {}", id);

      agendaRepo.delete(id);

      log.info("Agenda deleted: {}", id);
      return new ResponseEntity<Agenda>(HttpStatus.NO_CONTENT);
   }

   @RequestMapping(value = "/{agendaId}/contacts", method = RequestMethod.GET)
   public ResponseEntity<Collection<Resource<Contact>>> getContacts(@PathVariable("agendaId") Long agendaId) {
      Collection<Contact> contacts = contactRepo.findByAgendaId(agendaId);
      log.info("GET /agendas/{}/contacts: {}", agendaId, contacts);

      Collection<Resource<Contact>> contactList = new ArrayList<Resource<Contact>>();
      for (Contact contact : contacts) {
         contactList.add(new Resource<>(contact, assembler.assemble(contact).getLinks()));
      }

      return new ResponseEntity<Collection<Resource<Contact>>>(contactList, HttpStatus.OK);
   }

   @RequestMapping(value = "/{agenda}/contacts/{contact}", method = RequestMethod.GET)
   public ResponseEntity<Resource<Contact>> getContact(@PathVariable("agenda") Long agendaId, @PathVariable("contact") Long contactId) throws ResourceNotFoundException {
      for (Contact contact : contactRepo.findByAgendaId(agendaId)) {
         if (contact.getId().equals(contactId)) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add(HttpHeaders.ETAG, contact.getVersion().toString());
            log.info("getContact Contact ETag: {}", responseHeaders.getETag());

            Resource<Contact> resource = assembler.assemble(contact);
            return new ResponseEntity<>(resource, responseHeaders, HttpStatus.OK);
         }
      }

      String msg = String.format("NOT FOUND: GET /agendas/'%d'/contacts/'%d' - The contact does nto exist in agenda.", agendaId, contactId);
      log.info(msg);
      throw new ResourceNotFoundException(msg);
   }

   @RequestMapping(value = "/{agenda}/contacts", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Resource<Contact>> addContact(@PathVariable("agenda") Long agendaId, @RequestBody @Valid Contact contact) {
      log.info("POST Contact: {}", contact);

      // Forzamos que la agenda siga siendo la que corresponde
      Agenda agenda = agendaRepo.findOne(agendaId);
      contact.setAgenda(agenda);

      contact = contactRepo.saveAndFlush(contact);
      log.info("Contact created: {}", contact.toString());

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(contact.getId()).toUri());

      return new ResponseEntity<>(assembler.assemble(contact), httpHeaders, HttpStatus.OK);
   }

   @RequestMapping(value = "/{agenda}/contacts/{contact}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
   public ResponseEntity<Resource<Contact>> updateContact(@PathVariable("agenda") Long ownerId, @PathVariable("contact") Long contactId, @RequestBody @Valid Contact contact) {
      String contactName = contact.getContactName();
      String email = contact.getEmail();
      String tel = contact.getTel();

      contact = contactRepo.findOne(contactId);

      if (null == contact) {
         String msg = String.format("Contact (id '%d' - owner '%d') should exist previously to update.", contactId, ownerId);
         log.info(msg);
         throw new ResourceNotFoundException(msg);
      }

      // Forzamos que la agenda siga siendo la que corresponde
      Agenda agenda = agendaRepo.findOne(ownerId);
      contact.setAgenda(agenda);

      contact.setContactName(contactName);
      contact.setEmail(email);
      contact.setTel(tel);

      log.info("Contact PUT:  Agenda id: {} - Contact: {}", ownerId, contact);
      contact = contactRepo.saveAndFlush(contact);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(contact.getId()).toUri());

      return new ResponseEntity<>(assembler.assemble(contact), httpHeaders, HttpStatus.OK);
   }

   @ExceptionHandler(ResourceNotFoundException.class)
   public ResponseEntity<Resource<? extends AbstractEntity>> handleResourceNotFoundException(ResourceNotFoundException e) {
      log.info("Not Found Exception: {}", e.getMessage());
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
   }
}
