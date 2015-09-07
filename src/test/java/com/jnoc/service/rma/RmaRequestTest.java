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


package com.jnoc.service.rma;

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

import com.jnoc.app.email.DefaultEmailer;
import com.jnoc.app.email.Email;
import com.jnoc.app.misc.JsfUtil;
import com.jnoc.app.security.SessionUser;
import com.jnoc.per.dap.CrudService;
import com.jnoc.per.entity.User;
import com.jnoc.service.navigation.Navigator;

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
	@Mocked
	private User user;
	
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
		
		new MockUp<SessionUser>(){
			@Mock
			public User getCurrentUser(){
				return user;
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
