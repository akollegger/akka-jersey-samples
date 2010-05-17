package com.sun.jersey.samples.storageservice

trait Store {

    def hasContainer(container:Container):Boolean 
    
    def getContainers():List[Container]
    
    def getContainer(container:String):Option[Container]
    
    def createContainer(container:Container):Option[Container]
    
    def deleteContainer(container:String):Option[Container]

    def hasItem(inContainerNamed:String, itemNamed:String):Boolean 
        
    def getItem(container:String, item:String):Option[Item]

    def getItemData(container:String, item:String):Option[Array[Byte]]
    
    def createOrUpdateItem(container:String, item:Item, content:Array[Byte]):Option[Item]

    def deleteItem(container:String, item:String):Option[Item]
}
