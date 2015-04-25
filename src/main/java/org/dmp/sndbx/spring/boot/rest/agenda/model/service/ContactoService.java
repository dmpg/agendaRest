package org.dmp.sndbx.spring.boot.rest.agenda.model.service;

import java.util.Collection;

import org.dmp.sndbx.spring.boot.rest.agenda.model.domain.Contacto;

public interface ContactoService {
	Contacto findOne(Long contactoId);

	Collection<Contacto> findAll();

	Contacto create(Contacto contacto);

	Contacto update(Long contactoId, Contacto contacto);

	void delete(Long contactoId);

	Collection<Contacto> findContactoListByOwnerId(Long ownerId);

	Contacto findOwnerByContactId(Long ownerId, Long contactoId);
}
