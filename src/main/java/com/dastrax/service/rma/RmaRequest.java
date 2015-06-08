/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.rma;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.dastrax.app.misc.JsfUtil;
import com.dastrax.app.security.SessionUser;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.Comment;
import com.dastrax.per.entity.Ticket;
import com.dastrax.per.entity.User;
import com.dastrax.per.project.DTX;
import com.dastrax.per.project.DTX.TicketStatus;
import com.dastrax.service.navigation.Navigator;

/**
 * An Return Material Authorization (RMA) request. Currently RMA requests are
 * not stored in the persistence layer, nor are they traceable. They are simply
 * requests that are sent as an email. SOLiD currently use NetSuite to track the
 * RMA so the API needs to be integrated.
 *
 * @version 2.0.0
 * @since Build 2.1.0 (May 26, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@ViewScoped
public class RmaRequest implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(RmaRequest.class.getName());
    private static final long serialVersionUID = 1L;

    private static final String recipient = "support@solid.com";
    private RMA rma;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public RmaRequest() {
        rma = new RMA();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CDI">
    @Inject
    private Navigator navigator;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of rma
     *
     * @return the value of rma
     */
    public RMA getRma() {
        return rma;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of rma.
     *
     * @param rma new value of rma
     */
    public void setRma(RMA rma) {
        this.rma = rma;
    }
//</editor-fold>

    /**
     * Currently RMA requests are not stored in the persistence layer, 
     * nor are they traceable. They are simply requests that are sent as an 
     * email. This method converts the form into an email and sends it.
     */
    public void request() {

        String satDelivery;
        if (rma.isSaturdayDelivery()) {
            satDelivery = "YES";
        } else {
            satDelivery = "NO";
        }

        String deliveryDate = new SimpleDateFormat(
                DTX.TemporalFormat.DATE_FORMAT.getValue())
                .format(rma.getDeliveryDate());
        
        // Convert List to string
        String html = "<h4>RMA Details:</h4>"
                + "<br /> Company Name (on invoice): "
                + rma.getCompanyName()
                + "<br /> Project Name: "
                + rma.getProjectName()
                + "<br /> Quote/Sales Order/Fulfillment: "
                + rma.getOrderNumber()
                + "<br /><br /> <h4>RMA Contact (responsible for return of product):</h4>"
                + "<br /> Name: "
                + rma.getResponsibleName()
                + "<br /> Company: "
                + rma.getResponsibleCompany()
                + "<br /> Phone: "
                + rma.getResponsiblePhone()
                + "<br /> Email: "
                + rma.getResponsibleEmail()
                + "<br /><br /> <h4>Replacement Product Ship To Info:</h4>"
                + "<br /> Name: "
                + rma.getShippingName()
                + "<br /> Company: "
                + rma.getShippingCompany()
                + "<br /> Phone: "
                + rma.getShippingPhone()
                + "<br /> Email: "
                + rma.getShippingEmail()
                + "<br /> Address1: "
                + rma.getShippingAddress1()
                + "<br /> Address2: "
                + rma.getShippingAddress2()
                + "<br /> City: "
                + rma.getShippingCity()
                + "<br /> State: "
                + rma.getShippingState()
                + "<br /> Zip: "
                + rma.getShippingZip()
                + "<br /> Ship To address accepts Saturday deliveries?: "
                + satDelivery
                + "<br /> Date replacement product must be delivered?: "
                + deliveryDate
                + "<br /><br /> <h4>Product Info:</h4>";

        for (RMA.RmaProduct product : rma.getProducts()) {
            String temp = " <br /> Quantity: "
                    + product.getQty()
                    + "<br /> Serial Number: "
                    + product.getSerialNumber()
                    + "<br /> Part Number: "
                    + product.getPartNumber()
                    + "<br /> Part Name: "
                    + product.getPartName()
                    + "<br /> Reason for return: "
                    + product.getReturnReason()
                    + "<br /> Failure stage: "
                    + product.getFailureStage()
                    + "<br /> Issue Details: "
                    + product.getIssueDetails()
                    + "<br /> Troubleshooting Steps: "
                    + product.getTroubleshooting()
                    + "<br />";
            html = html + temp;
        }
        
        Ticket ticket = new Ticket();

        User user = SessionUser.getCurrentUser();
        Comment comment = new Comment();
        comment.setComment(html);
        comment.setCommenter(user);
        long epocd = System.currentTimeMillis();
        comment.setCreateEpoch(epocd);
        List<Comment> comments = new ArrayList<Comment>();
        comments.add(comment);
        ticket.setComments(comments);
        ticket.setSatisfied(0);
        ticket.setRequester(user);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setEmail(rma.getResponsibleEmail());
        ticket.setTitle("created by RMA");
        ticket.setOpenEpoch(epocd);
        dap.create(ticket);
        
        /*
        // Build an new email
        Email email = new Email();
        email.setRecipientEmail(recipient);

        // Set the variables
        Map<EmailVariableKey, String> vars = new HashMap<>();
        vars.put(EmailVariableKey.RMA, html);
        email.setVariables(vars);

        // Retreive the email template from the database.
        email.setTemplate(
                (Template) dap.find(
                        Template.class,
                        DTX.EmailTemplate.RMA_REQUEST
                        .getValue()));

        // Send the email
        new DefaultEmailer().send(email);
		*/
        JsfUtil.addSuccessMessage("Your RMA request has been received and "
                + "submitted for processing.  A support representative will"
                + " contact you during normal business hours:  Monday-Friday,"
                + " 5:00 AM PST to 5:00 PM PST.  Estimated response time is "
                + "within 2 hours.  If the matter requires immediate "
                + "attention, you may call support directly at 888-409-9997 "
                + "option 2 or email at: support@solid.com");

        // Carry the message over to the page redirect
        FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getFlash()
                .setKeepMessages(true);

        navigator.navigate("RMA_REQUEST");
    }

}
