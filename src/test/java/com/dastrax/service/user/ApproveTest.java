package com.dastrax.service.user;

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

import com.dastrax.app.misc.JsfUtil;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.dap.QueryParameter;
import com.dastrax.per.entity.Account;
import com.dastrax.per.entity.Token;
import com.dastrax.per.entity.User;
import com.dastrax.service.navigation.Navigator;

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
