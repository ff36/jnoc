/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.temp;

import com.dastrax.app.util.ExceptionUtil;
import com.dastrax.mesh.email.Email;
import com.dastrax.mesh.email.EmailUtil;
import com.dastrax.per.dao.core.AuditDAO;
import com.dastrax.per.dao.core.EmailParamDAO;
import com.dastrax.per.dao.core.EmailTemplateDAO;
import com.dastrax.per.entity.core.EmailTemplate;
import com.dastrax.per.project.DastraxCst;
import com.dastrax.service.util.JsfUtil;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @version Build 2.0.0
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class RmaRequest implements Serializable {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(RmaRequest.class.getName());

    // Variables----------------------------------------------------------------
    private static final String recipient = "support@solid.com";
    //private static final String recipient = "test1@dev.tarka.tv";
    private String companyName;
    private String projectName;
    private String orderNumber;
    private String responsibleName;
    private String responsibleCompany;
    private String responsibleEmail;
    private String responsiblePhone;
    private String shippingName;
    private String shippingCompany;
    private String shippingPhone;
    private String shippingEmail;
    private String shippingAddress1;
    private String shippingAddress2;
    private String shippingCity;
    private String shippingState;
    private String shippingZip;
    private boolean saturdayDelivery = false;
    private Date deliveryDate = new Date();
    private RmaProductInfo rmaProduct = new RmaProductInfo();
    private List<RmaProductInfo> products = new ArrayList<>();

    // Constructors-------------------------------------------------------------
    public RmaRequest() {
    }

    // EJB----------------------------------------------------------------------
    @EJB
    EmailTemplateDAO emailTemplateDAO;
    @EJB
    EmailUtil emailUtil;
    @EJB
    EmailParamDAO emailParamDAO;
    @EJB
    ExceptionUtil exu;
    @EJB
    AuditDAO auditDAO;

    // Getter-------------------------------------------------------------------
    public static Logger getLOG() {
        return LOG;
    }

    public static String getRecipient() {
        return recipient;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getResponsibleName() {
        return responsibleName;
    }

    public String getResponsibleCompany() {
        return responsibleCompany;
    }

    public String getResponsibleEmail() {
        return responsibleEmail;
    }

    public String getResponsiblePhone() {
        return responsiblePhone;
    }

    public String getShippingName() {
        return shippingName;
    }

    public String getShippingCompany() {
        return shippingCompany;
    }

    public String getShippingPhone() {
        return shippingPhone;
    }

    public String getShippingEmail() {
        return shippingEmail;
    }

    public String getShippingAddress1() {
        return shippingAddress1;
    }

    public String getShippingAddress2() {
        return shippingAddress2;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public String getShippingState() {
        return shippingState;
    }

    public String getShippingZip() {
        return shippingZip;
    }

    public boolean isSaturdayDelivery() {
        return saturdayDelivery;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public List<RmaProductInfo> getProducts() {
        return products;
    }

    public EmailTemplateDAO getEmailTemplateDAO() {
        return emailTemplateDAO;
    }

    public EmailUtil getEmailUtil() {
        return emailUtil;
    }

    public EmailParamDAO getEmailParamDAO() {
        return emailParamDAO;
    }

    public ExceptionUtil getExu() {
        return exu;
    }

    public AuditDAO getAuditDAO() {
        return auditDAO;
    }

    public RmaProductInfo getRmaProduct() {
        return rmaProduct;
    }

    // Setter-------------------------------------------------------------------
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setResponsibleName(String responsibleName) {
        this.responsibleName = responsibleName;
    }

    public void setResponsibleCompany(String responsibleCompany) {
        this.responsibleCompany = responsibleCompany;
    }

    public void setResponsibleEmail(String responsibleEmail) {
        this.responsibleEmail = responsibleEmail;
    }

    public void setResponsiblePhone(String responsiblePhone) {
        this.responsiblePhone = responsiblePhone;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public void setShippingEmail(String shippingEmail) {
        this.shippingEmail = shippingEmail;
    }

    public void setShippingAddress1(String shippingAddress1) {
        this.shippingAddress1 = shippingAddress1;
    }

    public void setShippingAddress2(String shippingAddress2) {
        this.shippingAddress2 = shippingAddress2;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public void setShippingState(String shippingState) {
        this.shippingState = shippingState;
    }

    public void setShippingZip(String shippingZip) {
        this.shippingZip = shippingZip;
    }

    public void setSaturdayDelivery(boolean saturdayDelivery) {
        this.saturdayDelivery = saturdayDelivery;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setProducts(List<RmaProductInfo> products) {
        this.products = products;
    }

    public void setEmailTemplateDAO(EmailTemplateDAO emailTemplateDAO) {
        this.emailTemplateDAO = emailTemplateDAO;
    }

    public void setEmailUtil(EmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }

    public void setEmailParamDAO(EmailParamDAO emailParamDAO) {
        this.emailParamDAO = emailParamDAO;
    }

    public void setExu(ExceptionUtil exu) {
        this.exu = exu;
    }

    public void setAuditDAO(AuditDAO auditDAO) {
        this.auditDAO = auditDAO;
    }

    public void setRmaProduct(RmaProductInfo rmaProduct) {
        this.rmaProduct = rmaProduct;
    }

    // Methods------------------------------------------------------------------
    public String request() {

        String satDel;
        if (saturdayDelivery) {
            satDel = "YES";
        } else {
            satDel = "NO";
        }

        String delDate = new SimpleDateFormat("MM-dd-yyyy").format(deliveryDate);

        // Convert List to string
        String html = "<h4>RMA Details:</h4>"
                + "<br /> Company Name (on invoice): "
                + companyName
                + "<br /> Project Name: "
                + projectName
                + "<br /> Quote/Sales Order/Fulfillment: "
                + orderNumber
                + "<br /><br /> <h4>Replacement Product Ship To Info:</h4>"
                + "<br /> Name: "
                + responsibleName
                + "<br /> Company: "
                + responsibleCompany
                + "<br /> Phone: "
                + responsiblePhone
                + "<br /> Email: "
                + responsibleEmail
                + "<br /><br /> <h4>Replacement Product Ship To Info:</h4>"
                + "<br /> Name: "
                + shippingName
                + "<br /> Company: "
                + shippingCompany
                + "<br /> Phone: "
                + shippingPhone
                + "<br /> Email: "
                + shippingEmail
                + "<br /> Address1: "
                + shippingAddress1
                + "<br /> Address2: "
                + shippingAddress2
                + "<br /> City: "
                + shippingCity
                + "<br /> State: "
                + shippingState
                + "<br /> Zip: "
                + shippingZip
                + "<br /> Ship To address accepts Saturday deliveries?: "
                + satDel
                + "<br /> Date replacement product must be delivered?: "
                + delDate
                + "<br /><br /> <h4>Product Info:</h4>";

        for (RmaProductInfo product : products) {
            String temp = " <br /> Quantity: "
                    + product.qty
                    + "<br /> Serial Number: "
                    + product.serialNumber
                    + "<br /> Part Number: "
                    + product.partNumber
                    + "<br /> Part Name: "
                    + product.partName
                    + "<br /> Reason for return: "
                    + product.returnReason
                    + "<br /> Failure stage: "
                    + product.failureStage;
            html = html + temp;
        }

        // Build an new email
        Email e = new Email();

        // Set the recipient
        e.setRecipientEmail(recipient);

        // Set the variables
        Map<String, String> vars = new HashMap<>();
        vars.put("rma", html);

        e.setVariables(vars);

        // Retreive the email template from the database.
        EmailTemplate et = emailTemplateDAO.findTemplateById(
                DastraxCst.EmailTemplate.RMA_REQUEST.toString());
        e.setTemplate(et);

        // Send the email
        emailUtil.build(e);

        JsfUtil.addSuccessMessage("Your RMA request has been received and submitted for processing.  A support representative will contact you during normal business hours:  Monday-Friday, 5:00 AM PST to 5:00 PM PST.  Estimated response time is within 2 hours.  If the matter requires immediate attention, you may call support directly at 888-409-9997 option 2 or email at: support@solid.com");

        // Carry the message over to the page redirect
        FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getFlash()
                .setKeepMessages(true);
        
        // Set the navigation outcome
        return "access-rma-request-page";

    }

    public void addProduct() {
        products.add(rmaProduct);
        rmaProduct = new RmaProductInfo();
    }

    public void removeProduct(RmaProductInfo rmaProductInfo) {
        Iterator<RmaProductInfo> i = products.iterator();
        while (i.hasNext()) {
            RmaProductInfo rpi = i.next();
            if (rpi == rmaProductInfo) {
                i.remove();
            }
        }
    }

    public class RmaProductInfo {

        // Variable-------------------------------------------------------------
        private int qty = 0;
        private String serialNumber;
        private String partNumber;
        private String partName;
        private String returnReason;
        private String failureStage;

        // Constructor----------------------------------------------------------
        public RmaProductInfo() {
        }

        // Getter---------------------------------------------------------------
        public int getQty() {
            return qty;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public String getPartNumber() {
            return partNumber;
        }

        public String getPartName() {
            return partName;
        }

        public String getReturnReason() {
            return returnReason;
        }

        public String getFailureStage() {
            return failureStage;
        }

        // Setters--------------------------------------------------------------
        public void setQty(int qty) {
            this.qty = qty;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public void setPartNumber(String partNumber) {
            this.partNumber = partNumber;
        }

        public void setPartName(String partName) {
            this.partName = partName;
        }

        public void setReturnReason(String returnReason) {
            this.returnReason = returnReason;
        }

        public void setFailureStage(String failureStage) {
            this.failureStage = failureStage;
        }

    }

}
