package com.dastrax.service.ticket;

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

import com.dastrax.app.misc.JsfUtil;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.Ticket;
import com.dastrax.per.entity.Token;

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
