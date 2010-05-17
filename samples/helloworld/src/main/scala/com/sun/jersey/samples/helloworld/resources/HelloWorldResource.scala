package com.sun.jersey.samples.helloworld.resources

import se.scalablesolutions.akka.actor.Actor

import javax.ws.rs.{GET, Path, Produces}

// The Scala class will be hosted at the URI path "/helloworld"
@Path("/hello/helloworld")
class HelloWorldResource extends Actor {

  case object Hello
    
  // The Java method will process HTTP GET requests
  @GET 
  // The Java method will produce content identified by the MIME Media
  // type "text/plain"
  @Produces(Array("text/plain"))
  def getClichedMessage = (this !! Hello).getOrElse("couldn't say hello")
    
  def receive = {
    case Hello => {
      // Return some cliched textual content
      reply("Hello World")
    }
  }
}
