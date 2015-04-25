package org.dmp.sndbx.spring.boot.rest.agenda;

import org.dmp.sndbx.spring.boot.rest.agenda.model.repo.AgendaRepository;
import org.dmp.sndbx.spring.boot.rest.agenda.model.repo.ContactoRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgendaRestApplication.class)
@WebAppConfiguration
public class AgendaRestApplicationTests {
	@Autowired
	private AgendaRepository agendaRepo;
	@Autowired
	private ContactoRepository contactoRepo;

	@Test
	public void contextLoads() {
		long agendaCnt = agendaRepo.count();
		long contactoCnt = contactoRepo.count();

		Assert.assertTrue(agendaCnt > 0L);
		Assert.assertTrue(contactoCnt > 0L);
	}

}
