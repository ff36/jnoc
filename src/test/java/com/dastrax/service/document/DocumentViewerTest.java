package com.dastrax.service.document;

import static org.junit.Assert.assertEquals;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.Attachment;

@RunWith(JMockit.class)
public class DocumentViewerTest {

	@Mocked
	private DocumentViewer dv;
	@Mocked
	private String url;
	@Mocked
	private boolean render;
	@Mocked
	private boolean error;
	@Mocked
	private CrudService dap;
	
	@Test
	public void testInit() {
		
		new Expectations() {{
			render = true;
		}};

		dv.init();
		assertEquals(true, render);
		assertEquals(false, error);
		
	}

	@Test
	public void testInitFalse() {
		
		new NonStrictExpectations() {{
			dap.find(Attachment.class, 122);
			result =  new NullPointerException("xxx");
			error = true;
		}};

		dv.init();
		assertEquals(false, render);
		assertEquals(true, error);
		
	}
	
}
