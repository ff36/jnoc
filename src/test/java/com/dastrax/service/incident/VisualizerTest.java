package com.dastrax.service.incident;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineModel;

public class VisualizerTest {
	
	@Test
	public void testInit() {
		Visualizer visualizer = new Visualizer();
		
		visualizer.init();
		
		TimelineModel model = visualizer.getModel();
		List<TimelineEvent> ss = model.getEvents();

		Assert.assertEquals(30, ss.size());
		
	}

}
