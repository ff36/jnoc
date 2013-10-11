/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.cnx.pojo;

import com.dastrax.per.entity.core.Site;

/**
 *
 * @version Build 1.0.0
 * @since Sep 17, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class SnmpResult {

    // Variables----------------------------------------------------------------
    private Site site;
    private boolean confDastrax;
    private boolean dastraxConf;
    private boolean dastraxr;
    private boolean dastraxSgd;
    private boolean dastraxMon;
    private boolean dastraxD;
    private boolean dastraxCntl;

    // Constructors-------------------------------------------------------------
    public SnmpResult() {
        confDastrax = false;
        dastraxConf = false;
        dastraxr = false;
        dastraxSgd = false;
        dastraxMon = false;
        dastraxD = false;
        dastraxCntl = false;
    }

    // Getters------------------------------------------------------------------
    public Site getSite() {
        return site;
    }

    public boolean isConfDastrax() {
        return confDastrax;
    }

    public boolean isDastraxConf() {
        return dastraxConf;
    }

    public boolean isDastraxr() {
        return dastraxr;
    }

    public boolean isDastraxSgd() {
        return dastraxSgd;
    }

    public boolean isDastraxMon() {
        return dastraxMon;
    }

    public boolean isDastraxD() {
        return dastraxD;
    }

    public boolean isDastraxCntl() {
        return dastraxCntl;
    }

    // Setters------------------------------------------------------------------
    public void setSite(Site site) {
        this.site = site;
    }

    public void setConfDastrax(boolean confDastrax) {
        this.confDastrax = confDastrax;
    }

    public void setDastraxConf(boolean dastraxConf) {
        this.dastraxConf = dastraxConf;
    }

    public void setDastraxr(boolean dastraxr) {
        this.dastraxr = dastraxr;
    }

    public void setDastraxSgd(boolean dastraxSgd) {
        this.dastraxSgd = dastraxSgd;
    }

    public void setDastraxMon(boolean dastraxMon) {
        this.dastraxMon = dastraxMon;
    }

    public void setDastraxD(boolean dastraxD) {
        this.dastraxD = dastraxD;
    }

    public void setDastraxCntl(boolean dastraxCntl) {
        this.dastraxCntl = dastraxCntl;
    }

    // Methods------------------------------------------------------------------
}
