package com.dastrax.service.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.logging.Level;

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

import com.dastrax.app.misc.JsfUtil;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.dap.QueryParameter;
import com.dastrax.per.entity.User;

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
				return "http://dastrax.com/";
			}
			@Mock
			public void redirect(String url){
				assertEquals("http://dastrax.com/index.jsf", url);
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
	public void testRedirect() throws IOException {
		new Expectations() {
			//TODO 
			User user ;
			{
				user =  (User) dap.findWithNamedQuery(
		                "User.findByEmail",
		                QueryParameter.with("email", "xxxx@xxx.com")
		                .parameters())
		                .get(0);
				result = user1;
			}
		};
		
		authenticate.redirect();
		
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
			String url = "http://dastrax.com/index.jsf";
			{
				SecurityUtils.getSubject().logout();
				ectx.redirect(url);
			}
		};
		
	}

	
}
