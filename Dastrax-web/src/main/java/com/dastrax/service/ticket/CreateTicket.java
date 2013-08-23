/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.ticket;

import com.dastrax.app.util.FilterUtil;
import com.dastrax.mesh.email.Email;
import com.dastrax.mesh.email.EmailUtil;
import com.dastrax.per.dao.core.EmailTemplateDAO;
import com.dastrax.per.dao.core.SiteDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.dao.csm.TicketDAO;
import com.dastrax.per.entity.core.EmailParam;
import com.dastrax.per.entity.core.EmailTemplate;
import com.dastrax.per.entity.core.Site;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.entity.csm.Comment;
import com.dastrax.per.entity.csm.Ticket;
import com.dastrax.per.project.DastraxCst;
import com.dastrax.service.util.JsfUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @version Build 2.0.0
 * @since Aug 4, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class CreateTicket implements Serializable {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(CreateTicket.class.getName());

    // Variables----------------------------------------------------------------
    private Ticket ticket = new Ticket();
    private Helper helper = new Helper();

    // EJB----------------------------------------------------------------------
    @EJB
    TicketDAO ticketDAO;
    @EJB
    SubjectDAO subjectDAO;
    @EJB
    SiteDAO siteDAO;
    @EJB
    EmailUtil emailUtil;
    @EJB
    EmailTemplateDAO emailTemplateDAO;
    @EJB
    FilterUtil filterUtil;

    // Constructors-------------------------------------------------------------
    public CreateTicket() {
    }

    @PostConstruct
    private void init() {
        // ADMIN access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
            helper.sites = siteDAO.findAllSites();
            helper.requesterSubjects = subjectDAO.findAllSubjects();
            helper.assigneeSubjects = subjectDAO.findAllSubjectsByMetier(DastraxCst.Metier.ADMIN.toString());
        }
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            helper.sites = s.getCompany().getVarSites();
            helper.requesterSubjects = subjectDAO.findAllChildSubjectsByCompany(filterUtil.authorizedCompaniesList());
            helper.assigneeSubjects = subjectDAO.findAllSubjectsByCompany(s.getCompany().getId());
        }
        // CLIENT access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            helper.sites = s.getCompany().getClientSites();
        }
    }

    // Getters------------------------------------------------------------------
    public Ticket getTicket() {
        return ticket;
    }

    public Helper getHelper() {
        return helper;
    }

    // Setters------------------------------------------------------------------
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }

    // Methods------------------------------------------------------------------
    /**
     * Some roles have the ability to add subjects of the fly. In those 
     * circumstances they can call this method to reload the list of subjects.
     */
    public void reloadSubjects() {
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
            helper.requesterSubjects = subjectDAO.findAllSubjects();
            helper.assigneeSubjects = subjectDAO.findAllSubjectsByMetier(DastraxCst.Metier.ADMIN.toString());
        }
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            helper.requesterSubjects = subjectDAO.findAllChildSubjectsByCompany(filterUtil.authorizedCompaniesList());
            helper.assigneeSubjects = subjectDAO.findAllSubjectsByCompany(s.getCompany().getId());
        }
    }

    /**
     * Create a new ticket
     * @throws IOException 
     */
    private void create() throws IOException {
        // Set the ticket variables
        Subject s = subjectDAO.findSubjectByUid(
                SecurityUtils.getSubject().getPrincipals()
                .asList().get(1).toString());
        ticket.setCreator(s);
        // ADMIN access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
            ticket.setRequester(subjectDAO.findSubjectByUid(helper.selectedRequester));
        }
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            ticket.setRequester(subjectDAO.findSubjectByUid(helper.selectedRequester));
        }
        // CLIENT access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
            ticket.setRequester(s);
        }
        ticket.setAssignee(subjectDAO.findSubjectByUid(helper.selectedAssignee));
        ticket.setSite(siteDAO.findSiteById(helper.selectedSite));

        helper.comment.setCreated(Calendar.getInstance().getTimeInMillis());
        helper.comment.setCommenter(ticket.getCreator());
        ticket.getComments().add(helper.comment);

        // Persist the ticket
        ticket = ticketDAO.create(ticket);

        // Build a list of all the emails
        List<String> emails;
        if (!"".equals(helper.ccEmail)) {
            String ccs = helper.ccEmail.replaceAll("\\s", "");
            emails = new ArrayList<>(Arrays.asList(ccs.split(",")));
        } else {
            emails = new ArrayList<>();
        }
        if (helper.requesterEmail) {
            emails.add(ticket.getRequester().getEmail());
        }
        if (helper.assigneeEmail && ticket.getAssignee() != null) {
            emails.add(ticket.getAssignee().getEmail());
        }

        // Send the emails
        for (String email : emails) {
            sendEmail(ticket, email);
        }

        // Add success message
        JsfUtil.addSuccessMessage("Ticket DTX-" + ticket.getId() + " created");
        // Carry the message over to the page redirect
        FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getFlash()
                .setKeepMessages(true);

        ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
        String url = null;
        // ADMIN access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
            if (helper.createAnotherTicket) {
                url = ectx.getRequestContextPath() + "/a/tickets/manual/create.jsf";
                FacesContext.getCurrentInstance().getExternalContext().redirect(url);
            } else {
                url = ectx.getRequestContextPath() + "/a/tickets/manual/list.jsf";
            }
        }
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            if (helper.createAnotherTicket) {
                url = ectx.getRequestContextPath() + "/b/tickets/manual/create.jsf";
                FacesContext.getCurrentInstance().getExternalContext().redirect(url);
            } else {
                url = ectx.getRequestContextPath() + "/b/tickets/manual/list.jsf";
            }
        }
        // CLIENT access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
            if (helper.createAnotherTicket) {
                url = ectx.getRequestContextPath() + "/c/tickets/manual/create.jsf";
                FacesContext.getCurrentInstance().getExternalContext().redirect(url);
            } else {
                url = ectx.getRequestContextPath() + "/c/tickets/manual/list.jsf";
            }
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }

    /**
     * Set the ticket to OPEN and create it
     * @throws IOException 
     */
    public void createOpen() throws IOException {
        ticket.setStatus(DastraxCst.TicketStatus.OPEN.toString());
        create();
    }

    /**
     * Set the ticket to SOLVED and create it
     * @throws IOException 
     */
    public void createSolved() throws IOException {
        ticket.setStatus(DastraxCst.TicketStatus.SOLVED.toString());
        create();
    }

    /**
     * Set the ticket to ARCHIVED and create it
     * @throws IOException 
     */
    public void createArchived() throws IOException {
        ticket.setStatus(DastraxCst.TicketStatus.ARCHIVED.toString());
        create();
    }

    /**
     * Sends asynchronous email to the specified recipient
     * @param ticket
     * @param recipient
     * @return the email parameter object to be stored in the database
     */
    private EmailParam sendEmail(Ticket ticket, String recipient) {
        // Build an new email
        Email email = new Email();

        // Set the recipient
        email.setRecipientEmail(recipient.toLowerCase());

        // Set the variables
        Map<String, String> vars = new HashMap<>();
        vars.put("ticket_id", "DTX-" + ticket.getId());
        vars.put("ticket_title", ticket.getTitle());
        vars.put("ticket_status", ticket.getStatus());
        email.setVariables(vars);

        // Retreive the email template from the database.
        EmailTemplate et = emailTemplateDAO.findTemplateById(
                DastraxCst.EmailTemplate.NEW_TICKET.toString());
        email.setTemplate(et);

        // Send the email
        emailUtil.build(email);

        return email.getParam();
    }

    /**
     * This is a simple class designed to tidy up the outer-class by
     * encapsulating some variables that are needed to complete some of the out
     * methods.
     */
    public class Helper {

        // Variables----------------------------------------------------------------
        private List<Site> sites = new ArrayList<>();
        private List<Subject> requesterSubjects = new ArrayList<>();
        private List<Subject> assigneeSubjects = new ArrayList<>();
        private String selectedRequester;
        private String selectedAssignee;
        private String selectedSite;
        private Comment comment = new Comment();
        private boolean createAnotherTicket;
        private boolean requesterEmail = true;
        private boolean assigneeEmail = true;
        private String ccEmail;

        // Constructors-------------------------------------------------------------
        public Helper() {
        }

        // Getters------------------------------------------------------------------
        public List<Site> getSites() {
            return sites;
        }

        public List<Subject> getRequesterSubjects() {
            return requesterSubjects;
        }

        public List<Subject> getAssigneeSubjects() {
            return assigneeSubjects;
        }

        public String getSelectedRequester() {
            return selectedRequester;
        }

        public String getSelectedAssignee() {
            return selectedAssignee;
        }

        public String getSelectedSite() {
            return selectedSite;
        }

        public Comment getComment() {
            return comment;
        }

        public boolean isCreateAnotherTicket() {
            return createAnotherTicket;
        }

        public boolean isRequesterEmail() {
            return requesterEmail;
        }

        public boolean isAssigneeEmail() {
            return assigneeEmail;
        }

        public String getCcEmail() {
            return ccEmail;
        }

        // Setters------------------------------------------------------------------
        public void setSites(List<Site> sites) {
            this.sites = sites;
        }

        public void setRequesterSubjects(List<Subject> requesterSubjects) {
            this.requesterSubjects = requesterSubjects;
        }

        public void setAssigneeSubjects(List<Subject> assigneeSubjects) {
            this.assigneeSubjects = assigneeSubjects;
        }

        public void setSelectedRequester(String selectedRequester) {
            this.selectedRequester = selectedRequester;
        }

        public void setSelectedAssignee(String selectedAssignee) {
            this.selectedAssignee = selectedAssignee;
        }

        public void setSelectedSite(String selectedSite) {
            this.selectedSite = selectedSite;
        }

        public void setComment(Comment comment) {
            this.comment = comment;
        }

        public void setCreateAnotherTicket(boolean createAnotherTicket) {
            this.createAnotherTicket = createAnotherTicket;
        }

        public void setRequesterEmail(boolean requesterEmail) {
            this.requesterEmail = requesterEmail;
        }

        public void setAssigneeEmail(boolean assigneeEmail) {
            this.assigneeEmail = assigneeEmail;
        }

        public void setCcEmail(String ccEmail) {
            this.ccEmail = ccEmail;
        }

        // Methods------------------------------------------------------------------
        /**
         * The auto complete function allows the input field to dynamically
         * filter down options based on currently entered values. This method is
         * called whenever a 'keyup' event is detected in the field and passes
         * in the current value of the field to filter down the possible correct
         * matches from the list of subjects. Values are converted to lower case
         * to make sure that case sensitivity is not required for a match.
         *
         * @param query
         * @return The filtered list of subjects that match the current value of
         * the field.
         */
        public List<Subject> completeRequesterSubject(String query) {
            List<Subject> suggestions = new ArrayList<>();

            for (Subject s : requesterSubjects) {
                String name = s.getContact().buildFullName();
                if (name.toLowerCase().startsWith(query.toLowerCase())) {
                    suggestions.add(s);
                }
            }

            return suggestions;
        }

        /**
         * The auto complete function allows the input field to dynamically
         * filter down options based on currently entered values. This method is
         * called whenever a 'keyup' event is detected in the field and passes
         * in the current value of the field to filter down the possible correct
         * matches from the list of subjects. Values are converted to lower case
         * to make sure that case sensitivity is not required for a match.
         *
         * @param query
         * @return The filtered list of subjects that match the current value of
         * the field.
         */
        public List<Subject> completeAssigneeSubject(String query) {
            List<Subject> suggestions = new ArrayList<>();

            for (Subject s : assigneeSubjects) {
                String name = s.getContact().buildFullName();
                if (name.toLowerCase().startsWith(query.toLowerCase())) {
                    suggestions.add(s);
                }
            }

            return suggestions;
        }

    }

}
