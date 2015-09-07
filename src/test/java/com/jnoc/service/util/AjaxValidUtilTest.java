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


//package com.jnoc.service.util;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import mockit.Injectable;
//import mockit.Tested;
//import mockit.Verifications;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import com.jnoc.app.service.internal.DefaultDNSManager;
//import com.jnoc.per.dap.CrudService;
//import com.jnoc.per.entity.User;
//
//public class AjaxValidUtilTest {
//
//	@Tested
//	private AjaxValidUtil ajaxValidUtil;
//	@Injectable
//	private CrudService dap;
//	@Injectable
//	private List<String> emails = new ArrayList<String>(Arrays.asList("xxx@xxx.com", "sss@sss.com"));
//	@Injectable
//	private List<String> subdomains = new ArrayList<String>(Arrays.asList("jnoc"));
//	
//	@Before
//	public void setUp() throws Exception {
//		
//	}
//
//	@Injectable
//	private List<User> users1;
//	
//	@Test
//	public void testInit() {
//		ajaxValidUtil.init();
//		new Verifications() {
//			{
//				List<User> users = dap.findWithNamedQuery("User.findAll");
//				users.iterator();
//				new DefaultDNSManager().listRecordSets(0);
//			}
//		};
//
//	}
//
//	@Test
//	public void testEmailAvailable() {
//		boolean result = ajaxValidUtil.emailAvailable("xxx@xxx.com");
//		Assert.assertEquals(true, result);
//	}
//
//	@Test
//	public void testEmailAvailableFalse() {
//		boolean result = ajaxValidUtil.emailAvailable("xxx.com");
//		Assert.assertEquals(false, result);
//	}
//	
//	@Test
//	public void testSubdomainAvailable() {
//		
//		boolean result = ajaxValidUtil.subdomainAvailable("jnoc");
//		Assert.assertEquals(true, result);
//	}
//
//	@Test
//	public void testSubdomainAvailableFlase() {
//		boolean result = ajaxValidUtil.subdomainAvailable("jnoc.com");
//		Assert.assertEquals(false, result);
//	}
//	
//}
