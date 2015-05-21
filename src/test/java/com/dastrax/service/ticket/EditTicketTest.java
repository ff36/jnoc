package com.dastrax.service.ticket;

import java.util.ArrayList;
import java.util.List;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.dastrax.app.misc.JsfUtil;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.Comment;
import com.dastrax.per.entity.Ticket;
import com.dastrax.service.navigation.Navigator;

@RunWith(JMockit.class)
public class EditTicketTest {

	@Tested
	private EditTicket editTicket;
	private Ticket ticket;
	private Ticket ticket1;
	@Injectable
	private CrudService dap;
	@Injectable
	private Navigator navigator;
	
	@Before
	public void setUp() throws Exception {
		new MockUp<JsfUtil>() {
			@Mock
			public String getRequestParameter(String key) {
				Assert.assertEquals("ticket", key);
				return "1111";
			}
		};
		MockUp<Ticket> ticketMocked = new MockUp<Ticket>() {
			@Mock
			public void setCcEmailRecipients(List<String> ccEmailRecipients) {
		    }
		};
		ticket = ticketMocked.getMockInstance();
		ticket1 =ticketMocked.getMockInstance();
		
	}

	@Test
	public void testInit() {
		editTicket.init();
		new Verifications() {{
			ticket.setCcEmailRecipients(new ArrayList<String>());
		}};
	}

	@Mocked
	private boolean renderEditor = false;
	@Test
	public void testChangeEditor() {
		editTicket.changeEditor();
		new Verifications() {{
			ticket.setComment(new Comment());
		}};
	}

	@Injectable
	private String viewParamTicketID = "12312";
	@Ignore
	public void testUpdateComments() {
		
		new Expectations() {{
				Ticket newTicket = (Ticket) dap.find(Ticket.class, Long.valueOf(viewParamTicketID));
				result = ticket1;
				ticket1.setComment(ticket.getComment());
				ticket = ticket1;
			}
		};
		editTicket.updateComments();
	}

}
