/**
 *  Copyright (C) 2015  555 inc ltd.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/


package co.ff36.jnoc.service.ticket;

import java.util.ArrayList;
import java.util.List;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.entity.Metier;
import co.ff36.jnoc.per.entity.Ticket;
import co.ff36.jnoc.per.entity.User;
import co.ff36.jnoc.per.project.JNOC;
import co.ff36.jnoc.service.ticket.TicketAnalytics;
import co.ff36.jnoc.service.ticket.TicketAnalytics.TicketDigest;

@RunWith(JMockit.class)
public class TicketAnalyticsTest {
	@Tested
	private TicketAnalytics ticketAnalytics;
	@Injectable
	private List<Ticket> tickets = new ArrayList<Ticket>();
	@Injectable
	private CrudService dap;
	@Before
	public void setUp() throws Exception {
		
		final User user = new MockUp<User>(){
			@Mock
			public Metier getMetier(){
				Metier metier = new Metier();
				metier.setName("xxxx");
				return metier;
			}
		}.getMockInstance();
		
		MockUp<Ticket> ticketMock = new MockUp<Ticket>(){
			@Mock
			public User getRequester(){
				return user;
			}
		};
		
		new MockUp<TicketAnalytics.TicketDigest>(){
			@Mock
			public void create(Ticket ticket){
			}
		};
		
		tickets.add(ticketMock.getMockInstance());
		tickets.add(ticketMock.getMockInstance());
		tickets.add(ticketMock.getMockInstance());
		tickets.add(ticketMock.getMockInstance());
		
	}

	@Test
	public void testInit() {
		
		new Expectations() {
			List<TicketDigest> digests;
			{
				dap.findWithNamedQuery("Ticket.findAll");
				result = tickets;
				digests = new ArrayList<>();
				result = digests;
				tickets.iterator();
				// Only process tickets that belong to legitimate users
                TicketDigest ticketDigest = ticketAnalytics.new TicketDigest();
                ticketDigest.create(tickets.get(0));
                digests.add(ticketDigest);
                
                tickets.get(0).getStatus();
                result = JNOC.TicketStatus.OPEN;
                
                tickets.get(0).getStatus();
                result = JNOC.TicketSeverity.S1;
                
			}
		};
		
		ticketAnalytics.init();
	}

}
