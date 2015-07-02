'use strict'
/**
 * 
 */
	//pulgin html2canvas: chrome  Support svg2canvas
	//but firefox can't support svg2canvas, so we can svg2canvas
	function svgToCanvas (targetElem) {
		var nodesToRecover = [];
		var nodesToRemove = [];

		var svgElem = targetElem.find('svg');

		svgElem.each(function(index, node) {
			var parentNode = node.parentNode;
			//var svg = "<svg width='320' height='220'>"+node.innerHTML+"</svg>";
			var svg = node.outerHTML;
			//alert(svg);
			
			var canvas = document.createElement('canvas');
			
			canvg(canvas, svg);
			
			nodesToRecover.push({
				parent: parentNode,
				child: node
			});
			parentNode.removeChild(node);
			
			nodesToRemove.push({
				parent: parentNode,
				child: canvas
			});
			
			parentNode.appendChild(canvas);
		});
		
		return true;
	}

	/*
		get chart html elements to new iframe.
	*/
	function openWithIframe(html){
		
		var iframe = document.createElement('iframe');
		iframe.setAttribute("id", "myFrmame");
		
		var $iframe = $(iframe);
		$iframe.css({
          'visibility': 'hidden', 'position':'static', 'z-index':'4'
        }).width($(window).width()).height($(window).height());

		$('body').append(iframe);
		
		
		var ifDoc = iframe.contentWindow.document;
		
		var style = "<link href='/javax.faces.resource/css/auth.css.jsf' rel='stylesheet' type='text/css'>";
		style+="<link href='/javax.faces.resource/css/common.css.jsf' rel='stylesheet' type='text/css'>";
		style+="<link href='/javax.faces.resource/css/dc.css.jsf' rel='stylesheet' type='text/css'>";
		
		html = "<!DOCTYPE html><html><head>"+style+"</head><body>"+html+"</body></html>"
		
		ifDoc.open();		
		ifDoc.write(html);		
		ifDoc.close();
		
		var fbody = $iframe.contents().find("body");
		
		fbody.find("#chart-center").removeAttr("width");
		
		fbody.find(".page-container").css("width", "370px");
		fbody.find(".center-container").css("width", "600px");
		
		fbody.find("#severity-chart svg").attr("width", "370");
		fbody.find("#status-chart svg").attr("width", "300");
		
		return fbody;
		
	}
	
	function exportAsPDF(){
		
		var chartCenter = document.getElementById("chart-center").outerHTML;
		
		var fbody = openWithIframe(chartCenter);
		var result = svgToCanvas(fbody);
		
		html2canvas(fbody, { 
			onrendered: function(canvas) { 
				//var myImage = canvas.toDataURL("image/png");
				//alert(myImage);
				//window.open(myImage);
				
				/*
				canvas.toBlob(function(blob) {
					saveAs(blob, "report.png");
				}, "image/png");
				*/
				var imgData = canvas.toDataURL('image/jpeg');
				//alert(imgData);
				var doc = new jsPDF('l', 'pt', 'a3');
				//var doc = new jsPDF('l', 'mm', [360, 210]);
				doc.setFontSize(22);
				doc.setFontType("bolditalic");
				doc.text(500, 30, "Ticket Report");
				
				
				doc.addImage(imgData, 'jpeg', 10, 60);
				
				doc.addPage();
				
				var res = doc.autoTableHtmlToJson(document.getElementById("tickets-summary-table"), true);
				doc.autoTable(res.columns, res.data);
				
				doc.save('ticket.report_"+new Date().getTime()+".pdf');
				$('#myFrmame').remove();
			}, 
			background:"#fff",
			allowTaint: true
		});
		
		
		
		//var ticketDoc = new jsPDF('l', 'pt', 'a3');
		/*
		// We'll make our own renderer to skip this editor
		var specialElementHandlers = {
			'#editor': function(element, renderer){
				return true;
			}
		};

		// All units are in the set measurement for the document
		// This can be changed to "pt" (points), "mm" (Default), "cm", "in"
		ticketDoc.fromHTML($('#tickets-summary-table').get(0), 15, 15, {
			'width': 170, 
			'elementHandlers': specialElementHandlers
		});
		*/
		
		//ticketDoc.text("Tickets", 100, 50);
		//var res = ticketDoc.autoTableHtmlToJson(document.getElementById("tickets-summary-table"), true);
		//ticketDoc.autoTable(res.columns, res.data);
		//ticketDoc.save('tickets.pdf');
		
    };
	
