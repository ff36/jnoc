/*
 * Created Jul 10, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.per.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringEscapeUtils;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;

import com.dastrax.app.email.DefaultEmailer;
import com.dastrax.app.email.Email;
import com.dastrax.app.misc.TemporalUtil;
import com.dastrax.app.security.SessionUser;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.dap.QueryParameter;
import com.dastrax.per.project.DTX;
import com.dastrax.per.project.DTX.TicketSeverity;
import com.dastrax.per.project.DTX.TicketStatus;
import com.dastrax.per.project.DTX.TicketTopic;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 *
 * Ticket holds information pertaining to support tickets.
 *
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@NamedQueries({
    @NamedQuery(name = "Ticket.findAll", query = "SELECT e FROM Ticket e"),
    @NamedQuery(name = "Ticket.findByID", query = "SELECT e FROM Ticket e WHERE e.id = :id"),
    @NamedQuery(name = "Ticket.findAllByStatus", query = "SELECT e FROM Ticket e WHERE e.status = :status"),
    @NamedQuery(name = "Ticket.findAllExceptStatus", query = "SELECT e FROM Ticket e WHERE e.status <> :status"),
    @NamedQuery(name = "Ticket.findAllExceptMultiStatus", query = "SELECT e FROM Ticket e WHERE e.status <> :status1 AND e.status <> :status2"),
    @NamedQuery(name = "Ticket.findAllByType", query = "SELECT e FROM Ticket e WHERE e.topic = :topic"),
    @NamedQuery(name = "Ticket.findAllByEmail", query = "SELECT e FROM Ticket e WHERE e.email = :email"),
    @NamedQuery(name = "Ticket.findAllByTitle", query = "SELECT e FROM Ticket e WHERE e.mailTitle = :mailTitle"),
    @NamedQuery(name = "Ticket.findAllByCreator", query = "SELECT e FROM Ticket e JOIN e.creator a WHERE a.id = :id"),
    @NamedQuery(name = "Ticket.findAllByRequester", query = "SELECT e FROM Ticket e JOIN e.requester a WHERE a.id = :id"),
    @NamedQuery(name = "Ticket.findAllByAssignee", query = "SELECT e FROM Ticket e JOIN e.assignee a WHERE a.id = :id"),
    @NamedQuery(name = "Ticket.findAllByCloser", query = "SELECT e FROM Ticket e JOIN e.closer a WHERE a.id = :id"),
    @NamedQuery(name = "Ticket.findAllByCloseRange", query = "SELECT DISTINCT e FROM Ticket e WHERE e.closeEpoch BETWEEN :start AND :end")})
@Entity
public class Ticket implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Ticket.class.getName());
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private TicketStatus status;
    @Enumerated(EnumType.STRING)
    private TicketTopic topic;
    @Enumerated(EnumType.STRING)
    private TicketSeverity severity;
    private String title;
    private String mailTitle;
    
    @ManyToOne
    private User creator;
    @ManyToOne
    private User requester;
    @ManyToOne
    private User closer;
    @ManyToOne
    private User assignee;
    private Long openEpoch;
    private Long closeEpoch;
    private int satisfied;
    @Column(length = 8000)
    private String feedback;
    @ManyToOne
    private DAS das;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<Comment> comments;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.MERGE})
    private List<Tag> tags;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Attachment> attachments;
    private String email;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Transient Properties">
    @Transient
    private CrudService dap;
    @Transient
    private Comment comment;
    @Transient
    private Attachment attachment;

    @Transient
    private boolean sendEmailToAssignee;
    @Transient
    private boolean sendEmailToRequester;
    @Transient
    private List<String> ccEmailRecipients;

    @Transient
    private Map<String, List> available;

    @Transient
    private boolean gmailJobTicketed =  false; 
    
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Ticket() {
        this.attachments = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.comment = new Comment();
        this.attachment = new Attachment();
        this.ccEmailRecipients = new ArrayList<>();

        this.available = new HashMap<>();

        try {
            dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));
        } catch (NamingException ex) {
            LOG.log(Level.CONFIG, null, ex);
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of id. Unique storage key
     *
     * @return the value of id
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the value of das.
     *
     * @return the value of das
     */
    public DAS getDas() {
        return das;
    }

    /**
     * Get the value of title.
     *
     * @return the value of title
     */
    public String getTitle() {
        return title;
    }

    public String getMailTitle() {
		return mailTitle;
	}

	public void setMailTitle(String mailTitle) {
		this.mailTitle = mailTitle;
	}

	/**
     * Get the value of openEpoch.
     *
     * @return the value of openEpoch
     */
    public Long getOpenEpoch() {
        return openEpoch;
    }

    /**
     * Get the value of openEpoch as a formated string.
     *
     * @return the value of openEpoch as a formated string
     */
    public String getOpenTimeStamp() {
        return TemporalUtil.epochToStringDateTime(openEpoch);
    }

    /**
     * Get the value of closeEpoch.
     *
     * @return the value of closeEpoch
     */
    public Long getCloseEpoch() {
        return closeEpoch;
    }

    /**
     * Get the value of comments.
     *
     * @return the value of comments
     */
    public List<Comment> getComments() {
        return comments;
    }

    /**
     * Get the value of tags.
     *
     * @return the value of tags
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Get the value of topic.
     *
     * @return the value of topic
     */
    public TicketTopic getTopic() {
        return topic;
    }

    /**
     * Get the value of creator.
     *
     * @return the value of creator
     */
    public User getCreator() {
        return creator;
    }

    /**
     * Get the value of requester.
     *
     * @return the value of requester
     */
    public User getRequester() {
        return requester;
    }

    /**
     * Get the value of closer.
     *
     * @return the value of closer
     */
    public User getCloser() {
        return closer;
    }

    /**
     * Get the value of assignee.
     *
     * @return the value of assignee
     */
    public User getAssignee() {
        return assignee;
    }

    /**
     * Get the value of feedback.
     *
     * @return the value of feedback
     */
    public String getFeedback() {
        return feedback;
    }

    /**
     * Get the value of attachments.
     *
     * @return the value of attachments
     */
    public List<Attachment> getAttachments() {
        return attachments;
    }

    /**
     * Get the value of status.
     *
     * @return the value of status
     */
    public TicketStatus getStatus() {
        return status;
    }

    /**
     * Get the value of severity.
     *
     * @return the value of severity
     */
    public TicketSeverity getSeverity() {
        return severity;
    }

    /**
     * Get the value of satisfied.
     *
     * @return the value of satisfied
     */
    public int getSatisfied() {
        return satisfied;
    }

    /**
     * Get the value of comment.
     *
     * @return the value of comment
     */
    public Comment getComment() {
        return comment;
    }

    /**
     * Get the value of sendEmailToAssignee.
     *
     * @return the value of sendEmailToAssignee
     */
    public boolean isSendEmailToAssignee() {
        return sendEmailToAssignee;
    }

    /**
     * Get the value of sendEmailToRequester.
     *
     * @return the value of sendEmailToRequester
     */
    public boolean isSendEmailToRequester() {
        return sendEmailToRequester;
    }

    /**
     * Get the value of ccEmailRecipients.
     *
     * @return the value of ccEmailRecipients
     */
    public List<String> getCcEmailRecipients() {
        return ccEmailRecipients;
    }

    /**
     * Get the value of available. A map containing lists of helper objects used
     * to populate the ticket. These include requesters, assignees, das and
     * tags.
     *
     * @return the value of available
     */
    public Map<String, List> getAvailable() {
        return available;
    }

    /**
     * Get the value of attachment.
     *
     * @return the value of attachment
     */
    public Attachment getAttachment() {
        return attachment;
    }

    /**
     * Get the value of email.
     *
     * @return the value of email
     */
    public String getEmail() {
        return email;
    }

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of id. Unique storage key
     *
     * @param id new value of id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Set the value of das.
     *
     * @param das new value of das
     */
    public void setDas(DAS das) {
        this.das = das;
    }

    /**
     * Set the value of title.
     *
     * @param title new value of title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set the value of openEpoch.
     *
     * @param openEpoch new value of openEpoch
     */
    public void setOpenEpoch(Long openEpoch) {
        this.openEpoch = openEpoch;
    }

    /**
     * Set the value of closeEpoch.
     *
     * @param closeEpoch new value of closeEpoch
     */
    public void setCloseEpoch(Long closeEpoch) {
        this.closeEpoch = closeEpoch;
    }

    /**
     * Set the value of comments. CascadeType.PERSIST, CascadeType.MERGE,
     * CascadeType.REMOVE
     *
     * @param comments new value of comments
     */
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    /**
     * Set the value of tags. CascadeType.PERSIST, CascadeType.MERGE,
     * CascadeType.REMOVE
     *
     * @param tags new value of tags
     */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Set the value of creator.
     *
     * @param creator new value of creator
     */
    public void setCreator(User creator) {
        this.creator = creator;
    }

    /**
     * Set the value of requester.
     *
     * @param requester new value of requester
     */
    public void setRequester(User requester) {
        this.requester = requester;
    }

    /**
     * Set the value of closer.
     *
     * @param closer new value of closer
     */
    public void setCloser(User closer) {
        this.closer = closer;
    }

    /**
     * Set the value of assignee.
     *
     * @param assignee new value of assignee
     */
    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    /**
     * Set the value of feedback.
     *
     * @param feedback new value of feedback
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    /**
     * Set the value of attachments. CascadeType.PERSIST, CascadeType.MERGE,
     * CascadeType.REMOVE
     *
     * @param attachments new value of attachments
     */
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    /**
     * Set the value of status.
     *
     * @param status new value of status
     */
    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    /**
     * Set the value of topic.
     *
     * @param topic new value of topic
     */
    public void setTopic(TicketTopic topic) {
        this.topic = topic;
    }

    /**
     * Set the value of severity.
     *
     * @param severity new value of severity
     */
    public void setSeverity(TicketSeverity severity) {
        this.severity = severity;
    }

    /**
     * Set the value of satisfied.
     *
     * @param satisfied new value of satisfied
     */
    public void setSatisfied(int satisfied) {
        this.satisfied = satisfied;
    }

    /**
     * Set the value of comment.
     *
     * @param comment new value of comment
     */
    public void setComment(Comment comment) {
        this.comment = comment;
    }

    /**
     * Set the value of sendEmailToAssignee.
     *
     * @param sendEmailToAssignee new value of sendEmailToAssignee
     */
    public void setSendEmailToAssignee(boolean sendEmailToAssignee) {
        this.sendEmailToAssignee = sendEmailToAssignee;
    }

    /**
     * Set the value of sendEmailToRequester.
     *
     * @param sendEmailToRequester new value of sendEmailToRequester
     */
    public void setSendEmailToRequester(boolean sendEmailToRequester) {
        this.sendEmailToRequester = sendEmailToRequester;
    }

    /**
     * Set the value of ccEmailRecipients.
     *
     * @param ccEmailRecipients new value of ccEmailRecipients
     */
    public void setCcEmailRecipients(List<String> ccEmailRecipients) {
        this.ccEmailRecipients = ccEmailRecipients;
    }

    /**
     * Set the value of available. A map containing lists of helper objects used
     * to populate the ticket. These include requesters, assignees, das and
     * tags.
     *
     * @param available new value of available
     */
    public void setAvailable(Map<String, List> available) {
        this.available = available;
    }

    /**
     * Set the value of attachment.
     *
     * @param attachment new value of attachment
     */
    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * Set the value of email.
     *
     * @param email new value of email
     */
    public void setEmail(String email) {
        this.email = email;
    }

//</editor-fold>
    
    public void setGmailJobTicketed(boolean gmailJobTicketed) {
		this.gmailJobTicketed = gmailJobTicketed;
	}

	/**
     * Creates a new Ticket, adds it to the persistence layer and adds storage
     * related resources.
     *
     * @param navigation
     * @param newStatus
     * @return navigation string
     */
    public String create(String navigation, DTX.TicketStatus newStatus) {

        // Set the ticket variables
        creator = (User) dap.find(User.class, SessionUser.getCurrentUser().getId());

        // Set the satisfaction
        satisfied = 0;
        
        // VAR access
        if (SessionUser.getCurrentUser().isVAR()
                || SessionUser.getCurrentUser().isClient()) {
            requester = creator;
        }

        // Set the person who closed the ticket if its set to solved
        switch (newStatus) {
            case OPEN:
                newOpenStatus(creator);
                break;
            case CLOSED:
                newSolveStatus(creator);
                break;
        }

        // Set the ticket creation
        openEpoch = Calendar.getInstance().getTimeInMillis();

        // Create a time based id for email identification
        email = String.valueOf(new Date().getTime());

        // Sort the tags
        cleanTags();

        // Persist the new ticket
        dap.create(this);

        // send the emails
        email(DTX.EmailTemplate.NEW_TICKET);
        // Push
        if (this.requester != null) {
            push();
        }

        return navigation;

    }

    /**
     * Creates a new Ticket from email, adds it to the persistence layer and
     * adds storage related resources.
     *
     * @param user
     * @param newStatus
     * @return navigation string
     */
    public Ticket create(User user, DTX.TicketStatus newStatus) {

        // Set the ticket variables
        creator = user;
        requester = user;

        // Set the satisfaction
        satisfied = 0;
        
        // Set the person who closed the ticket if its set to solved
        switch (newStatus) {
            case OPEN:
                newOpenStatus(creator);
                break;
            case CLOSED:
                newSolveStatus(creator);
                break;
        }
        // Set the ticket creation
        openEpoch = Calendar.getInstance().getTimeInMillis();

        // Create a time based id for email identification
        email = String.valueOf(new Date().getTime());

        // Sort the tags
        cleanTags();

        // Persist the new ticket
        Ticket newTicket = (Ticket) dap.create(this);

        // send the emails
        email(DTX.EmailTemplate.NEW_TICKET);

        // Push
        if (this.requester != null) {
            push();
        }

        return newTicket;

    }

    /**
     * Push notifications
     */
    private void push() {
            // Push
    	if(!this.gmailJobTicketed){
            EventBus eventBus = EventBusFactory.getDefault().eventBus();
            eventBus.publish("ticket", new FacesMessage(
                    StringEscapeUtils.escapeHtml("New Unassigned Ticket"),
                    StringEscapeUtils.escapeHtml(
                            this.getTitle()
                            + " requested by "
                            + this.getRequester().getContact().buildFullName()
                            + " ("
                            + this.getRequester().getEmail()
                            + ")")));
    	}
    }

    /**
     * Update the persistence layer with a new version of the ticket.
     */
    public void update() {
        dap.update(this);
    }

    /**
     * Removes the ticket from the persistence layer and any associated
     * resources linked to the incident.
     */
    public void delete() {
        // ADMIN access
        if (SessionUser.getCurrentUser().isAdministrator()) {
            dap.delete(Ticket.class, id);
        }
    }

    /**
     * When a ticket is edited this method provides all the dependency setting
     * changes required to update the ticket.
     *
     * @param newStatus
     */
    public void edit(DTX.TicketStatus newStatus) {

        // Check and manage any changes to the ticket status
        switch (newStatus) {
            case OPEN:
                newOpenStatus(SessionUser.getCurrentUser());
                break;
            case CLOSED:
                newSolveStatus(SessionUser.getCurrentUser());
                break;
        }

        // Sort the tags
        cleanTags();
        
        // send the emails
        email(DTX.EmailTemplate.TICKET_MODIFIED);
        
        // reset the comment
        comment = new Comment();
        
        // Persist the ticket
        update();

    }

    /**
     * When a ticket is edited this method provides all the dependency setting
     * changes required to update the ticket created by an email.
     *
     * @param newStatus
     * @param commenter
     */
    public void edit(DTX.TicketStatus newStatus, User commenter) {

        // Check and manage any changes to the ticket status
        switch (newStatus) {
            case OPEN:
                newOpenStatus(commenter);
                break;
            case CLOSED:
                newSolveStatus(commenter);
                break;
        }

        // Sort the tags
        cleanTags();

        // send the emails
        email(DTX.EmailTemplate.TICKET_MODIFIED);
        
        // reset the comment
        comment = new Comment();

        // Reset the CC
        ccEmailRecipients.clear();

        // Persist the ticket
        update();
    }

    /**
     * Converts a ticket status to OPEN.
     */
    private void newOpenStatus(User commenter) {

        switch (status) {
            // Going from SOLVED to OPEN
            case CLOSED:
                closeEpoch = null;
                status = DTX.TicketStatus.OPEN;

                Comment closing = new Comment();
                closing.setCommenter(closer);
                closing.setComment("%REOPENED%");
                closing.setCreateEpoch(new Date().getTime());
                comments.add(closing);
                break;
        }

        try {
            if (!comment.getComment().isEmpty()) {
                // Add the comment
                comment.setCommenter(commenter);
                comment.setCreateEpoch(new Date().getTime());
                comments.add(comment);
            }
        } catch (NullPointerException npe) {
            // Do nothing. The comment is null.
        	LOG.log(Level.CONFIG, npe.getMessage(), npe);
        }
    }

    /**
     * Converts a ticket status to SOLVED.
     */
    private void newSolveStatus(User commenter) {

        try {
            if (!comment.getComment().isEmpty()) {
                // Add the comment
                comment.setCommenter(commenter);
                comment.setCreateEpoch(new Date().getTime());
                comments.add(comment);
            }
        } catch (NullPointerException npe) {
            // The comment is null.
            comment = new Comment();
            // Add the comment
            comment.setCommenter(commenter);
            comment.setCreateEpoch(new Date().getTime());
            comments.add(comment);
        }

        switch (status) {
            // Going from OPEN to SOLVED
            case OPEN:
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, 10);

                status = DTX.TicketStatus.CLOSED;
                closeEpoch = cal.getTimeInMillis();
                closer = SessionUser.getCurrentUser();
                
                // If no assignee has been given make one from the closer
                if (assignee == null && closer.isAdministrator()) {
                    assignee = closer;
                }

                Comment closing = new Comment();
                closing.setCommenter(closer);
                closing.setComment("%CLOSED%");
                closing.setCreateEpoch(cal.getTimeInMillis());
                comments.add(closing);
                if (comment.getComment() == null || comment.getComment().isEmpty()) {
                    comment.setComment("TICKET CLOSED.");
                }
                
                break;
        }
    }

    /**
     * Prepares the tags for persistence.
     */
    private void cleanTags() {
        // Sort the tags
        try {
            if (!tags.isEmpty()) {
                Iterator<Tag> i = tags.iterator();
                while (i.hasNext()) {
                    Tag tag = i.next();
                    Tag existingTag = (Tag) dap.findWithNamedQuery(
                            "Tag.findByName",
                            QueryParameter
                            .with("name", tag.getName())
                            .parameters())
                            .get(0);
                    if (existingTag != null) {
                        tag.setId(existingTag.getId());
                    }
                }
            }
        } catch (Exception e) {
            // Do nothing tags is null
        	LOG.log(Level.CONFIG, e.getMessage(), e);
        }

    }

    /**
     * Determines if the ticket can be initialized for creation by the current
     * user. If it can be initialized it will.
     *
     * @param parameters the URI view parameters
     */
    public void initCreator(Map<String, List<String>> parameters) {

        initAvailableAssignees();
        initAvailableRequesters();
        initAvailableDAS();
        initAvailableTags();

        this.status = DTX.TicketStatus.OPEN;

        // Set the requester if they have been passed as a view parameter
        try {
            List<User> users = dap.findWithNamedQuery(
                    "User.findByEmail",
                    QueryParameter
                    .with("email", parameters.get("requester").get(0))
                    .parameters());

            // Check the user email is registered and can be set as requester
            if (!users.isEmpty() && available.get("requesters").contains(users.get(0))) {
                this.requester = users.get(0);
            }

        } catch (NullPointerException npe) {
            // Do Nothing! The View parameter is null
        	LOG.log(Level.CONFIG, npe.getMessage(), npe);
        }
    }

    /**
     * Determines if the ticket can be initialized for editing by the current
     * user. If it can be initialized it will.
     *
     * @return true if the requesting user is permitted to initialize the ticket
     * for editing purposed. If the return value is true the ticket is also
     * initialized.
     */
    public boolean initEditor() {

        boolean initable = false;

        // Admins can load any ticket
        if (SessionUser.getCurrentUser().isAdministrator()) {
            initable = true;
        }
        /* 
         VAR can load ticket whos requester is either in their company or
         in a client company
         */
        if (SessionUser.getCurrentUser().isVAR()) {
            if (SessionUser.getCurrentUser().getCompany()
                    .equals(requester.getCompany())
                    || SessionUser.getCurrentUser().getCompany().getClients()
                    .contains(requester.getCompany())) {
                initable = true;
            }
        }

        /* 
         Client can load ticket whos requester is a member of their company
         */
        if (SessionUser.getCurrentUser().isClient()) {
            if (SessionUser.getCurrentUser().getCompany()
                    .equals(requester.getCompany())) {
                initable = true;
            }
        }

        if (initable) {
            sortCommentsOldestFirst();
            initAvailableAssignees();
            initAvailableRequesters();
            initAvailableDAS();
            initAvailableTags();
        }

        return initable;
    }

    /**
     * Depending on the user metier it is possible to set the requester to other
     * related users. This method can be called when that list needs to be
     * constructed.
     */
    public void initAvailableRequesters() {

        List<User> ar = null;

        // ADMIN access can set anybody to be a requester
        if (SessionUser.getCurrentUser().isAdministrator()) {
            ar = dap.findWithNamedQuery("User.findAll");
        }
        // VAR access can set people in their own company
        if (SessionUser.getCurrentUser().isVAR()) {
            ar = dap.findWithNamedQuery(
                    "User.findByCompany",
                    QueryParameter.with(
                            "id",
                            SessionUser
                            .getCurrentUser()
                            .getCompany()
                            .getId())
                    .parameters());
        }

        // Set the available requesters
        available.put("requesters", ar);

    }

    /**
     * Depending on the user metier it is possible to set the assignee to other
     * related users. This method can be called when that list needs to be
     * constructed.
     */
    public void initAvailableAssignees() {

        List<User> aa = null;

        // ADMIN access can set anybody to be an assignee
        if (SessionUser.getCurrentUser().isAdministrator()) {
            aa = dap.findWithNamedQuery(
                    "User.findByMetier",
                    QueryParameter.with(
                            "name",
                            DTX.Metier.ADMIN.toString())
                    .parameters());
        }

        // Set the available requesters
        available.put("assignees", aa);

    }

    /**
     * Depending on the user metier it is possible to set the das and the list
     * of available das depends on the user. This method can be called when that
     * list needs to be constructed.
     */
    public void initAvailableDAS() {

        List<DAS> ad = null;

        // ADMIN access can set any das
        if (SessionUser.getCurrentUser().isAdministrator()) {
            ad = dap.findWithNamedQuery("DAS.findAll");
        }
        // VAR and CLIENT access can only set das related to their company
        if (SessionUser.getCurrentUser().isVAR()
                || SessionUser.getCurrentUser().isClient()) {
            User user = (User) dap.find(
                    User.class, SessionUser.getCurrentUser().getId());
            ad = user.getCompany().getDas();
        }

        // Set the available das
        available.put("das", ad);

    }

    /**
     * Depending on the user metier it is possible to set the das and the list
     * of available das depends on the user. This method can be called when that
     * list needs to be constructed.
     */
    public void initAvailableTags() {
        List<Tag> at = dap.findWithNamedQuery("Tag.findAll");

        // Set the available das
        available.put("tags", at);

    }

    /**
     * The auto complete function allows the input field to dynamically filter
     * down options based on currently entered values. This method is called
     * whenever a 'keyup' event is detected in the field and passes in the
     * current value of the field to filter down the possible correct matches
     * from the list of subjects. Values are converted to lower case to make
     * sure that case sensitivity is not required for a match.
     *
     * @param query
     * @return The filtered list of subjects that match the current value of the
     * field.
     */
    public List<User> filterAvailableRequesters(String query) {
        List<User> suggestions = new ArrayList<>();

        for (User user : (List<User>) available.get("requesters")) {
            if (user.getAccount().getCloseEpoch() == null) {
                String name = user.getContact().buildFullName();
                if (name.toLowerCase().startsWith(query.toLowerCase())) {
                    suggestions.add(user);
                }
            }
        }

        return suggestions;
    }

    /**
     * The auto complete function allows the input field to dynamically filter
     * down options based on currently entered values. This method is called
     * whenever a 'keyup' event is detected in the field and passes in the
     * current value of the field to filter down the possible correct matches
     * from the list of subjects. Values are converted to lower case to make
     * sure that case sensitivity is not required for a match.
     *
     * @param query
     * @return The filtered list of subjects that match the current value of the
     * field.
     */
    public List<User> filterAvailableAssignees(String query) {
        List<User> suggestions = new ArrayList<>();

        for (User user : (List<User>) available.get("assignees")) {
            if (user.getAccount().getCloseEpoch() == null) {
                String name = user.getContact().buildFullName();
                if (name.toLowerCase().startsWith(query.toLowerCase())) {
                    suggestions.add(user);
                }
            }
        }

        return suggestions;
    }

    /**
     * The auto complete function allows the input field to dynamically filter
     * down options based on currently entered values. This method is called
     * whenever a 'keyup' event is detected in the field and passes in the
     * current value of the field to filter down the possible correct matches
     * from the list of subjects. Values are converted to lower case to make
     * sure that case sensitivity is not required for a match.
     *
     * @param query
     * @return The filtered list of tags that match the current value of the
     * field.
     */
    public List<Tag> filterAvailableTags(String query) {
        List<Tag> suggestions = new ArrayList<>();

        // Add suggestions from the existing list
        for (Tag t : (List<Tag>) available.get("tags")) {
            String name = t.getName();
            if (name.toLowerCase().startsWith(query.toLowerCase())) {
                suggestions.add(t);
            }
        }

        return suggestions;
    }

    /**
     * The auto complete function allows the input field to dynamically filter
     * down options based on currently entered values. This method is called
     * whenever a 'keyup' event is detected in the field and passes in the
     * current value of the field to filter down the possible correct matches
     * from the list of subjects. Values are converted to lower case to make
     * sure that case sensitivity is not required for a match.
     *
     * @param query
     * @return The filtered list of tags that match the current value of the
     * field.
     */
    public List<String> filterCCEmail(String query) {
        List<String> suggestions = new ArrayList<>();
        // Add the query suggestion
        suggestions.add(query);

        return suggestions;
    }

    /**
     * Retrieves tickets form the persistence layer that are by the same
     * assignee.
     *
     * @param resultLimit
     * @return a list of tickets by the same assignee
     */
    public List<Ticket> relatedTickets(int resultLimit) {

        try {
            // Create the CriteriaQuery
            CriteriaBuilder builder = dap.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(Ticket.class);

            // Set the Root class against which the query is to be performed
            Root ticket = query.from(Ticket.class);

            // Create a new list of Predicates
            List<Predicate> predicates = new ArrayList<>();

            // Split the title and remove linking words
            String[] titleWords = title.split(" ");

            for (String subWord : titleWords) {
                if (!subWord.matches("(is|are|and|as|for|to|in|that|the|this|a|i)")) {
                    Expression literal = builder.literal("%" + subWord + "%");

                    // When the globalFilter is deleted it returns ""
                    if (!"".equals(subWord)) {
                        List<Predicate> globalPredicate = new ArrayList<>();

                        globalPredicate.add(builder.like(ticket.get(Ticket_.title), literal));

                        predicates.add(builder.or(globalPredicate.toArray(new Predicate[globalPredicate.size()])));
                    }
                }
            }

            // Pass all the predicates into the query
            if (predicates.isEmpty()) {
                query.select(ticket);
            } else {
                query.where(predicates.toArray(new Predicate[predicates.size()]));
            }

            return dap.findWithCriteriaQuery(query, resultLimit);
        } catch (NullPointerException npe) {
            // Title was null so return empty list
            return new ArrayList<>();
        }

    }

    /**
     * Retrieves tickets form the persistence layer that are by the same
     * requester.
     *
     * @param resultLimit
     * @return a list of tickets by the same requester
     */
    public List<Ticket> relatedRequester(int resultLimit) {
        return dap.findWithNamedQuery(
                "Ticket.findAllByRequester",
                QueryParameter.with("id", requester.getId()).parameters(), resultLimit);
    }

    /**
     * Retrieves tickets form the persistence layer that are by the same
     * assignee.
     *
     * @param resultLimit
     * @return a list of tickets by the same assignee
     */
    public List<Ticket> relatedAssignee(int resultLimit) {
        return dap.findWithNamedQuery(
                "Ticket.findAllByAssignee",
                QueryParameter.with("id", assignee.getId()).parameters(), resultLimit);
    }

    /**
     * Sort comments into chronological order
     */
    public void sortCommentsOldestFirst() {
        Collections.sort(comments,
                new Comparator<Comment>() {
                    @Override
                    public int compare(Comment c1, Comment c2) {
                        if (Objects.equals(c1.getCreateEpoch(), c2.getCreateEpoch())) {
                            return 0;
                        } else if (c1.getCreateEpoch() < c2.getCreateEpoch()) {
                            return -1;
                        }
                        return 1;
                    }
                }
        );
    }

    /**
     * Sort comments into chronological order
     */
    public void sortCommentsOldestLast() {
        Collections.sort(comments,
                new Comparator<Comment>() {
                    @Override
                    public int compare(Comment c1, Comment c2) {
                        if (Objects.equals(c1.getCreateEpoch(), c2.getCreateEpoch())) {
                            return 0;
                        } else if (c1.getCreateEpoch() > c2.getCreateEpoch()) {
                            return -1;
                        }
                        return 1;
                    }
                }
        );
    }

    /**
     * Retrieves the last comment added to the ticket
     *
     * @return
     */
    public Comment lastComment() {
        Comment last = new Comment();
        last.setCreateEpoch(0L);
        for (Comment c : comments) {
            if (c.getCreateEpoch() > last.getCreateEpoch()) {
                last = c;
            }
        }
        return last;
    }

    /**
     * Simply adds the current transient attachment to the ticket.
     */
    public void addAttachment() {
        attachment.save();
        attachments.add(attachment);
        attachment = new Attachment();
        // Persist the ticket
        update();
    }

    /**
     * Builds and cleans a list of email recipients to whom an email should be
     * send. It then invokes the sendEmail() to dispatch the email.
     *
     * @param template The template to be used for the email
     */
    private void email(DTX.EmailTemplate template) {
        // Build a list of all the emails
        try {
            if (!ccEmailRecipients.isEmpty()) {
                // Make sure the email conforms to proper format
                Iterator<String> i = ccEmailRecipients.iterator();
                while (i.hasNext()) {
                    String ccEmail = i.next();
                    if (!ccEmail.matches(DTX.EMAIL_REGEX)) {
                        i.remove();
                    }
                }
            }
        } catch (NullPointerException npe) {
            // ccEmailRecipients is null
            ccEmailRecipients = new ArrayList<>();
        }
        if (sendEmailToRequester) {
            ccEmailRecipients.add(requester.getEmail());
        }

        if (sendEmailToAssignee && assignee != null) {
            ccEmailRecipients.add(assignee.getEmail());
        }
        
        if(gmailJobTicketed){
	        String noc = ResourceBundle.getBundle("config").getString("notification.of.creation.mail");
	        if(noc!=null){
	        	ccEmailRecipients.add(noc);
	        }
        }
        // Retreive the email template from the database.
        Template temp = (Template) dap.find(
                Template.class, template.getValue());

        // Send the emails
        for (String ccEmail : ccEmailRecipients) {
            sendEmail(ccEmail, temp);
        }
    }

    /**
     * Sends asynchronous email to the specified recipient
     *
     * @param recipient
     */
    private void sendEmail(String recipient, Template template) {
        // Build an new email
        Email e = new Email();

        // Set the recipient
        e.setRecipientEmail(recipient.toLowerCase());

        // Override the template subject
        template.setSubject(title + " (DTX-" + email + ")");

        // Set the variables
        Map<DTX.EmailVariableKey, String> vars = new HashMap<>();
        vars.put(DTX.EmailVariableKey.TICKET_ID, id.toString());
        vars.put(DTX.EmailVariableKey.TICKET_MSG, comment.getComment());
        vars.put(DTX.EmailVariableKey.TICKET_TITLE, title);
        e.setVariables(vars);

        // Set the template
        e.setTemplate(template);

        // Send the email
        new DefaultEmailer().send(e);
    }

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /**
     * Warning - this method won't work in the case the id fields are not set.
     *
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ticket)) {
            return false;
        }
        Ticket other = (Ticket) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
//</editor-fold>

}
