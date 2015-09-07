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


package com.jnoc.service.feedback;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionContext;

import com.jnoc.per.dap.CrudService;
import com.jnoc.per.dap.QueryParameter;
import com.jnoc.per.entity.Template;
import com.jnoc.per.entity.Ticket;
import com.jnoc.per.project.JNOC;

@RunWith(JMockit.class)
public class FeedbackJobTest {

	private FeedbackJob feedbackJob;
	@Mocked
	private JobExecutionContext context;
	@Mocked
	CrudService dap;

	@Mocked
	private Template template;

	private List<Ticket> tickets1;
	@Before
	public void setUp(){

		new MockUp<FeedbackJob>(){
			
			@Mock
			private void sendEmail(Ticket ticket, Template template){
			}
		};
		
		new MockUp<Ticket>() {
			@Mock
			void $init(){
			}
		};
		
		new MockUp<InitialContext>() {
			@Mock
			public <T> CrudService doLookup(String name){
				return dap;
			} 
		};
		feedbackJob = new FeedbackJob();
		tickets1 = new ArrayList<Ticket>();
		tickets1.add(new Ticket());
		tickets1.add(new Ticket());
		tickets1.add(new Ticket());
		tickets1.add(new Ticket());
	}
	
	@Test
	public void testExecute() throws Exception {
		
		new Expectations() {
			{
				 CrudService dap = InitialContext.doLookup("xxx");
				 List<Ticket> tickets = dap.findWithNamedQuery(
                    "Ticket.findAllByCloseRange",
                    QueryParameter
                    .with("start", any)
                    .and("end", any)
                    .parameters());
				 
				 dap.find(Template.class, JNOC.EmailTemplate.TICKET_FEEDBACK.getValue());
				 
				 tickets.iterator();
				 Deencapsulation.invoke(feedbackJob, "sendEmail", new Object[]{new Ticket(), template});
				 
			}
		};
		
		feedbackJob.execute(context);
	}

}
