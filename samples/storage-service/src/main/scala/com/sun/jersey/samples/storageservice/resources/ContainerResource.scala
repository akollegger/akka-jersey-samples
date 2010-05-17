package com.sun.jersey.samples.storageservice.resources

import com.sun.jersey.api.NotFoundException
import com.sun.jersey.samples.storageservice.{Container,Item,Store}
import java.net.URI
import java.util.Iterator
import javax.ws.rs.{DELETE,GET,PUT,Produces,QueryParam,Path,PathParam}
import javax.ws.rs.core.{Context,Request,Response,UriInfo}
import javax.ws.rs.core.Response._

import scala.xml.Elem

import se.scalablesolutions.akka.actor.Actor

import scalaz._
import Scalaz._

case class GetContainer(named:String)
case class SearchContainer(named:String, search:String)
case class PutContainer(named:String, uriInfo:UriInfo)
case class DeleteContainer(named:String)

@Produces(Array("application/xml"))
class ContainerResource(uriInfo:UriInfo, request:Request, containerName:String, 
  resourceActor:Actor) 
{

  @GET
  def getContainer(@QueryParam("search") search:String):Elem = {
    if (search != null) 
      (resourceActor !! SearchContainer(containerName, search)).getOrElse(
        throw new NotFoundException("Container not found"))
    else 
      (resourceActor !! GetContainer(containerName)).getOrElse(
        throw new NotFoundException("Container not found"))
    }



  @PUT
  def putContainer():Response = 
    (resourceActor !! PutContainer(containerName,uriInfo)).getOrElse(noContent.build)
    
  @DELETE
  def deleteContainer():Unit = 
    (resourceActor !! DeleteContainer(containerName)).getOrElse(noContent.build)
    
    
  @Path("{item: .+}")
  def getItemResource(@PathParam("item") item:String):ItemResource = {
    return new ItemResource(uriInfo, request, containerName, item, resourceActor);
  }
}

trait ContainerResourceActor extends Actor {
  
  val store:Store

  def containerMessageHandler: PartialFunction[Any, Unit] = {
    case GetContainer(named:String) => {
      println("GET CONTAINER " + named);

      val oc = store.getContainer(named);
      if (!oc.isDefined) 
        throw new NotFoundException("Container not found");

      reply(oc.get.toXML)
    }
    case SearchContainer(named:String, search:String) => {
      println("SEARCH CONTAINER " + named + ", search = " + search);

      val oc = store.getContainer(named);
      if (!oc.isDefined) 
        throw new NotFoundException("Container not found");

      var c = oc.get.copy();
      val searchBytes:Array[Byte] = search.getBytes();
      c.items = c.items.filter(i =>
        matchBytes(searchBytes, named, i.name)
      )
      reply(c.toXML)
    }
    case PutContainer(named:String, uriInfo:UriInfo) => {
      println("PUT CONTAINER " + named);
        
      val uri =  uriInfo.getAbsolutePath();
      val c = new Container(named, uri.toString());

      println("FYNEH FIX THIS!!!")
      if (!store.hasContainer(c)) {
        store.createContainer(c);
        reply(created(uri).build);
      } else {
        reply(noContent().build);
      }
    }
    case DeleteContainer(named:String) => {
      println("DELETE CONTAINER " + named);
        
      val c = store.deleteContainer(named);
      if (!c.isDefined)
        throw new NotFoundException("Container not found");
    }
  }

  private def matchBytes(search:Array[Byte], containerName:String, item:String):Boolean = {
    val bo = store.getItemData(containerName, item);

    bo match {
      case None => return false;
      case Some(b) => return b.containsSlice(search)
    }
  }

}
