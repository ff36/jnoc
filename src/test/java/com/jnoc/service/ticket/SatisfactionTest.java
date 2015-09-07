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


package com.jnoc.service.ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jnoc.app.misc.JsfUtil;
import com.jnoc.per.dap.CrudService;
import com.jnoc.per.entity.Ticket;
import com.jnoc.per.entity.Token;

@RunWith(JMockit.class)
public class SatisfactionTest {

	@Tested
	private Satisfaction satisfaction;
	@Injectable
	private CrudService dap;
	@Injectable
	private Token token;
	@Injectable
	private Ticket ticket;
	private Map<String, List<String>> requestParameters;
	
	private List<String> list = new ArrayList<String>();
	@Before
	public void setUp() throws Exception {
		list.add("1111");
		list.add("2222");
		new MockUp<JsfUtil>() {
			@Mock
			public Map<String, List<String>> getRequestParameters(){
				return null;
			}
			@Mock
			public void addSuccessMessage(String msg){
				Assert.assertEquals("Please add a feedback rating between 1 and 5.", msg);
			}
		};
		
		requestParameters = new MockUp<Map<String, List<String>>>() {
			@Mock
			public List<String> get(Object key){
				return list;
			} 
		}.getMockInstance();
		
	}

	@Test
	public void testInit() {
		satisfaction.init();
		new Verifications() {{
				Integer.valueOf(requestParameters.get("rating").get(0));
			}
		};
	}

	@Injectable
	private Integer rating = 3;
	@Test
	public void testSubmitCase1() {
		satisfaction.submit();
		new Verifications() {{
				ticket.update(); times = 1;
				token.delete(); times = 1;
			}
		};
	}

	@Test
	public void testSubmitCase2() {
		this.rating = 10;
		satisfaction.submit();
		new Verifications() {
			boolean renderForm;
			{
				JsfUtil.addSuccessMessage("Please add a feedback rating between 1 and 5.");
				renderForm = true;
			}
		};
	}
	
}
