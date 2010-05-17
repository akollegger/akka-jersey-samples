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

package com.sun.jersey.samples.contacts.client;

import com.sun.jersey.samples.contacts.models.Contact;
import com.sun.jersey.samples.contacts.models.User;
import java.util.List;

/**
 * <p>Unit tests for client access to the Contacts Service.</p>
 */
public class UsersClientTest extends AbstractTest {

    public UsersClientTest(String testName) {
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

    public void testCreateUpdateDeleteUser() {
        List<User> users = client.findUsers();
        assertEquals(1, users.size());
        User user = new User();
        user.setId("new_id");
        user.setUsername("OldUsername");
        user.setPassword("OldPassword");
        client.createUser(user);
        user = client.findUser("OldUsername");
        assertNotNull(user);
        assertEquals("new_id", user.getId());
        assertEquals("OldUsername", user.getUsername());
        assertEquals("OldPassword", user.getPassword());
        List<Contact> contacts = client.findContacts("OldUsername");
        assertEquals(0, contacts.size());
        users = client.findUsers();
        assertEquals(2, users.size());
        user.setPassword("NewPassword");
        client.updateUser(user);
        user = client.findUser("OldUsername");
        assertNotNull(user);
        assertEquals("new_id", user.getId());
        assertEquals("OldUsername", user.getUsername());
        assertEquals("NewPassword", user.getPassword());
        client.deleteUser("OldUsername");
        users = client.findUsers();
        assertEquals(1, users.size());
        try {
            client.findUser("OldUsername");
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected result
        }
    }

    public void testAuthenticationNegative() {
        try {
            client = new ContactsClient("http://localhost:" + getPort(9998), "badusername", "badpassword");
            client.findUser("OldUsername");
            fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException e) {
            // Expected result
        }
    }

    public void testFindUserNegative() {
        try {
            client.findUser("badid");
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected result
        }
    }

    public void testFindUsersPositive() {
        List<User> users = client.findUsers();
        assertEquals(1, users.size());
    }


}
