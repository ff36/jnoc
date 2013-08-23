/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.csm.pojo;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @version Build 2.0.0
 * @since Aug 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class JsonMsg {

        // Variables----------------------------------------------------------------
        private String id;
        private String ip;
        private long startEpoch;
        private long stopEpoch;
        private String name;
        private String squealer;

        // Constructors-------------------------------------------------------------
        public JsonMsg() {
        }

        // Getters------------------------------------------------------------------
        public String getId() {
            return id;
        }

        public String getIp() {
            return ip;
        }

        public long getStartEpoch() {
            return startEpoch;
        }

        public long getStopEpoch() {
            return stopEpoch;
        }

        public String getName() {
            return name;
        }

        public String getSquealer() {
            return squealer;
        }

        // Setters------------------------------------------------------------------
        @JsonProperty("id")
        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("ip_addr")
        public void setIp(String ip) {
            this.ip = ip;
        }

        @JsonProperty("set_epoch")
        public void setStartEpoch(long startEpoch) {
            this.startEpoch = startEpoch;
        }

        @JsonProperty("clear_epoch")
        public void setStopEpoch(long stopEpoch) {
            this.stopEpoch = stopEpoch;
        }

        @JsonProperty("alarm_name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("squealer_name")
        public void setSquealer(String squealer) {
            this.squealer = squealer;
        }

    }
