package com.sun.jersey.samples.console.resources

import scalaz._
import Scalaz._

import com.sun.jersey.api.representation.Form
import java.io.InputStream
import java.util.Date
import javax.ws.rs.{Consumes, GET, POST, Produces, Path}
import javax.ws.rs.core.{Cookie, Context, HttpHeaders, NewCookie, Response, MultivaluedMap}

import se.scalablesolutions.akka.actor.Actor

/**
 * A Web form resource, produces the form and processes the results of
 * submitting it.
 * 
 */
@Path("/simple_console/form")
@Produces(Array("text/html"))
class FormResource extends Actor {

    case object ProcessForm
    
    val coloursResource = new Colours();
    
    @Path("colours")
    def getColours():Colours  = {
        return coloursResource;
    }
    
    /**
     * Produce a form from a static HTML file packaged with the compiled class
     * @return a stream from which the HTML form can be read.
     */
    @GET
    def getForm():Response = {
        val now = new Date()

        val entity = this.getClass().getClassLoader().getResourceAsStream("form.html")
        Response.ok(entity).
                cookie(new NewCookie("date",now.toString())).build();
    }
    
    /**
     * Process the form submission. Produces a table showing the form field
     * values submitted.
     * @return a dynamically generated HTML table.
     * @param formData the data from the form submission
     */
    @POST
    @Consumes(Array("application/x-www-form-urlencoded"))
    def processForm(@Context headers:HttpHeaders, formData: MultivaluedMap[String, String]) = {
        <html><head><title>Form results</title></head><body> 
        <p>Hello, you entered the following information: </p>
        <table border='1'> 
        { 
          for ( c <- headers.getCookies().values()) yield 
            <tr><td>Cookie:{c.getName()}</td><td>{c.getValue()}</td></tr>
        }
        {
          for (key <- formData.keySet if !key.equals("submit")) yield 
            <tr><td>Form: {key}</td><td>{formData.getFirst(key)}</td></tr>
          
        }
        </table></body></html>.toString
    }

    def receive = {
      case ProcessForm => <h1>Form Processed</h1>
    }
    
}
