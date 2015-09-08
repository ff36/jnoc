package co.ff36.jnoc.service.util;
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


//package co.ff36.jnoc.service.util;
//
//import static org.junit.Assert.assertEquals;
//
//import java.util.Map;
//import java.util.Properties;
//
//import mockit.Injectable;
//import mockit.Mock;
//import mockit.MockUp;
//import mockit.Tested;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import co.ff36.jnoc.app.security.SessionUser;
//import co.ff36.jnoc.per.entity.Account;
//import co.ff36.jnoc.per.entity.Company;
//import co.ff36.jnoc.per.entity.User;
//
//public class UriUtilTest {
//	
//	@Tested
//	private UriUtil uriUtil;
//	private User user;
//	@Injectable
//	private Company company;
//	@Injectable
//	private Account account;
//	@Before
//	public void setUp() throws Exception {
//		new MockUp<SessionUser>() {
//			@Mock
//			public User getCurrentUser(){
//				return user;
//			}
//		};
//		
//		user = new MockUp<User>() {
//			@Mock
//			public Company getCompany(){
//				return company;
//			}
//			@Mock
//			public Account getAccount() {
//		        return account;
//		    }
//		}.getMockInstance();
//		
//	}
//
//	@Test
//	public void testIcon() {
//		String icon = uriUtil.icon("logo.png", false);
//		//System.out.println(icon);
//		assertEquals("http://jnoc-qa.elasticbeanstalk.com/com.jnoc.dev.re90/CORE/GRAPHICS/ICONS/logo.png", icon);
//	}
//
//	@Test
//	public void testImage() {
//		String image = uriUtil.image("default.png", false);
//		//System.out.println(image);
//		assertEquals("http://jnoc-qa.elasticbeanstalk.com/com.jnoc.dev.re90/CORE/GRAPHICS/IMAGES/default.png", image);
//	
//	}
//
//	@Test
//	public void testLogo() {
//		String logo = uriUtil.image("logo.png", false);
//		//System.out.println(logo);
//		assertEquals("http://jnoc-qa.elasticbeanstalk.com/com.jnoc.dev.re90/CORE/GRAPHICS/IMAGES/logo.png", logo);
//	}
//
//	@Test
//	public void testProfileImageBoolean() {
//		String profile = uriUtil.profileImage(false);
//		//System.out.println(profile);
//		//System.out.println(uriUtil.profileImage(true));
//		//true: http://jnoc-qa.elasticbeanstalk.com/com.jnoc.dev.re90/CORE/GRAPHICS/IMAGES/profile_holder.svg
//		//false: http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/profile_holder.svg
//		assertEquals("http://jnoc-qa.elasticbeanstalk.com/com.jnoc.dev.re90/CORE/GRAPHICS/IMAGES/profile_holder.svg", profile);
//	}
//
//	@Test
//	public void testProfileImageUserBoolean() {
//		String profile = uriUtil.profileImage(user, true);
//		//System.out.println(profile);
//		//System.out.println(uriUtil.profileImage(user, false));
//		//true: http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/profile_holder.svg
//		//false: http://jnoc-qa.elasticbeanstalk.com/com.jnoc.dev.re90/CORE/GRAPHICS/IMAGES/profile_holder.svg
//		assertEquals("http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/profile_holder.svg", profile);
//	
//	}
//
//	@Test
//	public void testCompanyLogoBoolean() {
//		String companyLogo = uriUtil.companyLogo(false);
//		//System.out.println(companyLogo);
//		//System.out.println(uriUtil.companyLogo(true));
//		//false http://jnoc-qa.elasticbeanstalk.com/com.jnoc.dev.re90/CORE/GRAPHICS/IMAGES/company_logo_holder.svg
//		//true  http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/company_logo_holder.svg
//		//assertEquals("http://jnoc-qa.elasticbeanstalk.com/com.jnoc.dev.re90/CORE/GRAPHICS/IMAGES/company_logo_holder.svg", companyLogo);
//	}
//
//	@Test
//	public void testCompanyLogoCompanyBoolean() {
//		String companyLogo = uriUtil.companyLogo(company, true);
//		//System.out.println(companyLogo);
//		//System.out.println(uriUtil.companyLogo(company, false));
//		//false http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/company_logo_holder.svg
//		//true  http://jnoc-qa.elasticbeanstalk.com/com.jnoc.dev.re90/CORE/GRAPHICS/IMAGES/company_logo_holder.svg
//		assertEquals("http://d39qej2brds8l8.cloudfront.net/CORE/GRAPHICS/IMAGES/company_logo_holder.svg", companyLogo);
//	
//	}
//
//}
