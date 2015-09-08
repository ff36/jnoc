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


package co.ff36.jnoc.service.company;

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
import org.junit.Test;
import org.junit.runner.RunWith;

import co.ff36.jnoc.app.security.SessionUser;
import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.dap.QueryParameter;
import co.ff36.jnoc.per.entity.Company;
import co.ff36.jnoc.per.entity.User;
import co.ff36.jnoc.per.project.JNOC.CompanyType;
import co.ff36.jnoc.service.company.CreateCompany;

@RunWith(JMockit.class)
public class CreateCompanyTest {

	@Tested
	private CreateCompany createCompany;
	
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
		
		createCompany.init();
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
		
		createCompany.init();
		Assert.assertEquals(true, this.render);
		
		new Verifications() {{
			dap.findWithNamedQuery("Company.findByType",  QueryParameter
                    .with("type", CompanyType.VAR)
                    .parameters());
			times = 0;
		}};
		
	}
	
}
