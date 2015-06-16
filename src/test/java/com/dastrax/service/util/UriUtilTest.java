package com.dastrax.service.util;

import static org.junit.Assert.assertEquals;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;

import org.junit.Before;
import org.junit.Test;

import com.dastrax.app.security.SessionUser;
import com.dastrax.per.entity.Account;
import com.dastrax.per.entity.Company;
import com.dastrax.per.entity.User;

public class UriUtilTest {

	@Tested
	private UriUtil uriUtil;
	private User user;
	@Injectable
	private Company company;
	@Injectable
	private Account account;
	@Before
	public void setUp() throws Exception {
		new MockUp<SessionUser>() {
			@Mock
			public User getCurrentUser(){
				return user;
			}
		};
		
		user = new MockUp<User>() {
			@Mock
			public Company getCompany(){
				return company;
			}
			@Mock
			public Account getAccount() {
		        return account;
		    }
		}.getMockInstance();
		
	}

	@Test
	public void testIcon() {
		String icon = uriUtil.icon("logo.png", false);
		assertEquals("http://s3.amazonaws.com/com.dastrax.dev.re90/CORE/GRAPHICS/ICONS/logo.png", icon);
	}

	@Test
	public void testImage() {
		String image = uriUtil.image("default.png", false);
		assertEquals("http://s3.amazonaws.com/com.dastrax.dev.re90/CORE/GRAPHICS/IMAGES/default.png", image);
	
	}

	@Test
	public void testLogo() {
		String logo = uriUtil.image("logo.png", false);
		assertEquals("http://s3.amazonaws.com/com.dastrax.dev.re90/CORE/GRAPHICS/IMAGES/logo.png", logo);
	}

	@Test
	public void testProfileImageBoolean() {
		String profile = uriUtil.profileImage(false);
		//true: http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/profile_holder.svg
		//false: http://s3.amazonaws.com/com.dastrax.dev.re90/CORE/GRAPHICS/IMAGES/profile_holder.svg
		assertEquals("http://s3.amazonaws.com/com.dastrax.dev.re90/CORE/GRAPHICS/IMAGES/profile_holder.svg", profile);
	}

	@Test
	public void testProfileImageUserBoolean() {
		String profile = uriUtil.profileImage(user, true);
		//true: http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/profile_holder.svg
		//false: http://s3.amazonaws.com/com.dastrax.dev.re90/CORE/GRAPHICS/IMAGES/profile_holder.svg
		assertEquals("http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/profile_holder.svg", profile);
	
	}

	@Test
	public void testCompanyLogoBoolean() {
		String companyLogo = uriUtil.companyLogo(false);
		//false http://s3.amazonaws.com/com.dastrax.dev.re90/CORE/GRAPHICS/IMAGES/company_logo_holder.svg
		//true  http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/company_logo_holder.svg
		assertEquals("http://s3.amazonaws.com/com.dastrax.dev.re90/CORE/GRAPHICS/IMAGES/company_logo_holder.svg", companyLogo);
	}

	@Test
	public void testCompanyLogoCompanyBoolean() {
		String companyLogo = uriUtil.companyLogo(company, true);
		//false http://s3.amazonaws.com/com.dastrax.dev.re90/CORE/GRAPHICS/IMAGES/company_logo_holder.svg
		//true  http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/company_logo_holder.svg
		assertEquals("http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/company_logo_holder.svg", companyLogo);
	
	}

}
