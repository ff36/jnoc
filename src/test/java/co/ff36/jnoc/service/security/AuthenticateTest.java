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


package co.ff36.jnoc.service.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import co.ff36.jnoc.app.misc.JsfUtil;
import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.dap.QueryParameter;
import co.ff36.jnoc.per.entity.User;
import co.ff36.jnoc.service.security.Authenticate;

public class AuthenticateTest {

	@Tested
	private Authenticate authenticate;
	
	@Injectable
	private CrudService dap;
	
	@Mocked
	private String stage = "DEV";
	
	//@Mocked
	private ExternalContext ectx;
	@Mocked
	private FacesContext facesContext;
	@Mocked
	private HttpServletRequest request;
	
	@Mocked
	private User user1;
	
	@Before
	public void setUp() throws Exception {
		
		new MockUp<JsfUtil>() {
			@Mock
			public String getRequestParameter(String key){
				Assert.assertEquals("email", key);
				return "xxx@xxx.com";
			}
		};
		
		subject = new MockUp<Subject>() {
			@Mock
			public boolean isAuthenticated(){
				return true;
			}
			@Mock
			public void logout(){}
		}.getMockInstance();
		
		new MockUp<SecurityUtils>() {
			@Mock
			public Subject getSubject(){
				return subject;
			}
		};
		
		ectx = new MockUp<ExternalContext>() {
			@Mock
			public String getRequestContextPath(){
				return "http://jnoc.com/";
			}
			@Mock
			public void redirect(String url){
				assertEquals("http://jnoc.com/index.jsf", url);
			}
		}.getMockInstance();
		
	}

	@Ignore
	public void testInit() {
		//TODO
		new Expectations() {{
			 ectx.getRequest();
			 request.getRequestURL();
		}};
		
		authenticate.init();
		
	}

	@Ignore
	public void testAuthenticate() {
		//TODO
		fail("Not yet implemented");
	}

	private Subject subject;
	@Test
	public void testLogout() throws IOException {
		
		authenticate.logout();
		
		new Verifications() {
			String url = "http://jnoc.com/index.jsf";
			{
				SecurityUtils.getSubject().logout();
				ectx.redirect(url);
			}
		};
		
	}

	
}
