/*
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.jersey.samples.bookmark_em.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Primary Key class BookmarkEntityPK for entity class BookmarkEntity
 * 
 * @author japod
 */
@Embeddable
public class BookmarkEntityPK implements Serializable {

    @Column(name = "USERID", nullable = false)
    private String userid;

    @Column(name = "BMID", nullable = false)
    private String bmid;
    
    /** Creates a new instance of BookmarkEntityPK */
    public BookmarkEntityPK() {
    }

    /**
     * Creates a new instance of BookmarkEntityPK with the specified values.
     * @param bmid the bmid of the BookmarkEntityPK
     * @param userid the userid of the BookmarkEntityPK
     */
    public BookmarkEntityPK(String bmid, String userid) {
        this.bmid = bmid;
        this.userid = userid;
    }

    /**
     * Gets the userid of this BookmarkEntityPK.
     * @return the userid
     */
    public String getUserid() {
        return this.userid;
    }

    /**
     * Sets the userid of this BookmarkEntityPK to the specified value.
     * @param userid the new userid
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * Gets the bmid of this BookmarkEntityPK.
     * @return the bmid
     */
    public String getBmid() {
        return this.bmid;
    }

    /**
     * Sets the bmid of this BookmarkEntityPK to the specified value.
     * @param bmid the new bmid
     */
    public void setBmid(String bmid) {
        this.bmid = bmid;
    }

    /**
     * Returns a hash code value for the object.  This implementation computes 
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.bmid != null ? this.bmid.hashCode() : 0);
        hash += (this.userid != null ? this.userid.hashCode() : 0);
        return hash;
    }

    /**
     * Determines whether another object is equal to this BookmarkEntityPK.  The result is 
     * <code>true</code> if and only if the argument is not null and is a BookmarkEntityPK object that 
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BookmarkEntityPK)) {
            return false;
        }
        BookmarkEntityPK other = (BookmarkEntityPK)object;
        if (this.bmid != other.bmid && (this.bmid == null || !this.bmid.equals(other.bmid))) return false;
        if (this.userid != other.userid && (this.userid == null || !this.userid.equals(other.userid))) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs 
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "com.sun.jersey.samples.bookmark.entities.BookmarkEntityPK[bmid=" + bmid + ", userid=" + userid + "]";
    }
    
}
