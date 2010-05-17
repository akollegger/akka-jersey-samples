package com.sun.jersey.samples.console.resources

import java.util.{ArrayList, List}
import javax.ws.rs.{GET, Produces,QueryParam}

import se.scalablesolutions.akka.serialization.Serializer._

/**
 * A web resource for a list of colours.
 */
class Colours {

    val colours = Array("red","orange","yellow","green","blue","indigo","violet");
    
    /**
     * Returns a list of colours as plain text, one colour per line.
     * @param filter If not empty, constrains the list of colours to only
     * those that contain this substring
     * @return the list of colours matching the filter
     */
    @GET
    @Produces(Array("text/plain"))
    def getColourListAsText(@QueryParam("match") filter:String) = {
      getMatchingColours(filter).mkString(", ")
   }
    
    /**
     * Returns a list of colours as a JSON array.
     * @param filter If not empty, constrains the list of colours to only 
     * those that contain this substring
     * @return the list of colours matching the filter
     */
    @GET
    @Produces(Array("application/json"))
    def getColourListAsJSON(@QueryParam("match") filter:String) = {
      val json = ScalaJSON.out(getMatchingColours(filter))

      json
    }

    def getMatchingColours(filter:String):Array[String] = {
      filter match {
        case null => colours
        case "" => colours
        case _ => { 
          colours.filter( s => {
            s.contains(filter)
          })
        }
      }
    }
    
}
