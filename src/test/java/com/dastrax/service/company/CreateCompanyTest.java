package com.dastrax.service.company;

import java.util.List;

import javax.mail.Session;

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
import org.junit.Test;
import org.junit.runner.RunWith;

import com.dastrax.app.security.SessionUser;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.dap.QueryParameter;
import com.dastrax.per.entity.Company;
import com.dastrax.per.entity.User;
import com.dastrax.per.project.DTX.CompanyType;

@RunWith(JMockit.class)
public class CreateCompanyTest {

	@Tested
	private CreateCompany cc;
	
	@Mocked
	private Company company;
	@Mocked
	private List<Company> vars;
	@Injectable
	private CrudService dap;
	@Mocked
	private User user;
	@Mocked
	private boolean render;
	
	@Before
	public void setUp(){
		
		new MockUp<SessionUser>(){
			@Mock
			public User getCurrentUser(){
				return user;
			}
		};

	}
	
	@Test
	public void testInit() {
		
		new Expectations() {{
			SessionUser.getCurrentUser().isVAR();
			result = true;
			
			SessionUser.getCurrentUser().isAdministrator();
			result =  true;
			
			render = true;
			
		}};
		
		cc.init();
		Assert.assertEquals(true, this.render);
		
		new Verifications() {{
			company.setNewParent(SessionUser.getCurrentUser().getCompany());
			dap.findWithNamedQuery("Company.findByType",  QueryParameter
                    .with("type", CompanyType.VAR)
                    .parameters());
		}};
		
	}

	@Test
	public void testInitFalse() {
		
		new Expectations() {{
			SessionUser.getCurrentUser().isVAR();
			result = false;
			
			SessionUser.getCurrentUser().isAdministrator();
			result =  false;
			
			render = true;
			
		}};
		
		cc.init();
		Assert.assertEquals(true, this.render);
		
		new Verifications() {{
			dap.findWithNamedQuery("Company.findByType",  QueryParameter
                    .with("type", CompanyType.VAR)
                    .parameters());
			times = 0;
		}};
		
	}
	
}
