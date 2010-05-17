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
package com.sun.jersey.samples.extendedwadl.resources;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.samples.extendedwadl.model.Item;
import com.sun.jersey.samples.extendedwadl.util.Examples;
import com.sun.jersey.spi.resource.Singleton;

/**
 * This is the root resource for managing items.
 * 
 * @author <a href="mailto:martin.grotzke@freiheit.com">Martin Grotzke</a>
 * @version $Id$
 */
@Singleton
@Path("items")
public class ItemsResource {
    
    @Context UriInfo _uriInfo;
    private final AtomicInteger _sequence;
    private final Map<Integer,Item> _repository;
    
    public ItemsResource() {
        _sequence = new AtomicInteger();
        _repository = new HashMap<Integer, Item>();
    }

    @Path("{id}")
    public ItemResource getItem( @PathParam("id") final Integer id ) {
        final Item item = _repository.get( id );
        if ( item == null ) {
            throw new NotFoundException("Item with id " + id + " does not exist!");
        }
        
        return new ItemResource( item );
    }

    /**
     * Add a new item to the list of managed items. The item will get assigned an id,
     * the resource where the item is available will be returned in the location header.
     * 
     * @param item The item to create.
     * 
     * @request.representation.qname {http://www.example.com}item
     * @request.representation.mediaType application/xml
     * @request.representation.example {@link Examples#SAMPLE_ITEM}
     * 
     * @response.param {@name Location}
     *                  {@style header}
     *                  {@type {http://www.w3.org/2001/XMLSchema}anyURI}
     *                  {@doc The URI where the created item is accessable.}
     * 
     * @return The response with the status code and the location header.
     * 
     */
    @POST
    @Consumes( { "application/xml" } )
    public Response createItem( Item item ) {
        final Integer id = _sequence.incrementAndGet();
        _repository.put( id, item );
        return Response.created( 
                _uriInfo.getAbsolutePathBuilder().clone().path( id.toString() ).build() )
                .build();
    }
    
}
