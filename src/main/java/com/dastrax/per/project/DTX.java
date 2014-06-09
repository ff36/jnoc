/*
 * Created Jul 10, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.per.project;

/**
 * For ease of maintenance all the application constants are referenced from
 * this class. They are in the project stage package to facilitate transitions
 * between development and production environments if the constants are
 * different The only exceptions are the DynamoTableMap.
 *
 * @version 3.0.0
 * @since Build 3.0.0 (Jul 10, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class DTX {

    public final static String LOCALE_ENGLISH_UK = "en";
    public final static int PASSWORD_LENGTH = 20;
    public final static String EMAIL_REGEX = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
    
    
    /**
     * Storage directory names. Regardless of the storage implementation the
     * file structure naming convention should be identical.
     */
    public static enum StorageDirectory {

        CORE("CORE"),
        CORE_GRAPHICS("GRAPHICS"),
        CORE_IMAGES("IMAGES"),
        CORE_LOGOS("LOGOS"),
        CORE_ICONS("ICONS"),
        USERS("USERS"),
        USER_PROFILE("PROFILE"),
        COMPANIES("COMPANIES"),
        COMPANY_LOGOS("LOGOS"),
        SITE("SITES"),
        TICKETS("TICKETS"),
        TICKET_ATTACHMENTS("ATTACHMENTS"),
        TEMPORARY("TEMPORARY");

        private final String value;

        private StorageDirectory(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    };
    
    /**
     * Storage file names. Regardless of the storage implementation the
     * default fall back file naming convention should be identical.
     */
    public static enum StorageFile {

        CORE_PROFILE_GRAPHIC_HOLDER("profile_holder.svg"),
        CORE_COMPANY_LOGO_HOLDER("company_logo_holder.svg"),
        USER_PROFILE_GRAPHIC("profile.jpg"),
        COMPANY_LOGO("company_logo.png"),
        SITE_PURCHASE_ORDER("purchase_order.pdf");

        private final String value;

        private StorageFile(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    };
    
    /**
     * Temporal display formats supported by the system. These should be used
     * with a SimpleDateFormat.
     */
    public static enum TemporalFormat {

        TIME_FORMAT("HH:mm"),
        DATE_FORMAT("MMMM dd, yyyy"),
        DATE_TIME_FORMAT("MMMM dd, yyyy HH:mm");

        private final String value;

        private TemporalFormat(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    };

    /**
     * Support ticket assignment categories. 
     */
    public static enum TicketAssignment {

        CREATOR,
        REQUESTER,
        CLOSER,
        ASSIGNEE
    };

    /**
     * Current project implementation stages. 
     * DEV = 'Development', 
     * UAT = 'User Acceptance Testing'
     * PRO = 'Production'
     */
    public static enum ProjectStage {

        DEV,
        UAT,
        PRO
    };

    /**
     * Metiers (Roles) that users can be assigned.
     */
    public static enum Metier {

        ADMIN,
        VAR,
        CLIENT
    };

    /**
     * Email Templates. The value is mapped to the unique template ID in the
     * persistence layer.
     */
    public static enum EmailTemplate {

        NEW_ACCOUNT(1L),
        CHANGE_EMAIL(2L),
        CHANGE_PASSWORD(3L),
        NEW_TICKET(4L),
        TICKET_MODIFIED(5L),
        TICKET_SOLVED(6L),
        EXCEPTION(7L),
        ACCOUNT_REQUEST(8L),
        RMA_REQUEST(9L),
        FEEDBACK(10L);

        private final Long value;

        private EmailTemplate(Long value) {
            this.value = value;
        }

        public Long getValue() {
            return value;
        }
    };

    /**
     * Support ticket states. The value is mapped to a display name.
     */
    public static enum TicketStatus {

        OPEN("Open"),
        SOLVED("Solved");
        
        private final String label;

        private TicketStatus(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    };

    /**
     * Support ticket severities. The value is mapped to a display name.
     */
    public static enum TicketSeverity {

        S1("Sev 1 - (Service Down)"),
        S2("Sev 2 - (Service Disruption)"),
        S3("Sev 3 - (General Support)");
        
        private final String label;

        private TicketSeverity(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    };

    /**
     * Support ticket priorities. The value is mapped to a display name.
     */
    public static enum TicketPriority {

        INFO("Info"),
        MINOR("Minor"),
        MAJOR("Major"),
        CRITICAL("Critical");
        
        private final String label;

        private TicketPriority(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
        
    };

    /**
     * Support ticket satisfaction states.
     */
    public static enum TicketSatisfaction {

        NOT_RATED,
        SATISFIED,
        NOT_SATISFIED
    };

    /**
     * Support ticket topics. The value is mapped to a display name.
     */
    public static enum TicketTopic {

        GENERAL("General"),
        EQUIPMENT_FAILURE("Equipment Failure"),
        CONNECTION_FAILURE("Connection Failure"),
        SIGNAL_SOURCE_FAILURE("Signal Source Failure"),
        TOTAL_SYSTEM_FAILURE("Total System Failure"),
        INFORMATIONAL("Informational");
        
        private final String label;

        private TicketTopic(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    };

    /**
     * Company types.
     */
    public static enum CompanyType {

        VAR,
        CLIENT
    };

    /**
     * Filter (entity object) types.
     */
    public static enum FilterType {

        TICKET,
        COMPANY,
        SUBJECT,
        SITE
    };

    /**
     * Incident Event types.
     */
    public static enum IncidentEventType {

        ALARM,
        SECURITY,
        CONTROL
    };

    /**
     * Incident states. The value is mapped to a display name.
     */
    public static enum IncidentStatus {

        OPEN("Open"),
        SOLVED("Solved"),
        ARCHIVED("Archived");
        
        private final String label;

        private IncidentStatus(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    };
    
    /**
     * DMS Firmware types. The value is mapped to a display name.
     */
    public static enum DMSType {

        TWELVE_HUNDRED("1200"),
        R6("R6");

        private final String label;

        private DMSType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    };

    /**
     * Types of telephone numbers. The value is mapped to a display name.
     */
    public static enum TelephoneType {

        DESK("Desk"),
        CELL("Cell"),
        FAX("Fax");
        
        private final String label;

        private TelephoneType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    };

    /**
     * Email template variable names that can be substituted for dynamic 
     * values at runtime.
     */
    public static enum EmailVariableKey {

        LINK,
        PIN,
        DATE,
        TIME,
        TICKET_ID,
        TICKET_TITLE,
        TICKET_STATUS,
        NAME,
        USER_EMAIL,
        COMPANY,
        TELEPHONE,
        RMA,
        STAGE,
        EXCEPTION_NAME,
        EXCEPTION_MESSAGE,
        EXCEPTION_STACK,
        FEEDBACK_MESSAGE,
        AGENT_DEVICE_CATEGORY,
        AGENT_FAMILY,
        AGENT_OPERATING_SYSTEM,
        AGENT_TYPE,
        AGENT_VERSION_NUMBER;
    };

    /**
     * Root Nexus (groups). Root nexus are groups that are inherent to the
     * system and should not be modifiable by Users. The value is mapped to the 
     * unique ID in the persistence layer.
     */
    public static enum RootNexus {

        ADMIN(1L),
        VAR(2L),
        CLIENT(3L),
        ADMIN_VAR(4L);

        private final Long value;

        private RootNexus(Long value) {
            this.value = value;
        }

        public Long getValue() {
            return value;
        }

    };
    
    /**
     * Storage KVP keys. Accessing default paths for storage objects.
     * Storage implementations will construct the respective resource path.
     */
    public static enum KeyType {

        USER_PROFILE_IMAGE,
        COMPANY_LOGO,
        USER_DIRECTORY,
        COMPANY_DIRECTORY,
        TEMPORARY_FILE,
        TICKET_ATTACHEMENT
        
    };
    
    /**
     * Storage URI KVP keys. Accessing default paths for storage objects.
     * Storage implementations will construct the respective resource path.
     */
    public static enum URIType {

        USER_PROFILE_IMAGE,
        COMPANY_LOGO,
        FILE_TYPE_ICON,
        TEMPORARY_FILE,
        ATTACHMENT,
        ICON,
        IMAGE,
        LOGO;
        
    };
    
    /**
     * Image categories that can be cropped. Different image types are cropped
     * using different scaling algorithms.
     */
    public static enum CroppableType {

        USER_PROFILE_IMAGE,
        COMPANY_LOGO
        
    };

    /**
     * Types of uploaded files.
     */
    public static enum UploadType {

        PROFILE_IMAGE,
        COMPANY_LOGO,
        ATTACHMENT;
        
    };
    
}
