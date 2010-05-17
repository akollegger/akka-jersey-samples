/*
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://jersey.dev.java.net/CDDL+GPL.html
 * or jersey/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at jersey/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.jersey.samples.contacts.server;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.atom.abdera.ContentHelper;
import com.sun.jersey.samples.contacts.models.User;
import com.sun.jersey.samples.contacts.server.auth.Base64;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import junit.framework.TestCase;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;

/**
 * <p>Abstract base class for JUnit tests of the Contacts Service.</p>
 */
public abstract class AbstractTest extends TestCase {
    
    public AbstractTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.out.println("Starting grizzly ...");
        selectorThread = Server.startServer();
        ClientConfig config = new DefaultClientConfig();
        client = Client.create(config);
        providers = client.getProviders();
        service = client.resource(Server.BASE_URI);
        helper = new ContentHelper(providers);
    }

    @Override
    protected void tearDown() throws Exception {
        helper = null;
        service = null;
        providers = null;
        client = null;
        System.out.println("Stopping grizzly ...");
        if (selectorThread.isRunning()) {
            selectorThread.stopEndpoint();
        }
        selectorThread = null;
        super.tearDown();
    }

    protected static final Abdera abdera = Abdera.getInstance();

    Client client = null;
    ContentHelper helper = null;
    Providers providers = null;
    SelectorThread selectorThread = null;
    WebResource service = null;

    protected static final String CONTACTS_NAMESPACE = "http://example.com/contacts";

    // Value to pass in the "Authorization" header for the admin user
    protected String adminCredentials() {
        return userCredentials("admin", "password");
    }

    protected String path(String[] paths) {
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            sb.append('/');
            sb.append(path);
        }
        return sb.toString();
    }

    protected void createUser(String credentials, String mediaType, String username, String password) {
        Entry entry = abdera.newEntry();
        entry.setId(username);
        entry.setTitle(username);
        User user = new User();
        user.setPassword(password);
        user.setUsername(username);
        helper.setContentEntity(entry, MediaType.APPLICATION_XML_TYPE, user);
        try {
            service.
              path("users").
              type(mediaType).
              header("Authorization", credentials).
              post(entry);
        } catch (UniformInterfaceException e) {
            fail("Returned status " + e.getResponse().getStatus() + " instead of 200");
        }
    }

    protected void deleteUser(String credentials, String username) {
        service.
          path("users").
          path(username).
          header("Authorization", credentials).
          delete();
    }

    protected WebResource resource(String[] paths) {
        WebResource resource = service;
        for (String path : paths) {
            resource = resource.path(path);
        }
        return resource;
    }

    // Value to pass in the "Authorization" header for the specified user
    protected String userCredentials(String username, String password) {
        return "Basic " + new String(Base64.encode((username + ":" + password).getBytes()));
    }


}
