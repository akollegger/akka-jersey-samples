package com.sun.jersey.samples.storageservice

import scala.collection.mutable

class MemoryStore extends Store {
    
    
    private val containerMap:mutable.Map[String, Container] = new mutable.HashMap[String, Container]();
    
    private val dataMap:mutable.Map[String, mutable.Map[String, Array[Byte]]] = new mutable.HashMap[String, mutable.Map[String, Array[Byte]]]();
    
    def getContainers():List[Container] = {
        val l = mutable.ListBuffer[Container]()
        l.appendAll(containerMap.valuesIterator)
        
        l.toList
    }

    def getContainer(named:String):Option[Container] = {
        containerMap.get(named)
    }

    def hasContainer(container:Container):Boolean = {
        containerMap.contains(container.name) 
    }
    
    def createContainer(container:Container):Option[Container] = {
        val c = containerMap.get(container.name);
        if (c.isDefined) return None;
        
        containerMap += container.name -> container
        
        dataMap += container.name -> new mutable.HashMap[String, Array[Byte]]();

        c
    }

    def deleteContainer(named:String):Option[Container] = {
        dataMap.remove(named)

        containerMap.remove(named)
    }

    def hasItem(inContainerNamed:String, itemNamed:String):Boolean = {
        val c = containerMap.get(inContainerNamed)
        if (!c.isDefined) return false
        
        c.get.getItem(itemNamed).isDefined
    }
    
    def getItem(fromContainerNamed:String, itemNamed:String):Option[Item] = {
        val c = containerMap.get(fromContainerNamed)
        if (!c.isDefined) return None
        
        c.get.getItem(itemNamed)
    }
    
    def getItemData(fromContainerNamed:String, fromItemNamed:String):Option[Array[Byte]] = {
        val c = containerMap.get(fromContainerNamed)
        if (!c.isDefined) return None
        
        val data = dataMap.get(fromContainerNamed)

        data match {
          case None => return None
          case Some(data:mutable.Map[String, Array[Byte]]) => return data.get(fromItemNamed)
          case _ => return None
        }
    }

    def createOrUpdateItem(inContainerNamed:String, item:Item, 
      withContents:Array[Byte]):Option[Item] = {
        val c = containerMap.get(inContainerNamed)
        if (!c.isDefined) return None
    
        c.get.putItem(item)
        
        val data = dataMap(inContainerNamed)

        data += item.name -> withContents
        
        Some(item)
    }

    def deleteItem(fromContainerNamed:String, itemNamed:String):Option[Item] = {
        val c = containerMap.get(fromContainerNamed)
        if (!c.isDefined) return None
        
        c.get.getItem(itemNamed) match {
          case None => None
          case Some(i:Item) => {
            val data = dataMap(fromContainerNamed)
            data -= itemNamed
            Some(i)
          }
        }
    }
}
