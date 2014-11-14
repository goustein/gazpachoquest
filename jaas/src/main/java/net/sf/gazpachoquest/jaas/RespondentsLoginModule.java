/*******************************************************************************
 * Copyright (c) 2014 antoniomariasanchez at gmail.com. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this distribution, and is
 * available at http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors: antoniomaria - initial API and implementation
 ******************************************************************************/
package net.sf.gazpachoquest.jaas;

import java.util.Collections;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ServerErrorException;

import net.sf.gazpachoquest.api.AuthenticationResource;
import net.sf.gazpachoquest.dto.auth.Account;
import net.sf.gazpachoquest.dto.auth.RoleAccount;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

public class RespondentsLoginModule implements LoginModule {

    private static Logger logger = LoggerFactory.getLogger(RespondentsLoginModule.class);

    private CallbackHandler handler;
    private Subject subject;
    private Account userPrincipal;
    private AuthenticationResource authenticationResource;
    private String endpoint;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
            Map<String, ?> options) {
        endpoint = (String) options.get("gazpachoquest.rest.endpoint");
        handler = callbackHandler;
        this.subject = subject;
        authenticationResource = JAXRSClientFactory.create(endpoint, AuthenticationResource.class,
                Collections.singletonList(new JacksonJsonProvider()), null);
    }

    public void setAuthenticationResource(AuthenticationResource authenticationResource) {
        this.authenticationResource = authenticationResource;
    }

    @Override
    public boolean login() throws LoginException {

        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("username");
        callbacks[1] = new PasswordCallback("password", true);

        try {
            handler.handle(callbacks);
            String username = ((NameCallback) callbacks[0]).getName();
            String password = String.valueOf(((PasswordCallback) callbacks[1]).getPassword());
            logger.info("New username attempt for user: {}", username);
            Account account = authenticationResource.authenticate(password);
            logger.info("Access granted to user {}", account.getFullName());
            userPrincipal = account;
            return true;
        } catch (ClientErrorException e) {
            logger.error(e.getMessage(), e);
            throw new LoginException(e.getMessage());
        } catch (ServerErrorException e) {
            logger.error("Authentication server {} return html status = {} ", endpoint, e.getResponse().getStatus(), e);
            throw new LoginException(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new LoginException("An unknown error has occurred in authentication process");
        }

    }

    @Override
    public boolean commit() throws LoginException {
        subject.getPrincipals().add(userPrincipal);
        for (RoleAccount role : userPrincipal.getRoles()) {
            subject.getPrincipals().add(role);
        }
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        return false;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().remove(userPrincipal);
        for (RoleAccount role : userPrincipal.getRoles()) {
            subject.getPrincipals().remove(role);
        }
        return true;
    }

}
