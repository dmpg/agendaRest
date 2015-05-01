package org.dmp.sndbx.spring.boot.rest.agenda.model.repo;

import java.util.Collection;
import java.util.List;

import org.dmp.sndbx.spring.boot.rest.agenda.model.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "contacts")
public interface ContactRepository extends JpaRepository<Contact, Long> {

   // @RestResource(exported = false) <-- Para que no se exponga este finder
   @RestResource(path = "owners")
   // Para que el GET sea en "owners" y no en "findByAgendaIsNotNull"
   Collection<Contact> findByAgendaIsNotNull();

   // El siguiente responde al request de:
   // http://localhost:8080/contacts/search/agenda:> get --params "{ownerId:1}"
   @RestResource(path = "agenda")
   // Para que el GET sea en "agenda" y no en "findByAgendaId"
   public List<Contact> findByAgendaId(@Param("ownerId") Long ownerId);

   // El anterior "findByAgendaId" es igual a hacer el siguiente:
   @Query("select c from Contact c where  c.agenda.id = :ownerId")
   @RestResource(path = "querier")
   // Para que el GET sea en "querier" y no en "search"
   List<Contact> search(@Param("ownerId") Long ownerId);

   // El siguiente responde al request de:
   // http://localhost:8080/contacts/search/ownername:> get --params
   // "{ownerName:"Arya Stark"}"
   @Query("select c from Contact c where  c.agenda.ownerName = :ownerName)")
   @RestResource(path = "ownername")
   // Para que el GET sea en "ownername" y no en "search"
   List<Contact> searchByName(@Param("ownerName") String ownerName);

   // @Query
   // ("select c from Contact c where  c.agenda.ownerId = :ownerId and (c.contactName LIKE :q   )")
   // List<Contact> search(@Param("ownerId") Long ownerId, @Param("q") String
   // query);

}
