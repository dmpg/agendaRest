package org.dmp.sndbx.spring.boot.rest.agenda.model.repo;

import org.dmp.sndbx.spring.boot.rest.agenda.model.domain.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "agendas")
public interface AgendaRepository extends JpaRepository<Agenda, Long> {

}
