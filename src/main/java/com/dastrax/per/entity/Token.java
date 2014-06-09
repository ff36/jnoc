/*
 * Created Jul 10, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.per.entity;

import com.dastrax.per.dap.CrudService;
import com.dastrax.per.dap.QueryParameter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 *
 * Token holds temporary information that is required to perform a task beyond
 * the scope of the users session or where no link exists between the session
 * that created the token and the session that responds to it. eg. Confirming an
 * email address.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@NamedQueries({
    @NamedQuery(name = "Token.findAll", query = "SELECT e FROM Token e"),
    @NamedQuery(name = "Token.findByID", query = "SELECT e FROM Token e WHERE e.id = :id"),
    @NamedQuery(name = "Token.findByEmail", query = "SELECT e FROM Token e WHERE e.email = :email"),
    @NamedQuery(name = "Token.findByEpoch", query = "SELECT e FROM Token e WHERE e.createEpoch < :epoch")
})
@Entity
public class Token implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Token.class.getName());
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    private String email;
    @Column(name = "USERID")
    private String user;
    @Column(name = "PARAMETERVALUES")
    private String parameters;
    private Long createEpoch;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Token() {
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of version
     *
     * @return the value of version
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the value of email
     *
     * @return the value of email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get the value of user
     *
     * @return the value of user
     */
    public String getUser() {
        return user;
    }

    /**
     * Get the value of createEpoch
     *
     * @return the value of createEpoch
     */
    public Long getCreateEpoch() {
        return createEpoch;
    }

//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of id.
     *
     * @param id new value of id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Set the value of email.
     *
     * @param email new value of email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Set the value of user.
     *
     * @param user new value of user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Set the value of createEpoch.
     *
     * @param createEpoch new value of createEpoch
     */
    public void setCreateEpoch(Long createEpoch) {
        this.createEpoch = createEpoch;
    }

//</editor-fold>
    /**
     * Set the value of parameters.
     *
     * @param parameters new value of parameters
     */
    public void setParameters(Map<String, String> parameters) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            this.parameters = mapper.writeValueAsString(parameters);
        } catch (JsonProcessingException ex) {
            // Unable to convert map to JSON string
        }
    }

    /**
     * Get the value of parameters
     *
     * @return the value of parameters
     */
    public Map<String, String> getParameters() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(this.parameters,
                    new TypeReference<HashMap<String, String>>() {
                    });
        } catch (IOException ex) {
            // Unable to convert JSON to map
        }
        return null;
    }

    /**
     * Tidies up the database by deleting any tokens that are over 24 hours old.
     * This method is asynchronous and will return immedietly regardless of
     * whether the operation was successful or not.
     *
     */
    @Asynchronous
    public void tidy() {
        try {

            CrudService dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));

            // Get the tokens with a creationEpoch more than 24 hours ago
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -1);
            List<Token> tokens
                    = dap.findWithNamedQuery(
                            "Token.findByEpoch",
                            QueryParameter
                            .with("epoch", cal.getTimeInMillis())
                            .parameters());

            for (Token token : tokens) {
                dap.delete(Token.class, token.id);
            }

        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.id);
        return hash;
    }

    /**
     * Warning - this method won't work in the case the id fields are not set.
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Token other = (Token) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
//</editor-fold>

}
