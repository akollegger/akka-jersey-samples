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

package com.sun.jersey.samples.contacts.models;

import org.apache.abdera.model.Element;

/**
 * <p>Model class representing a phone number.</p>
 */
public class PhoneNumber extends Base {


    // ------------------------------------------------------------ Constructors


    /**
     * <p>Construct a default {@link PhoneNumber} instance.</p>
     */
    public PhoneNumber() {
    }


    /**
     * <p>Construct a configured {@link PhoneNumber} instance.</p>
     */
    public PhoneNumber(String number, String label, boolean primary, String uri, String type) {
        setNumber(number);
        setLabel(label);
        setPrimary(primary);
        setUri(uri);
        setType(type);
    }


    // ------------------------------------------------------ Instance Variables


    private String label;               // Optional textual label
    private String number;              // The phone number --> text
    private boolean primary;            // Flag to mark primary
    private String uri;                 // Telephone URI (see RFC 3966)
    private String type;                // Programmatic type --> rel


    // ---------------------------------------------------------- Public Methods


    @Override
    public boolean equals(Object o) {
        if (o instanceof PhoneNumber) {
            PhoneNumber other = (PhoneNumber) o;
            return match(getLabel(), other.getLabel()) &&
                   match(getNumber(), other.getNumber()) &&
                   match(isPrimary(), other.isPrimary()) &&
                   match(getUri(), other.getUri()) &&
                   match(getType(), other.getType());
        } else {
            return false;
        }
    }


    @Override
    public int hashCode() {
        int value = 0;
        if (label != null) {
            value ^= label.hashCode();
        }
        if (number != null) {
            value ^= number.hashCode();
        }
        if (primary) {
            value ^= 1;
        }
        if (uri != null) {
            value ^= uri.hashCode();
        }
        if (type != null) {
            value ^= type.hashCode();
        }
        return value;
    }


    /**
     * <p>Return this {@link PhoneNumber} as an Atom <code>Element</code> suitable for
     * inclusion in a web service request or response.</p>
     */
    public Element asElement() {
        Element element = abdera.getFactory().newExtensionElement(PHONE_NUMBER_QNAME);
        if (getLabel() != null) {
            element.setAttributeValue("label", getLabel());
        }
        if (getNumber() != null) {
            element.setText(getNumber());
        }
        if (isPrimary()) {
            element.setAttributeValue("primary", "true");
        }
        if (getType() != null) {
            element.setAttributeValue("rel", getType());
        }
        if (getUri() != null) {
            element.setAttributeValue("uri", getUri());
        }
        return element;
    }


    /**
     * <p>Return a new {@link PhoneNumber} created from the contents of the
     * specified <code>Element</code>.</p>
     *
     * @param element Atom <code>Element</code> containing our details
     */
     public static PhoneNumber fromElement(Element element) {
         PhoneNumber instance = new PhoneNumber();
         instance.setLabel(element.getAttributeValue("label"));
         instance.setNumber(element.getText());
         if ("true".equals(element.getAttributeValue("primary"))) {
             instance.setPrimary(true);
         }
         instance.setType(element.getAttributeValue("rel"));
         instance.setUri(element.getAttributeValue("protocol"));
         return instance;
     }


    // --------------------------------------------------------- Private Methods


    // -------------------------------------------------------- Property Methods


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("phoneNumber:{type:%s,label: %s,number: %s}", type, label, number);
    }
}
