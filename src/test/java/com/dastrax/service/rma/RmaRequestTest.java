package com.dastrax.service.rma;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.dastrax.app.email.DefaultEmailer;
import com.dastrax.app.email.Email;
import com.dastrax.app.misc.JsfUtil;
import com.dastrax.per.dap.CrudService;
import com.dastrax.service.navigation.Navigator;

@RunWith(JMockit.class)
public class RmaRequestTest {

	@Tested
	private RmaRequest rmaRequest;
	
	@Injectable
	private RMA rma;
	@Injectable
	private CrudService dap;
	@Injectable
	private Navigator navigator;
	
	@Mocked
	private ExternalContext ectx;
	@Mocked
	private FacesContext facesContext;
	@Mocked
	private Flash flash;
	
	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testRequest() {
		new MockUp<DefaultEmailer>() {
			@Mock
			public void send(Email email) {}
		};
		
		new MockUp<Email>(){
			@Mock
			public void $init(){
				
			}
		};
		
		new MockUp<JsfUtil>() {
			@Mock
			public void addSuccessMessage(String msg){
				Assert.assertEquals("Your RMA request has been received and "
                + "submitted for processing.  A support representative will"
                + " contact you during normal business hours:  Monday-Friday,"
                + " 5:00 AM PST to 5:00 PM PST.  Estimated response time is "
                + "within 2 hours.  If the matter requires immediate "
                + "attention, you may call support directly at 888-409-9997 "
                + "option 2 or email at: support@solid.com", msg);
			}
		};
		
		new Expectations() {
			
			{
				FacesContext.getCurrentInstance();
				result = facesContext;
	            facesContext.getExternalContext();
	            result = ectx;
	            ectx.getFlash();
	            result = flash;
	            flash.setKeepMessages(true);
			}
		};
		
		rmaRequest.request();
		
	}

}
