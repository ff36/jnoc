package co.ff36.jnoc.app.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import co.ff36.jnoc.app.email.DefaultEmailer;
import co.ff36.jnoc.app.email.Email;
import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.dap.QueryParameter;
import co.ff36.jnoc.per.entity.Template;
import co.ff36.jnoc.per.entity.Ticket;
import co.ff36.jnoc.per.project.JNOC;
import co.ff36.jnoc.service.ticket.TicketAnalytics.TicketDigest;

public class TicketReportOfWeeklyJob implements Job{
	private static final Logger LOG = Logger.getLogger(TicketReportOfWeeklyJob.class.getName());
    private CrudService dap;

    public TicketReportOfWeeklyJob() {
        try {
            dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		try {
			//判断是不是要进行作业、
			
			//得到数据并且构建数据结构
			Calendar from = Calendar.getInstance();
			Calendar to = (Calendar) from.clone();
			to.add(Calendar.DAY_OF_YEAR, -10);
			
			long expectTime = to.getTimeInMillis();
			List<Ticket> tickets = dap.findWithNamedQuery(
	                "Ticket.findAllByStatusAndOpen",
	                QueryParameter
	                .with("status", JNOC.TicketStatus.OPEN)
	                .and("openEpoch", expectTime)
	                .parameters());
			
			if(tickets==null || tickets.size()==0)
				return;
			
			List<TicketReportOfWeeklyBean> ticketbeans = TicketReportOfWeeklyBean.buildTickets(tickets);
			
			//生成report
			File tmpfile = new File("ticketreport.pdf");
			
			try {
				
				if(tmpfile.exists())
					tmpfile.delete();
				tmpfile.createNewFile();
				
				JasperReport jasperReport = JasperCompileManager.compileReport(TicketDigest.class.getResourceAsStream("/report/TicketReportByWeekly.jrxml"));

				Map customParameters = new HashMap();
				
				//JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(getSeverities(), false);
				customParameters.put("tickets", 
						new JRBeanCollectionDataSource(ticketbeans, false));
				
//				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, customParameters, 
//						new JRBeanCollectionDataSource(tickets));
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, customParameters, 
						new JREmptyDataSource());
				
				//test save to desktop
				FileOutputStream os = new FileOutputStream("C:/Users/Administrator/Desktop/ticketWeekReport.pdf");
//				FileOutputStream os = new FileOutputStream(tmpfile);
				JasperExportManager.exportReportToPdfStream(jasperPrint, os);
				os.close();
				
				
			} catch (JRException | IOException e) {
				LOG.log(Level.SEVERE, e.getMessage(), e);
			}
			
			//TODO email template
			Template template = (Template) dap.find(
	                Template.class, JNOC.EmailTemplate.TICKET_REPORT_BY_WEEKLY.getValue());
			
			//发送email.
			Email email = new Email();
			
			String emailAddress = "";
			
			// Set the recipient
			email.setRecipientEmail(emailAddress.toLowerCase());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			
	        // Set the variables
	        Map<JNOC.EmailVariableKey, String> vars = new HashMap<>();
	        vars.put(JNOC.EmailVariableKey.FROM_DATE, sdf.format(from.getTime()));
	        vars.put(JNOC.EmailVariableKey.TO_DATE, sdf.format(to.getTime()));
	        email.setVariables(vars);

	        // Set the template
	        email.setTemplate(template);
	        email.setAttachment(tmpfile);
			
			DefaultEmailer emailer = new DefaultEmailer();
			emailer.send(email);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
