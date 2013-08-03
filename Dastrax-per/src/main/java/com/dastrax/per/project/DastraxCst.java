/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */

package com.dastrax.per.project;

/**
 * For ease of maintenance all the application constants are referenced from this
 * class. They are in the project stage package to facilitate transitions between
 * development and production environments if the constants are different
 * The only exceptions are the DynamoTableMap.
 * 
 * @version Build 2.0.0
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class DastraxCst {
    
    // S3 folder names----------------------------------------------------------
    public final static String S3_CORE_DIRECTORY = "CORE";
    public final static String S3_CORE_GRAPHICS_DIRECTORY = "GRAPHICS";
    public final static String S3_CORE_IMAGES_DIRECTORY = "IMAGES";
    public final static String S3_CORE_LOGOS_DIRECTORY = "LOGOS";
    public final static String S3_CORE_ICONS_DIRECTORY = "ICONS";
    public final static String S3_SUBJECTS_DIRECTORY = "SUBJECTS";
    public final static String S3_SUBJECT_PROFILE_DIRECTORY = "PROFILE"; 
    public final static String S3_COMPANIES_DIRECTORY = "COMPANIES";
    public final static String S3_COMPANY_LOGOS_DIRECTORY = "LOGOS";
    public final static String S3_SITE_DIRECTORY = "SITES";
    
    // S3 References------------------------------------------------------------
    public final static String S3_CORE_PROFILE_GRAPHIC_HOLDER = "profile_holder.svg";    
    public final static String S3_CORE_COMPANY_LOGO_HOLDER = "company_logo_holder.svg";
    public final static String S3_SUBJECT_PROFILE_GRAPHIC = "profile.jpg";
    public final static String S3_COMPANY_LOGO = "company_logo.png";
    public final static String S3_SITE_PURCHASE_ORDER = "purchase_order.pdf";
    
    // Supported Locale---------------------------------------------------------
    public final static String LOCALE_ENGLISH_UK = "en";
    
    // Enums--------------------------------------------------------------------
    public static enum TicketStatus {PENDING, OPEN, SOLVED, ARCHIVED};
    public static enum TicketAssignment {CREATOR, REQUESTER, CLOSER, ASSIGNEE};
    public static enum ProjectStage {DEV, UAT, PRO};
    public static enum S3ContentType {PDF, JPEG, PNG, GIF, BMP, TIFF, PLAIN, RTF, MSWORD, ZIP};
    public static enum Metier {ADMIN, VAR, CLIENT, SOLO, ADHOC};
    public static enum CompanyType {VAR, CLIENT};
    public static enum EmailTemplate {NEW_ACCOUNT, CHANGE_EMAIL, CHANGE_PASSWORD, NEW_TICKET, TICKET_COMMENT, TICKET_CLOSED, EXCEPTION};
    public static enum L1Permissions {USER, SITE, VAR, CLIENT, TICKET, PAGE};
    public static enum L2Permissions {CREATE, UPDATE, DELETE, VIEW};
    public static enum ResponseJsf {SUCCESS, WARNING, ERROR, FATAL};

}
