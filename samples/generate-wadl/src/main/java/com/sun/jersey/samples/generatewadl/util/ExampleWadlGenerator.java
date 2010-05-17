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
package com.sun.jersey.samples.generatewadl.util;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.server.wadl.WadlGenerator;
import com.sun.jersey.server.wadl.generators.resourcedoc.ResourceDocAccessor;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.MethodDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.ResourceDocType;
import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Doc;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Param;
import com.sun.research.ws.wadl.RepresentationType;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Resource;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;

/**
 * This {@link WadlGenerator} shows how the custom information added by the
 * {@link ExampleDocProcessor} to the resourcedoc.xml can be processed by this
 * {@link WadlGenerator} and is used to extend the generated application.wadl<br>
 * Created on: Jul 20, 2008<br>
 * 
 * @author <a href="mailto:martin.grotzke@freiheit.com">Martin Grotzke</a>
 * @version $Id$
 */
public class ExampleWadlGenerator implements WadlGenerator {
    
    private static final Logger LOG = Logger.getLogger( ExampleWadlGenerator.class.getName() );

    private WadlGenerator _delegate;
    private File _resourceDocFile;
    private ResourceDocAccessor _resourceDoc;

    /* (non-Javadoc)
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#setWadlGeneratorDelegate(com.sun.jersey.server.impl.wadl.WadlGenerator)
     */
    public void setWadlGeneratorDelegate( WadlGenerator delegate ) {
        _delegate = delegate;
    }

    /* (non-Javadoc)
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#getRequiredJaxbContextPath()
     */
    public String getRequiredJaxbContextPath() {
        return _delegate.getRequiredJaxbContextPath();
    }
    
    public void setResourceDocFile( File resourceDocFile ) {
        _resourceDocFile = resourceDocFile;
    }
    
    public void init() throws Exception {
        _delegate.init();
        final ResourceDocType resourceDoc = loadFile( _resourceDocFile, ResourceDocType.class, ResourceDocType.class, MyNamedValueType.class );
        _resourceDoc = new ResourceDocAccessor( resourceDoc );
    }

    private <T> T loadFile( File fileToLoad, Class<T> targetClass, Class<?> ... classesToBeBound ) {
        if ( fileToLoad == null ) {
            throw new IllegalArgumentException( "The resource-doc file to load is not set!" );
        }
        try {
            final JAXBContext c = JAXBContext.newInstance( classesToBeBound );
            final Unmarshaller m = c.createUnmarshaller();
            return targetClass.cast( m.unmarshal( fileToLoad ) );
        } catch( Exception e ) {
            LOG.log( Level.SEVERE, "Could not unmarshal file " + fileToLoad, e );
            throw new RuntimeException( "Could not unmarshal file " + fileToLoad, e );
        }
    }

    /**
     * @return
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#createApplication()
     */
    public Application createApplication() {
        return _delegate.createApplication();
    }

    /**
     * @param resource
     * @param resourceMethod
     * @return
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#createMethod(com.sun.jersey.api.model.AbstractResource, com.sun.jersey.api.model.AbstractResourceMethod)
     */
    public Method createMethod( AbstractResource r,
            AbstractResourceMethod m ) {
        final Method result = _delegate.createMethod( r, m );
        final MethodDocType methodDoc = _resourceDoc.getMethodDoc( r.getResourceClass(), m.getMethod() );
        if ( methodDoc != null && methodDoc.getAny() != null && !methodDoc.getAny().isEmpty() ) {
            for ( Object any : methodDoc.getAny() ) {
                System.out.println( "test: " + ( any instanceof MyNamedValueType ) + " any: " + any );
                if ( any instanceof MyNamedValueType ) {
                    final MyNamedValueType namedValue = (MyNamedValueType) any;
                    if ( ExampleDocProcessor.EXAMPLE_TAG.equals( namedValue.getName() ) ) {
                        final Doc doc = new Doc();
                        doc.getContent().add( namedValue.getValue() );
                        result.getDoc().add( doc );
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#createRequest(com.sun.jersey.api.model.AbstractResource, com.sun.jersey.api.model.AbstractResourceMethod)
     */
    public Request createRequest( AbstractResource arg0,
            AbstractResourceMethod arg1 ) {
        return _delegate.createRequest( arg0, arg1 );
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @return
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#createParam(com.sun.jersey.api.model.AbstractResource, com.sun.jersey.api.model.AbstractMethod, com.sun.jersey.api.model.Parameter)
     */
    public Param createParam( AbstractResource arg0,
            AbstractMethod arg1, Parameter arg2 ) {
        return _delegate.createParam( arg0, arg1, arg2 );
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @return
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#createRequestRepresentation(com.sun.jersey.api.model.AbstractResource, com.sun.jersey.api.model.AbstractResourceMethod, javax.ws.rs.core.MediaType)
     */
    public RepresentationType createRequestRepresentation(
            AbstractResource arg0, AbstractResourceMethod arg1, MediaType arg2 ) {
        return _delegate.createRequestRepresentation( arg0, arg1, arg2 );
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#createResource(com.sun.jersey.api.model.AbstractResource, java.lang.String)
     */
    public Resource createResource( AbstractResource arg0, String arg1 ) {
        return _delegate.createResource( arg0, arg1 );
    }

    /**
     * @return
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#createResources()
     */
    public Resources createResources() {
        return _delegate.createResources();
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#createResponse(com.sun.jersey.api.model.AbstractResource, com.sun.jersey.api.model.AbstractResourceMethod)
     */
    public Response createResponse( AbstractResource arg0,
            AbstractResourceMethod arg1 ) {
        return _delegate.createResponse( arg0, arg1 );
    }

}
