/*
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.jersey.samples.managedbeans;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Naresh (Srinivas.Bhimisetty@Sun.Com)
 */
public class ManagedBeanWebAppTest extends JerseyTest {


    public ManagedBeanWebAppTest() throws Exception {
        super(new WebAppDescriptor.Builder("com.sun.jersey.samples.managedbeans.resources")
                .contextPath("managed-beans-webapp").servletPath("app").build());
    }

    /**
     * Test that the expected response is sent back.
     * @throws java.lang.Exception
     */
    @Test
    public void testPerRequestResource() throws Exception {
        WebResource webResource = resource().queryParam("x", "x");
        String responseMsg = webResource.path("managedbean/per-request").get(String.class);
        assertEquals("message x1", responseMsg);

        responseMsg = webResource.path("managedbean/per-request").get(String.class);
        assertEquals("message x1", responseMsg);
    }

    @Test
    public void testSingletonResource() throws Exception {
        WebResource webResource = resource();
        String responseMsg = webResource.path("managedbean/singleton").get(String.class);
        assertEquals("message 1", responseMsg);

        responseMsg = webResource.path("managedbean/singleton").get(String.class);
        assertEquals("message 2", responseMsg);

        responseMsg = webResource.path("managedbean/singleton").get(String.class);
        assertEquals("message 3", responseMsg);
    }

    @Test
    public void testExceptionMapper() throws Exception {
        WebResource webResource = resource().path("managedbean/singleton/exception");

        ClientResponse cr = webResource.get(ClientResponse.class);
        assertEquals(500, cr.getStatus());
        assertEquals("ManagedBeanException", cr.getEntity(String.class));
    }
    
    @Test
    public void testApplicationWadl() {
        WebResource webResource = resource();
        String serviceWadl = webResource.path("application.wadl").
                accept(MediaTypes.WADL).get(String.class);

        assertTrue(serviceWadl.length() > 0);
    }    

}