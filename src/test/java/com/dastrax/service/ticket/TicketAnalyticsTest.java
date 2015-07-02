package com.dastrax.service.ticket;

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

import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.Metier;
import com.dastrax.per.entity.Ticket;
import com.dastrax.per.entity.User;
import com.dastrax.per.project.DTX;
import com.dastrax.service.ticket.TicketAnalytics.TicketDigest;

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
                result = DTX.TicketStatus.OPEN;
                
                tickets.get(0).getStatus();
                result = DTX.TicketSeverity.S1;
                
			}
		};
		
		ticketAnalytics.init();
	}

}
