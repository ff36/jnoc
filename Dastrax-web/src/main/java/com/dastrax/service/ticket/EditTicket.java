/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.ticket;

import com.dastrax.app.util.NexusUtil;
import com.dastrax.app.util.TemporalUtil;
import com.dastrax.mesh.email.Email;
import com.dastrax.mesh.email.EmailUtil;
import com.dastrax.per.dao.core.EmailParamDAO;
import com.dastrax.per.dao.core.EmailTemplateDAO;
import com.dastrax.per.dao.core.NexusDAO;
import com.dastrax.per.dao.core.SiteDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.dao.core.TagDAO;
import com.dastrax.per.dao.csm.TicketDAO;
import com.dastrax.per.entity.core.Company;
import com.dastrax.per.entity.core.EmailParam;
import com.dastrax.per.entity.core.EmailTemplate;
import com.dastrax.per.entity.core.Nexus;
import com.dastrax.per.entity.core.Site;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.entity.core.Tag;
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
import java.util.UUID;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @version Build 2.0.0
 * @since Aug 7, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class EditTicket implements Serializable {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(EditTicket.class.getName());

    // Variables----------------------------------------------------------------
    private Ticket ticket = new Ticket();
    private Helper helper = new Helper();
    private String ticketId;

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
    NexusDAO nexusDAO;
    @EJB
    TemporalUtil temporalUtil;
    @EJB
    NexusUtil nexusUtil;
    @EJB
    EmailParamDAO emailParamDAO;
    @EJB
    TagDAO tagDAO;

    // Constructors-------------------------------------------------------------
    public EditTicket() {
    }

    // Getters------------------------------------------------------------------
    public Ticket getTicket() {
        return ticket;
    }

    public Helper getHelper() {
        return helper;
    }

    public String getTicketId() {
        return ticketId;
    }

    public TemporalUtil getTemporalUtil() {
        return temporalUtil;
    }

    public NexusUtil getNexusUtil() {
        return nexusUtil;
    }

    // Setters------------------------------------------------------------------
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public void setTemporalUtil(TemporalUtil temporalUtil) {
        this.temporalUtil = temporalUtil;
    }

    // Methods------------------------------------------------------------------
    /**
     * Initialize the page by loading the specified ticket from the database and
     * populating all the helper attributes.
     *
     * @throws IOException
     */
    public void init() throws IOException {
        // Set the ticket
        ticket = ticketDAO.findTicketById(ticketId);

        // Is the ticket accessible by the current subject
        boolean canLoadTicket = false;
        // ADMIN access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
            canLoadTicket = true;
        }
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            List<String> companies = new ArrayList<>();
            // Add the VAR company ID
            companies.add(s.getCompany().getId());
            // Add the Client companies
            for (Company client : s.getCompany().getClients()) {
                companies.add(client.getId());
            }
            if (ticket.getRequester().getCompany().getId() != null
                    && companies.contains(ticket.getRequester().getCompany().getId())) {
                canLoadTicket = true;
            }
        }
        // CLIENT access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            if (ticket.getRequester().getCompany().getId() != null
                    && s.getCompany().getId().equals(ticket.getRequester().getCompany().getId())) {
                canLoadTicket = true;
            }
        }

        // If the current subjet is authorised to access the current ticket
        if (canLoadTicket) {
            // Sort the ticket comments
            ticket.sortComments();

            // Set the current subject
            helper.currentSubject = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString()
                    );

            // Set the helpers
            // ADMIN access
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
                helper.sites = siteDAO.findAllSites();
                helper.assigneeSubjects = subjectDAO.findAllSubjectsByMetier(DastraxCst.Metier.ADMIN.toString());
            }
            // VAR access
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
                helper.sites = helper.getCurrentSubject().getCompany().getVarSites();
                helper.assigneeSubjects = subjectDAO.findAllSubjectsByCompany(helper.getCurrentSubject().getCompany().getId());
            }
            // CLIENT access
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
                helper.sites = helper.getCurrentSubject().getCompany().getClientSites();
            }
            // Set Tags
            helper.setAvailableTags(tagDAO.findAllTags());

        } else {
            // Redirect if the user is not allowed access to the ticket
            ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
            String url = null;
            // ADMIN access
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
                url = ectx.getRequestContextPath() + "/a/tickets/manual/list.jsf";
            }
            // VAR access
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
                url = ectx.getRequestContextPath() + "/b/tickets/manual/list.jsf";
            }
            // CLIENT access
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
                url = ectx.getRequestContextPath() + "/c/tickets/manual/list.jsf";
            }
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        }
    }

    /**
     * Submits the edited ticket to be processed and stored back into the db.
     *
     * @throws IOException
     */
    public void submit() throws IOException {
        // Set the ticket variables
        helper.completeComment();
        ticket.getComments().add(helper.comment);

        // Persist the ticket
        ticket = ticketDAO.update(ticket);

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
        JsfUtil.addSuccessMessage("Ticket DTX-" + ticket.getId() + " updated");
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
            url = ectx.getRequestContextPath() + "/a/tickets/manual/list.jsf";
        }
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            url = ectx.getRequestContextPath() + "/b/tickets/manual/list.jsf";
        }
        // CLIENT access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
            url = ectx.getRequestContextPath() + "/c/tickets/manual/list.jsf";
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);

    }

    /**
     * Modify the tickets status to OPEN and submit it for processing
     *
     * @throws IOException
     */
    public void submitOpen() throws IOException {
        ticket.setStatus(DastraxCst.TicketStatus.OPEN.toString());
        submit();
    }

    /**
     * Modify the tickets status to SOLVED and submit it for processing
     *
     * @throws IOException
     */
    public void submitSolved() throws IOException {
        ticket.setStatus(DastraxCst.TicketStatus.SOLVED.toString());
        Subject s = subjectDAO.findSubjectByUid(
                SecurityUtils.getSubject().getPrincipals()
                .asList().get(1).toString());
        ticket.setCloser(s);
        submit();
    }

    /**
     * Modify the tickets status to ARCHIVED and submit it for processing
     *
     * @throws IOException
     */
    public void submitArchived() throws IOException {
        ticket.setStatus(DastraxCst.TicketStatus.ARCHIVED.toString());
        submit();
    }

    /**
     * This is an empty listener to modify the assignee
     */
    public void changeAssignee() {
    }

    /**
     * TODO: Currently the site JSF converter is not working so we are having to
     * use a simple String and set the site afterwards using this method by
     * retrieving the set site from the DB using the set ID. This needs to be
     * fixed so it works like the assignee and the JSF converter means this
     * extra call to the DB can be removed.
     */
    public void changeSite() {
        // TODO: Fix JSF Converter issue.
        ticket.setSite(siteDAO.findSiteById(helper.selectedSite));
    }

    /**
     * Sends asynchronous email to the specified recipient
     *
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
        EmailTemplate et;
        if (ticket.getStatus().equals(DastraxCst.TicketStatus.SOLVED.toString())) {
            et = emailTemplateDAO.findTemplateById(
                    DastraxCst.EmailTemplate.TICKET_SOLVED.toString());
            // Set the email Params
            email.getParam().setToken(UUID.randomUUID().toString());
            email.getParam().setCreationEpoch(Calendar.getInstance().getTimeInMillis());
            email.getParam().setParamA(ticket.getId());

            // Persist the params
            emailParamDAO.create(email.getParam());
        } else {
            et = emailTemplateDAO.findTemplateById(
                    DastraxCst.EmailTemplate.TICKET_MODIFIED.toString());
        }
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
        private List<Subject> assigneeSubjects = new ArrayList<>();
        private String selectedAssignee;
        private String selectedSite;
        private Comment comment = new Comment();
        private String commentACL = "public";
        private boolean requesterEmail = true;
        private boolean assigneeEmail = true;
        private String ccEmail;
        private Subject currentSubject;
        private List<Tag> availableTags = new ArrayList<>();

        // Constructors-------------------------------------------------------------
        public Helper() {
        }

        // Getters------------------------------------------------------------------
        public List<Site> getSites() {
            return sites;
        }

        public List<Subject> getAssigneeSubjects() {
            return assigneeSubjects;
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

        public boolean isRequesterEmail() {
            return requesterEmail;
        }

        public boolean isAssigneeEmail() {
            return assigneeEmail;
        }

        public String getCcEmail() {
            return ccEmail;
        }

        public String getCommentACL() {
            return commentACL;
        }

        public Subject getCurrentSubject() {
            return currentSubject;
        }

        public List<Tag> getAvailableTags() {
            return availableTags;
        }

        // Setters------------------------------------------------------------------
        public void setSites(List<Site> sites) {
            this.sites = sites;
        }

        public void setAssigneeSubjects(List<Subject> assigneeSubjects) {
            this.assigneeSubjects = assigneeSubjects;
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

        public void setRequesterEmail(boolean requesterEmail) {
            this.requesterEmail = requesterEmail;
        }

        public void setAssigneeEmail(boolean assigneeEmail) {
            this.assigneeEmail = assigneeEmail;
        }

        public void setCcEmail(String ccEmail) {
            this.ccEmail = ccEmail;
        }

        public void setCommentACL(String commentACL) {
            this.commentACL = commentACL;
        }

        public void setAvailableTags(List<Tag> availableTags) {
            this.availableTags = availableTags;
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
        public List<Subject> completeAssignee(String query) {
            List<Subject> suggestions = new ArrayList<>();

            for (Subject s : assigneeSubjects) {
                String name = s.getContact().buildFullName();
                if (name.toLowerCase().startsWith(query.toLowerCase())) {
                    suggestions.add(s);
                }
            }

            return suggestions;
        }

        /**
         * If the ticket has a comment added we need to complete the comments
         * core attributes. This method provides boiler plate functions to
         * finish off the comment by adding required values to its attributes.
         */
        private void completeComment() {
            comment.setCommenter(subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString()));
            comment.setCreated(Calendar.getInstance().getTimeInMillis());

            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
                if (commentACL.equals("internal")) {
                    Nexus nexus = nexusDAO.findNexusById(DastraxCst.ROOT_NEXUS_ADMIN);
                    comment.setAcl(nexus);
                }
            }
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
                if (commentACL.equals("internal")) {
                    Nexus nexus = nexusDAO.findNexusById(DastraxCst.ROOT_NEXUS_ADMIN_VAR);
                    comment.setAcl(nexus);
                }
            }
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
         * @return The filtered list of tags that match the current value of the
         * field.
         */
        public List<Tag> completeTags(String query) {
            List<Tag> suggestions = new ArrayList<>();
            // Add the query suggestion
            Tag tag = new Tag();
            tag.setId("tempID(" + query + ")");
            tag.setName(query);
            suggestions.add(tag);

            // Add suggestions from the existing list
            for (Tag t : availableTags) {
                String name = t.getName();
                if (name.toLowerCase().startsWith(query.toLowerCase())) {
                    suggestions.add(t);
                }
            }

            return suggestions;
        }
        
        public void aclListener() {
            // empty listener to fire when acl is changed
        }

    }
}
