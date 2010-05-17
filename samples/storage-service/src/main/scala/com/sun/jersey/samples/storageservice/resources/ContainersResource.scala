package com.sun.jersey.samples.storageservice.resources

import se.scalablesolutions.akka.actor.Actor

import com.sun.jersey.samples.storageservice.Store
import javax.ws.rs.{GET,Produces,Path,PathParam}
import javax.ws.rs.core.{Context,Request,UriInfo}
import javax.ws.rs.core.Response._

@Path("/storage/containers")
@Produces(Array("application/xml"))
trait ContainersResource extends Actor {

  val store:Store

  case object AllContainers

  @Path("{container}")
  def getContainerResource(@Context uriInfo:UriInfo, 
    @Context request:Request, 
    @PathParam("container") containerName:String):ContainerResource = {
    return new ContainerResource(uriInfo, request, containerName, this)
  }
    
  @GET
  def getContainers() = (this !! AllContainers).getOrElse(noContent.build)
       
  def receive = {
    containersMessageHandler orElse containerMessageHandler orElse itemMessageHandler 
  }

  def containersMessageHandler: PartialFunction[Any, Unit] = {
    case AllContainers =>
      println("GET CONTAINERS");
      reply(
        ok(
          <containers>
          { for (c <- store.getContainers()) yield {
            <container>
              <name>{c.name}</name>
              <uri>{c.uri}</uri>
            </container>
            }
          }
          </containers>
          toString
         ) build
      ) 
  }

  def containerMessageHandler: PartialFunction[Any, Unit]

  def itemMessageHandler: PartialFunction[Any, Unit]

}
