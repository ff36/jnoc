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


package com.jnoc.service.ticket;

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

import com.jnoc.app.misc.JsfUtil;
import com.jnoc.per.dap.CrudService;
import com.jnoc.per.entity.Comment;
import com.jnoc.per.entity.Ticket;
import com.jnoc.service.navigation.Navigator;

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
