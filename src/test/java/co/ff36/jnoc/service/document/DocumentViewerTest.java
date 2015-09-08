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


package co.ff36.jnoc.service.document;

import static org.junit.Assert.assertEquals;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.entity.Attachment;
import co.ff36.jnoc.service.document.DocumentViewer;

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
