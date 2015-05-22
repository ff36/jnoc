package com.dastrax.service.navigation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.dastrax.app.misc.CookieUtil;
import com.dastrax.app.misc.JsfUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(JMockit.class)
public class NavigatorTest {
	@Mocked
	private ResourceBundle lStrings;
	
	//@Mocked
	private ExternalContext ectx;
	@Mocked
	private FacesContext facesContext;
	
	private Navigator navigator;
	@Before
	public void setUp() throws Exception {
		
		navigator = new MockUp<Navigator>(){
			@Mock
			public void $init(){
			}
			@Mock
			public void navigate(String navigationCase, String parameter){
				assertEquals(navigationCase, "DASHBOARD");
				if(parameter!=null)
					assertEquals("sta=xc&id=1", parameter);
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

	@Mocked
	private ObjectMapper mapper;
	@Test
	public void testWriteCookie() {
		
		new MockUp<Cookie>(){
			@Mock
			public void $init(String name, String value){
				assertEquals(name, "dtx_navigation");
				//assertEquals(value, "[Create DAS]");
			}
			@Mock
			public void setComment(String purpose) {
				assertEquals(purpose, "Stores personal navigation preferences");
			} 
		};
		
		new Expectations() {
			{
				//Deencapsulation.setField(Cookie.class, "lStrings", "xxx");
				Cookie cookie = new Cookie("dtx_navigation", "[Create DAS]");
				cookie.setComment("Stores personal navigation preferences");
			}
		};
		
		navigator.writeCookie();
	}

	@Test
	public void testRedirect() throws IOException {
		
		new Expectations() {
			String path = "index.jsf";
			String url;
			{
				FacesContext.getCurrentInstance();
				result = facesContext;
				facesContext.getExternalContext();
				result = ectx;
				url = ectx.getRequestContextPath() + path;
				ectx.redirect(url);
			}
		};
		
		navigator.redirect("index.jsf");
	}

	@Test
	public void testRedirectException() {
		
		new MockUp<Logger>() {
			public void log(Level level, String msg, Throwable thrown){
				assertEquals(Level.SEVERE, level);
				assertEquals("IOException: JSFREDIRECT -> REDIRECT", msg);
			}
		};
		
		new Expectations() {
			{
				new IOException("IOException: JSFREDIRECT -> REDIRECT");
			}
		};
		
		navigator.redirect("index.jsf");
	}
	
	@Test
	public void testNavigateString() {
		navigator.navigate("DASHBOARD");
		new Verifications() {{
			navigator.navigate("DASHBOARD", null);
		}};
	}

	@Test
	public void testNavigateStringString() throws IOException {
		navigator.navigate("DASHBOARD", "sta=xc&id=1");
		
		new Verifications() {{
			ectx.redirect("http://dastrax.com/index.jsf");
		}};
	}

}
