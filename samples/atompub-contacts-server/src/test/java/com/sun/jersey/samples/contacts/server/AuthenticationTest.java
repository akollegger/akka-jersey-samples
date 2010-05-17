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

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.axiom.om.util.Base64;

/**
 * <p>Unit tests for authentication in the Contacts Service.</p>
 */
public class AuthenticationTest extends AbstractTest {
    
    public AuthenticationTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // Partial URIs that should cause authentication errors if invalid
    // (or no) credentials are specified, or succeed for the admin user
    private static final String[][] AUTHENTICATION_ERROR_URIS = {
        { "users" },
        { "users", "admin" },
        { "contacts", "admin" },
    };

    // Expected entity classes corresponding to each AUTHENTICATION_ERROR_URIS
    // entry if access is actually granted
    private static final Class[] AUTHENTICATION_ERROR_CLASSES = {
        Feed.class,
        Entry.class,
        Feed.class,
    };

    public void testAuthenticationAdmin() {
        String credentials = adminCredentials();
        for (int i = 0; i < AUTHENTICATION_ERROR_URIS.length; i++) {
            WebResource resource = resource(AUTHENTICATION_ERROR_URIS[i]);
            try {
                Object result = resource.
                  header("Authorization", credentials).
                  get(AUTHENTICATION_ERROR_CLASSES[i]);
            } catch (UniformInterfaceException e) {
                fail("Status was " + e.getResponse().getStatus() + " instead of 200 for path '" + path(AUTHENTICATION_ERROR_URIS[i]) + "'");
            }
        }
    }

    public void testAuthenticationInvalid() {
        String credentials = Base64.encode("invalid-username:invalid-password".getBytes());
        for (String[] paths : AUTHENTICATION_ERROR_URIS) {
            WebResource resource = resource(paths);
            try {
                resource.
                  header("Authentication", "Basic " + credentials).
                  get(Object.class);
                fail("Should have returned status 401 for path '" + path(paths) + "'");
            } catch (UniformInterfaceException e) {
                if (e.getResponse().getStatus() == 401) {
                    // Expected result
                } else {
                    fail("Status was " + e.getResponse().getStatus() + " instead of 401 for path '" + path(paths) + "'");
                }
            }
        }
    }

    public void testAuthenticationMissing() {
        for (String[] paths : AUTHENTICATION_ERROR_URIS) {
            WebResource resource = resource(paths);
            try {
                resource.get(Object.class);
                fail("Should have returned status 401 for path '" + path(paths) + "'");
            } catch (UniformInterfaceException e) {
                if (e.getResponse().getStatus() == 401) {
                    // Expected result
                } else {
                    fail("Status was " + e.getResponse().getStatus() + " instead of 401 for path '" + path(paths) + "'");
                }
            }
        }
    }

}
