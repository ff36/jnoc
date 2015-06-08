package com.dastrax.service.feedback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.naming.InitialContext;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionContext;

import com.dastrax.per.dap.CrudService;
import com.dastrax.per.dap.QueryParameter;
import com.dastrax.per.entity.Template;
import com.dastrax.per.entity.Ticket;
import com.dastrax.per.entity.Token;
import com.dastrax.per.project.DTX;

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
			public <T> T doLookup(String name){
				return (T) dap;
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
				 
				 dap.find(Template.class, DTX.EmailTemplate.TICKET_FEEDBACK.getValue());
				 
				 tickets.iterator();
				 Deencapsulation.invoke(feedbackJob, "sendEmail", new Object[]{new Ticket(), template});
				 
			}
		};
		
		feedbackJob.execute(context);
	}

}
