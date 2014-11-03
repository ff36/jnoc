/*
 * Created Nov 3, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */

package com.dastrax.app.push;

import javax.faces.application.FacesMessage;
import org.primefaces.push.EventBus;
import org.primefaces.push.RemoteEndpoint;
import org.primefaces.push.annotation.OnClose;
import org.primefaces.push.annotation.OnMessage;
import org.primefaces.push.annotation.OnOpen;
import org.primefaces.push.annotation.PushEndpoint;
import org.primefaces.push.impl.JSONEncoder;

/**
 * {description}
 *
 * @version {project.version}
 * @since Build 141103.103031
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@PushEndpoint("/ticket")
public class NotifyTicketResource {

    @OnMessage(encoders = {JSONEncoder.class})
    public FacesMessage onMessage(FacesMessage message) {
        return message;
    }

    @OnOpen
    public void onOpen(RemoteEndpoint r, EventBus eventBus) {
    }
 
    @OnClose
    public void onClose(RemoteEndpoint r, EventBus eventBus) {
    }
}
