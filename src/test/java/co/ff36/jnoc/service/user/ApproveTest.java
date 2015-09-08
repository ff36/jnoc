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


package co.ff36.jnoc.service.user;

import java.util.HashMap;
import java.util.Map;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.ff36.jnoc.app.misc.JsfUtil;
import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.dap.QueryParameter;
import co.ff36.jnoc.per.entity.Account;
import co.ff36.jnoc.per.entity.Token;
import co.ff36.jnoc.per.entity.User;
import co.ff36.jnoc.service.navigation.Navigator;
import co.ff36.jnoc.service.user.Approve;

@RunWith(JMockit.class)
public class ApproveTest {
	
	private Approve approve;
	
	@Mocked
	private User user;
	//@Mocked
	private Navigator navigator;
	@Mocked
	private Account account;
	@Mocked
	private Token token;
	
	@Mocked
	private CrudService dap;

	@Before
	public void setUp(){
		new MockUp<JsfUtil>() {
			@Mock
			public String getRequestParameter(String navigationCase){
				return "dfl";
			}
			@Mock
			public void addWarningMessage(String msg){
				//System.out.println("msg:"+msg);
			}
			
		}.getMockInstance();
		
		navigator = new MockUp<Navigator>(){
			@Mock
			public void navigate(String navigationCase){
				//
			}
			
		}.getMockInstance();
		
		approve = new Approve();

	}
	
	@Test
	public void testTrueConfirmAccount(){
		
		final Map<String, String> params = new HashMap<String, String>();
		params.put("pin", "1234");
		new Expectations(){
			{
				token.getParameters();
				result = params;
//				user.updatePassword();
//				user.getAccount().setConfirmed(true);
//				user.getAccount().update();
//				token.delete();
//				System.out.println("run there...");
//				navigator.navigate("LOGIN");
			}
		};
		
		approve.setNavigator(navigator);
		approve.setCode("1234");
		approve.confirmAccount();
	
		new Verifications() {
			{
				user.getAccount(); times = 2;
			}
		};
		
	}
	
	@Test
	public void testFalseConfirmAccount(){
		approve.setNavigator(navigator);
		
		new NonStrictExpectations() {{
			JsfUtil.addWarningMessage("Invalid PIN Code");
		}};
		
		approve.setCode("12112");
		approve.confirmAccount();
		new Verifications() {
			{
				JsfUtil.addWarningMessage("Invalid PIN Code");
			}
		};
	}
	
	@Test
	public void testConfirmNewEmail(){
		
		final Map<String, String> params = new HashMap<String, String>();
		params.put("email", "1234@qq.com");
		new NonStrictExpectations(){
			{
				token.delete();
			}
		};
		
		approve.confirmNewEmail();
		
		new Verifications() {
			{
				token.delete();
				times = 1;
			}
		};
		
	}
	
	
	public void testSendLostPasswordEmail(){
		
		new Expectations() {{
			user = (User) dap.findWithNamedQuery("User.findByEmail",
                    QueryParameter.with("email", "xxx@xxx.com").parameters()).get(0);
			user.setNewEmail("xxxx@xxxx.com");
			user.newAnonymousPassword();
			
		}};
	
		approve.sendLostPasswordEmail();
		 new Verifications(){{
			 user.newAnonymousPassword();
		 }};
		
	}
	
	
}
