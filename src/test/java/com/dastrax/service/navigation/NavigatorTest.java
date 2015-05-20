package com.dastrax.service.navigation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.servlet.http.Cookie;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.dastrax.app.misc.CookieUtil;
import com.dastrax.app.misc.JsfUtil;

@RunWith(JMockit.class)
public class NavigatorTest {
	
	private Navigator navigator;
	@Before
	public void setUp() throws Exception {
		
		navigator = new MockUp<Navigator>(){
			@Mock
			public void $init(){
			}
		}.getMockInstance();
		
		new MockUp<JsfUtil>() {
			@Mock
			public void addErrorMessage(String msg){
				assertEquals(msg, "We where unable to update your navigation preferences");
			} 
		};
		new MockUp<CookieUtil>() {
			@Mock
			public boolean writeCookie(Cookie cookie){
				return false;
			} 
		};
	}

	@Test
	public void testWriteCookie() {
		
		new MockUp<Cookie>(){
			@Mock
			public void $init(String name, String value){
				assertEquals(name, "dtx_navigation");
				assertEquals(value, "[Create DAS]");
			}
			@Mock
			public void setComment(String purpose) {
				assertEquals(purpose, "Stores personal navigation preferences");
			} 
		};
		
		new Expectations() {
			{
				Cookie cookie = new Cookie("dtx_navigation", "[Create DAS]");
				cookie.setComment("Stores personal navigation preferences");
			}
		};
		
		navigator.writeCookie();
	}

	@Test
	public void testRedirect() {
		fail("Not yet implemented");
	}

	@Test
	public void testNavigateString() {
		fail("Not yet implemented");
	}

	@Test
	public void testNavigateStringString() {
		fail("Not yet implemented");
	}

}
