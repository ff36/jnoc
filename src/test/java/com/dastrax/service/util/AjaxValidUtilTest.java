package com.dastrax.service.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dastrax.app.service.internal.DefaultDNSManager;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.User;

public class AjaxValidUtilTest {

	@Tested
	private AjaxValidUtil ajaxValidUtil;
	@Injectable
	private CrudService dap;
	@Injectable
	private List<String> emails = new ArrayList<String>(Arrays.asList("xxx@xxx.com", "sss@sss.com"));
	@Injectable
	private List<String> subdomains = new ArrayList<String>(Arrays.asList("dastrax"));
	
	@Before
	public void setUp() throws Exception {
		
	}

	@Injectable
	private List<User> users1;
	
	@Test
	public void testInit() {
		ajaxValidUtil.init();
		new Verifications() {
			{
				List<User> users = dap.findWithNamedQuery("User.findAll");
				users.iterator();
				new DefaultDNSManager().listRecordSets(0);
			}
		};

	}

	@Test
	public void testEmailAvailable() {
		boolean result = ajaxValidUtil.emailAvailable("xxx@xxx.com");
		Assert.assertEquals(true, result);
	}

	@Test
	public void testEmailAvailableFalse() {
		boolean result = ajaxValidUtil.emailAvailable("xxx.com");
		Assert.assertEquals(false, result);
	}
	
	@Test
	public void testSubdomainAvailable() {
		
		boolean result = ajaxValidUtil.subdomainAvailable("dastrax");
		Assert.assertEquals(true, result);
	}

	@Test
	public void testSubdomainAvailableFlase() {
		boolean result = ajaxValidUtil.subdomainAvailable("dastrax.com");
		Assert.assertEquals(false, result);
	}
	
}
