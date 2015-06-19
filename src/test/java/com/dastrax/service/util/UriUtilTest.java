package com.dastrax.service.util;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Properties;

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
		//System.out.println(icon);
		assertEquals("http://dastrax-qa.elasticbeanstalk.com/com.dastrax.dev.re90/CORE/GRAPHICS/ICONS/logo.png", icon);
	}

	@Test
	public void testImage() {
		String image = uriUtil.image("default.png", false);
		//System.out.println(image);
		assertEquals("http://dastrax-qa.elasticbeanstalk.com/com.dastrax.dev.re90/CORE/GRAPHICS/IMAGES/default.png", image);
	
	}

	@Test
	public void testLogo() {
		String logo = uriUtil.image("logo.png", false);
		//System.out.println(logo);
		assertEquals("http://dastrax-qa.elasticbeanstalk.com/com.dastrax.dev.re90/CORE/GRAPHICS/IMAGES/logo.png", logo);
	}

	@Test
	public void testProfileImageBoolean() {
		String profile = uriUtil.profileImage(false);
		//System.out.println(profile);
		//System.out.println(uriUtil.profileImage(true));
		//true: http://dastrax-qa.elasticbeanstalk.com/com.dastrax.dev.re90/CORE/GRAPHICS/IMAGES/profile_holder.svg
		//false: http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/profile_holder.svg
		assertEquals("http://dastrax-qa.elasticbeanstalk.com/com.dastrax.dev.re90/CORE/GRAPHICS/IMAGES/profile_holder.svg", profile);
	}

	@Test
	public void testProfileImageUserBoolean() {
		String profile = uriUtil.profileImage(user, true);
		//System.out.println(profile);
		//System.out.println(uriUtil.profileImage(user, false));
		//true: http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/profile_holder.svg
		//false: http://dastrax-qa.elasticbeanstalk.com/com.dastrax.dev.re90/CORE/GRAPHICS/IMAGES/profile_holder.svg
		assertEquals("http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/profile_holder.svg", profile);
	
	}

	@Test
	public void testCompanyLogoBoolean() {
		String companyLogo = uriUtil.companyLogo(false);
		//System.out.println(companyLogo);
		//System.out.println(uriUtil.companyLogo(true));
		//false http://dastrax-qa.elasticbeanstalk.com/com.dastrax.dev.re90/CORE/GRAPHICS/IMAGES/company_logo_holder.svg
		//true  http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/company_logo_holder.svg
		//assertEquals("http://dastrax-qa.elasticbeanstalk.com/com.dastrax.dev.re90/CORE/GRAPHICS/IMAGES/company_logo_holder.svg", companyLogo);
	}

	@Test
	public void testCompanyLogoCompanyBoolean() {
		String companyLogo = uriUtil.companyLogo(company, true);
		//System.out.println(companyLogo);
		//System.out.println(uriUtil.companyLogo(company, false));
		//false http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/company_logo_holder.svg
		//true  http://dastrax-qa.elasticbeanstalk.com/com.dastrax.dev.re90/CORE/GRAPHICS/IMAGES/company_logo_holder.svg
		assertEquals("http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/company_logo_holder.svg", companyLogo);
	
	}

}
