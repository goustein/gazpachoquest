/*******************************************************************************
 * Copyright (c) 2014 antoniomariasanchez at gmail.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     antoniomaria - initial API and implementation
 ******************************************************************************/
package net.sf.gazpachoquest.questionnaires.i18.impl;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.inject.Named;

import net.sf.gazpachoquest.questionnaires.i18.MessageResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class MessageBundleImpl implements MessageResource {

    private static final Logger logger = LoggerFactory.getLogger(MessageBundleImpl.class);

    public static final String BUNDLE_NAME = "resources.messages";

    public MessageBundleImpl() {
        super();
    }

    @Override
    public String getString(Locale locale, String key) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        // .properties does not like space
        key = key.replaceAll(" ", ""); //$NON-NLS-1$
        // Locale locale = UI.getCurrent().getLocale();
        // Get the bundle for our current locale
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            logger.warn("[MISSING TRANSLATION] Locale: {}  Key: {}", locale, key);
            return String.format("?%s?", key);
        }
    }
}
