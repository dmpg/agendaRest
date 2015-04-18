package org.dmp.sndbx.spring.boot.rest.agenda.domain.model.service;

import java.util.Collection;

import org.dmp.sndbx.spring.boot.rest.agenda.domain.model.Agenda;

public interface AgendaService {
	Agenda findOne(Long agendaId);

	Collection<Agenda> findAll();

	Agenda create(Agenda agenda);

	Agenda update(Long agendaId, Agenda agenda);

	void delete(Long agendaId);

}
