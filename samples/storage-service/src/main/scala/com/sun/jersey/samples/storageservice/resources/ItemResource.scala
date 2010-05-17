package com.sun.jersey.samples.storageservice.resources

import com.sun.jersey.api.NotFoundException
import com.sun.jersey.samples.storageservice.{Container,Item,Store}
import java.math.BigInteger
import java.net.URI
import java.security.MessageDigest
import java.util.{Date,GregorianCalendar}
import java.util.Calendar._
import javax.ws.rs.{DELETE,GET,PUT}
import javax.ws.rs.core.{EntityTag,Context,HttpHeaders,MediaType,Request,Response,UriInfo}
import javax.ws.rs.core.Response._

import se.scalablesolutions.akka.actor.Actor

import scalaz._
import Scalaz._

case class GetItem(named:String, fromContainerNamed:String, asRequestedBy:Request)
case class PutItem(named:String, withContent:Array[Byte], andHeaders:HttpHeaders, 
  intoContainerNamed:String, atUriInfo:UriInfo)
case class DeleteItem(named:String, fromContainerNamed:String)

class ItemResource(uriInfo:UriInfo,request:Request,
  containerName:String,itemName:String,
  resourceActor:Actor) {

  @GET
  def getItem():Response = 
    (resourceActor !! GetItem(itemName, containerName, request)).getOrElse(noContent().build)
    
  @PUT
  def putItem(@Context headers:HttpHeaders, data:Array[Byte]):Response = 
    (resourceActor !! PutItem(itemName, data, headers, containerName, uriInfo)).getOrElse(noContent().build)

   
  @DELETE
  def deleteItem():Unit = 
    (resourceActor !! DeleteItem(itemName, containerName)).getOrElse(noContent().build)
  
}

trait ItemResourceActor extends Actor {

  val store:Store

  def itemMessageHandler: PartialFunction[Any, Unit] = {
    case GetItem(named:String, fromContainerNamed:String, asRequestedBy:Request) => {
      println("GET ITEM " + named + " from " + fromContainerNamed)
        
      val oi = store.getItem(fromContainerNamed, named);
      if (!oi.isDefined)
        throw new NotFoundException("Item not found");
      val i:Item = oi.get
      val lastModified = i.lastModified.getTime();
      val et = new EntityTag(i.digest);
      val rb = asRequestedBy.evaluatePreconditions(lastModified, et);
      if (rb != null)
        reply(rb.build);
            
      val ob = store.getItemData(fromContainerNamed, named);
      ob match {
        case Some(b:Array[Byte]) => {
          reply(ok(b, i.mimeType).
            lastModified(lastModified).tag(et).build
          )
        }
        case _ => reply(noContent().build)
      }
    }
    case PutItem(named:String, withData:Array[Byte], andHeaders:HttpHeaders, inContainerNamed:String, atUriInfo:UriInfo) =>
    {
      println("PUT ITEM " + named + " into " + inContainerNamed)
        
      val uri = atUriInfo.getAbsolutePath();
      val mimeType:String = andHeaders.getMediaType().toString();
      val gc = new GregorianCalendar();
      gc.set(MILLISECOND, 0);
      val i = new Item(named, uri.toString(), mimeType, gc);
      val digest = computeDigest(withData);
      i.digest = digest;
        
      var r:ResponseBuilder = null;
      if (!store.hasItem(inContainerNamed, named)) {
        r = created(uri)
      } else {
        r = noContent()
      }
        
      val ii = store.createOrUpdateItem(inContainerNamed, i, withData);
      if (!ii.isDefined) {
        // Create the container if one has not been created
        val containerUri = atUriInfo.getAbsolutePathBuilder().path("..").
          build().normalize();
        val c = new Container(inContainerNamed, containerUri.toString());
        store.createContainer(c);
        if (!store.createOrUpdateItem(inContainerNamed, i, withData).isDefined)
          throw new NotFoundException("Container not found");
      }
        
      reply(r.build)
    }    
    case DeleteItem(named:String, fromContainerNamed:String) =>
    {
        println("DELETE ITEM " + named + " from " + fromContainerNamed)
        
        val i = store.deleteItem(fromContainerNamed, named);
        if (!i.isDefined) {
            throw new NotFoundException("Item not found");
        }
    }
 
  }
 
    
    private def computeDigest(content:Array[Byte]):String = {
        try {
            val md = MessageDigest.getInstance("SHA");
            val digest = md.digest(content);
            val bi = new BigInteger(digest);
            return bi.toString(16);
        } catch {
          case (e:Exception) => return ""
        }
    }
}

