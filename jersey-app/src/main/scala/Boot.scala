package example.akka.mongo.bookmarks.app

import se.scalablesolutions.akka.actor.SupervisorFactory
import se.scalablesolutions.akka.config.ScalaConfig._

import com.sun.jersey.samples.helloworld.resources.{HelloWorldResource,MarkupResource}
import com.sun.jersey.samples.console.resources.FormResource
import com.sun.jersey.samples.storageservice.resources.{
  ContainersResource, ContainerResourceActor, ItemResourceActor
}
import com.sun.jersey.samples.storageservice.MemoryStore

class Boot {
	
	val factory = SupervisorFactory(
		SupervisorConfig(
			RestartStrategy(OneForOne, 3, 100, List(classOf[Exception])),
			Supervise(new HelloWorldResource, LifeCycle(Permanent)) 
			:: Supervise(new MarkupResource, LifeCycle(Permanent)) 
			:: Supervise(new FormResource, LifeCycle(Permanent)) 
      :: Supervise(new ContainersResource with ContainerResourceActor with ItemResourceActor {
          val store = new MemoryStore
        }, LifeCycle(Permanent))
      :: Nil
    )
  )


	factory.newInstance.start
}
