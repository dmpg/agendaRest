package org.dmp.sndbx.spring.boot.rest.agenda.domain.model.repo;

import java.util.Collection;
import java.util.List;

import org.dmp.sndbx.spring.boot.rest.agenda.domain.model.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "contactos")
public interface ContactoRepository extends JpaRepository<Contacto, Long> {

   // @RestResource(exported = false) <-- Para que no se exponga este finder
   @RestResource(path = "owners")
   // Para que el GET sea en "owners" y no en "findByAgendaIsNotNull"
   Collection<Contacto> findByAgendaIsNotNull();

   // El siguiente responde al request de:
   // http://localhost:8080/contactos/search/agenda:> get --params "{ownerId:1}"
   @RestResource(path = "agenda")
   // Para que el GET sea en "agenda" y no en "findByAgendaId"
   public List<Contacto> findByAgendaId(@Param("ownerId") Long ownerId);

   // El anterior "findByAgendaId" es igual a hacer el siguiente:
   @Query("select c from Contacto c where  c.agenda.id = :ownerId")
   @RestResource(path = "querier")
   // Para que el GET sea en "querier" y no en "search"
   List<Contacto> search(@Param("ownerId") Long ownerId);

   // El siguiente responde al request de:
   // http://localhost:8080/contactos/search/ownername:> get --params
   // "{ownerName:"Arya Stark"}"
   @Query("select c from Contacto c where  c.agenda.ownerName = :ownerName)")
   @RestResource(path = "ownername")
   // Para que el GET sea en "ownername" y no en "search"
   List<Contacto> searchByName(@Param("ownerName") String ownerName);

   // @Query
   // ("select c from Contacto c where  c.agenda.ownerId = :ownerId and (c.contactoName LIKE :q   )")
   // List<Contacto> search(@Param("ownerId") Long ownerId, @Param("q") String
   // query);

}
