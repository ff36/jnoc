/**
 *  Copyright (C) 2015  555 inc ltd.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.jnoc.service.rma;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * An Return Material Authorization (RMA).
 *
 * @version 2.0.0
 * @since Build 2.1.0 (May 26, 2014)
 * @author Tarka L'Herpiniere

 */
public class RMA {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(RMA.class.getName());

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
    private boolean saturdayDelivery;
    private Date deliveryDate;
    private RMA.RmaProduct newRmaProduct;
    private List<RMA.RmaProduct> products;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Creates a new instance of RMA
     */
    public RMA() {
        this.saturdayDelivery = false;
        this.products = new ArrayList<>();
        this.newRmaProduct = new RMA.RmaProduct();
        this.deliveryDate = new Date();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of companyName
     *
     * @return the value of companyName
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Get the value of projectName
     *
     * @return the value of projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Get the value of orderNumber
     *
     * @return the value of orderNumber
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Get the value of responsibleName
     *
     * @return the value of responsibleName
     */
    public String getResponsibleName() {
        return responsibleName;
    }

    /**
     * Get the value of responsibleCompany
     *
     * @return the value of responsibleCompany
     */
    public String getResponsibleCompany() {
        return responsibleCompany;
    }

    /**
     * Get the value of responsibleEmail
     *
     * @return the value of responsibleEmail
     */
    public String getResponsibleEmail() {
        return responsibleEmail;
    }

    /**
     * Get the value of responsiblePhone
     *
     * @return the value of responsiblePhone
     */
    public String getResponsiblePhone() {
        return responsiblePhone;
    }

    /**
     * Get the value of shippingName
     *
     * @return the value of shippingName
     */
    public String getShippingName() {
        return shippingName;
    }

    /**
     * Get the value of shippingCompany
     *
     * @return the value of shippingCompany
     */
    public String getShippingCompany() {
        return shippingCompany;
    }

    /**
     * Get the value of shippingPhone
     *
     * @return the value of shippingPhone
     */
    public String getShippingPhone() {
        return shippingPhone;
    }

    /**
     * Get the value of shippingEmail
     *
     * @return the value of shippingEmail
     */
    public String getShippingEmail() {
        return shippingEmail;
    }

    /**
     * Get the value of shippingAddress1
     *
     * @return the value of shippingAddress1
     */
    public String getShippingAddress1() {
        return shippingAddress1;
    }

    /**
     * Get the value of shippingAddress2
     *
     * @return the value of shippingAddress2
     */
    public String getShippingAddress2() {
        return shippingAddress2;
    }

    /**
     * Get the value of shippingCity
     *
     * @return the value of shippingCity
     */
    public String getShippingCity() {
        return shippingCity;
    }

    /**
     * Get the value of shippingState
     *
     * @return the value of shippingState
     */
    public String getShippingState() {
        return shippingState;
    }

    /**
     * Get the value of shippingZip
     *
     * @return the value of shippingZip
     */
    public String getShippingZip() {
        return shippingZip;
    }

    /**
     * Get the value of saturdayDelivery
     *
     * @return the value of saturdayDelivery
     */
    public boolean isSaturdayDelivery() {
        return saturdayDelivery;
    }

    /**
     * Get the value of deliveryDate
     *
     * @return the value of deliveryDate
     */
    public Date getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * Get the value of newRmaProduct
     *
     * @return the value of newRmaProduct
     */
    public RmaProduct getNewRmaProduct() {
        return newRmaProduct;
    }

    /**
     * Get the value of products
     *
     * @return the value of products
     */
    public List<RmaProduct> getProducts() {
        return products;
    }

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of companyName.
     *
     * @param companyName new value of companyName
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    /**
     * Set the value of projectName.
     *
     * @param projectName new value of projectName
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    /**
     * Set the value of orderNumber.
     *
     * @param orderNumber new value of orderNumber
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    /**
     * Set the value of responsibleName.
     *
     * @param responsibleName new value of responsibleName
     */
    public void setResponsibleName(String responsibleName) {
        this.responsibleName = responsibleName;
    }
    
    /**
     * Set the value of responsibleCompany.
     *
     * @param responsibleCompany new value of responsibleCompany
     */
    public void setResponsibleCompany(String responsibleCompany) {
        this.responsibleCompany = responsibleCompany;
    }
    
    /**
     * Set the value of responsibleEmail.
     *
     * @param responsibleEmail new value of responsibleEmail
     */
    public void setResponsibleEmail(String responsibleEmail) {
        this.responsibleEmail = responsibleEmail;
    }
    
    /**
     * Set the value of responsiblePhone.
     *
     * @param responsiblePhone new value of responsiblePhone
     */
    public void setResponsiblePhone(String responsiblePhone) {
        this.responsiblePhone = responsiblePhone;
    }
    
    /**
     * Set the value of shippingName.
     *
     * @param shippingName new value of shippingName
     */
    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }
    
    /**
     * Set the value of shippingCompany.
     *
     * @param shippingCompany new value of shippingCompany
     */
    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }
    
    /**
     * Set the value of shippingPhone.
     *
     * @param shippingPhone new value of shippingPhone
     */
    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }
    
    /**
     * Set the value of shippingEmail.
     *
     * @param shippingEmail new value of shippingEmail
     */
    public void setShippingEmail(String shippingEmail) {
        this.shippingEmail = shippingEmail;
    }
    
    /**
     * Set the value of shippingAddress1.
     *
     * @param shippingAddress1 new value of shippingAddress1
     */
    public void setShippingAddress1(String shippingAddress1) {
        this.shippingAddress1 = shippingAddress1;
    }
    
    /**
     * Set the value of shippingAddress2.
     *
     * @param shippingAddress2 new value of shippingAddress2
     */
    public void setShippingAddress2(String shippingAddress2) {
        this.shippingAddress2 = shippingAddress2;
    }
    
    /**
     * Set the value of shippingCity.
     *
     * @param shippingCity new value of shippingCity
     */
    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }
    
    /**
     * Set the value of shippingState.
     *
     * @param shippingState new value of shippingState
     */
    public void setShippingState(String shippingState) {
        this.shippingState = shippingState;
    }
    
    /**
     * Set the value of shippingZip.
     *
     * @param shippingZip new value of shippingZip
     */
    public void setShippingZip(String shippingZip) {
        this.shippingZip = shippingZip;
    }
    
    /**
     * Set the value of saturdayDelivery.
     *
     * @param saturdayDelivery new value of saturdayDelivery
     */
    public void setSaturdayDelivery(boolean saturdayDelivery) {
        this.saturdayDelivery = saturdayDelivery;
    }
    
    /**
     * Set the value of deliveryDate.
     *
     * @param deliveryDate new value of deliveryDate
     */
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    /**
     * Set the value of newRmaProduct.
     *
     * @param newRmaProduct new value of newRmaProduct
     */
    public void setNewRmaProduct(RmaProduct newRmaProduct) {
        this.newRmaProduct = newRmaProduct;
    }
    
    /**
     * Set the value of products.
     *
     * @param products new value of products
     */
    public void setProducts(List<RmaProduct> products) {
        this.products = products;
    }
//</editor-fold>

    /**
     * Resets the current RMA product.
     */
    public void resetRmaProduct() {
        newRmaProduct = new RMA.RmaProduct();
    }
    
    /**
     * An product relating to an RMA request.
     *
     * @version 2.0.0
     * @since Build 2.0.0 (May 26, 2014)
     * @author Tarka L'Herpiniere
    
     */
    public class RmaProduct {

        //<editor-fold defaultstate="collapsed" desc="Properties">
        private int qty = 0;
        private String serialNumber;
        private String partNumber;
        private String partName;
        private String returnReason;
        private String failureStage;
        private String issueDetails;
        private String troubleshooting;
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Constructors">
        public RmaProduct() {
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Getters">
        /**
         * Get the value of qty
         *
         * @return the value of qty
         */
        public int getQty() {
            return qty;
        }

        /**
         * Get the value of serialNumber
         *
         * @return the value of serialNumber
         */
        public String getSerialNumber() {
            return serialNumber;
        }

        /**
         * Get the value of partNumber
         *
         * @return the value of partNumber
         */
        public String getPartNumber() {
            return partNumber;
        }

        /**
         * Get the value of partName
         *
         * @return the value of partName
         */
        public String getPartName() {
            return partName;
        }

        /**
         * Get the value of returnReason
         *
         * @return the value of returnReason
         */
        public String getReturnReason() {
            return returnReason;
        }

        /**
         * Get the value of failureStage
         *
         * @return the value of failureStage
         */
        public String getFailureStage() {
            return failureStage;
        }

        /**
         * Get the value of issueDetails
         *
         * @return the value of issueDetails
         */
        public String getIssueDetails() {
            return issueDetails;
        }

        /**
         * Get the value of troubleshooting
         *
         * @return the value of troubleshooting
         */
        public String getTroubleshooting() {
            return troubleshooting;
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Setters">
        /**
         * Set the value of qty.
         *
         * @param qty new value of qty
         */
        public void setQty(int qty) {
            this.qty = qty;
        }
        
        /**
         * Set the value of serialNumber.
         *
         * @param serialNumber new value of serialNumber
         */
        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }
        
        /**
         * Set the value of partNumber.
         *
         * @param partNumber new value of partNumber
         */
        public void setPartNumber(String partNumber) {
            this.partNumber = partNumber;
        }
        
        /**
         * Set the value of partName.
         *
         * @param partName new value of partName
         */
        public void setPartName(String partName) {
            this.partName = partName;
        }
        
        /**
         * Set the value of returnReason.
         *
         * @param returnReason new value of returnReason
         */
        public void setReturnReason(String returnReason) {
            this.returnReason = returnReason;
        }
        
        /**
         * Set the value of failureStage.
         *
         * @param failureStage new value of failureStage
         */
        public void setFailureStage(String failureStage) {
            this.failureStage = failureStage;
        }
        
        /**
         * Set the value of issueDetails.
         *
         * @param issueDetails new value of issueDetails
         */
        public void setIssueDetails(String issueDetails) {
            this.issueDetails = issueDetails;
        }
        
        /**
         * Set the value of troubleshooting.
         *
         * @param troubleshooting new value of troubleshooting
         */
        public void setTroubleshooting(String troubleshooting) {
            this.troubleshooting = troubleshooting;
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Override">
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final RmaProduct other = (RmaProduct) obj;
            if (!Objects.equals(this.serialNumber, other.serialNumber)) {
                return false;
            }
            if (!Objects.equals(this.partNumber, other.partNumber)) {
                return false;
            }
            if (!Objects.equals(this.partName, other.partName)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + Objects.hashCode(this.serialNumber);
            hash = 67 * hash + Objects.hashCode(this.partNumber);
            hash = 67 * hash + Objects.hashCode(this.partName);
            return hash;
        }
//</editor-fold>

    }

}
