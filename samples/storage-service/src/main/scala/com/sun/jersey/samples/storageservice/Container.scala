package com.sun.jersey.samples.storageservice

case class Container(name:String, uri:String, var items:List[Item]) {

    def this(name:String, uri:String) = this(name, uri, List[Item]())
    
    def getItem(named:String):Option[Item] = {
      items.find(i => i.name.equals(named))
    }
    
    def putItem(item:Item):Unit = {
      if (items.exists(possibleItem => possibleItem.name.equals(item.name))) {
        items = items map (existingItem => {
            if (existingItem.name.equals(item.name)) item
            else existingItem
          }
        )
      } else {
        items ::= item
      }
    }
    
    def removeItem(named:String):Option[Item] = {
      val foundItem = items.find(i => i.name.equals(named))
      if (foundItem.isDefined) {
        items = items.filterNot(i => i == foundItem)
      }
      return foundItem
    }

    def toXML = {
      <container>
        { for(item <- items) yield {
            item.toXML
          }
        }
      </container>
    }
}
