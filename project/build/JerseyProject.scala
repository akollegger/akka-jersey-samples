import sbt._

class JerseyProject(info: ProjectInfo) extends DefaultProject(info) {

  // library versions
  val AKKA_VERSION = "0.8.1"
  val AKKA_ART_EXT = "_2.8.0.Beta1"

  // ------------------------------------------------------------
  // repositories
  val java_net = "java.net" at "http://download.java.net/maven/2"
  val scala_tools_snapshots = "scala-tools snapshots" at "http://scala-tools.org/repo-snapshots"
  val scala_tools_releases = "scala-tools releases" at "http://scala-tools.org/repo-releases"
  val akka_repository = "Akka Maven Repository" at "http://scalablesolutions.se/akka/repository"


  // ------------------------------------------------------------
  // project defintions
  lazy val helloworld = project("samples" / "helloworld", "Hello World", new HelloWorldProject(_))
  lazy val simple_console = project("samples" / "simple-console", "Simple Console", new SimpleConsoleProject(_))
  lazy val storage_service = project("samples" / "storage-service", "Storage Service", new StorageServiceProject(_))

  lazy val jersey_app = project("jersey-app", "jersey-app", new AkkaAppProject(_),
    helloworld,
    simple_console,
    storage_service
  )
   
  class AkkaProject(info: ProjectInfo) extends DefaultProject(info) {
    val akka_kernel = "se.scalablesolutions.akka" % ("akka-kernel"+AKKA_ART_EXT) % AKKA_VERSION % "compile"
    val akka_core = "se.scalablesolutions.akka" % ("akka-core"+AKKA_ART_EXT) % AKKA_VERSION % "compile"
    val akka_servlet = "se.scalablesolutions.akka" % ("akka-servlet"+AKKA_ART_EXT) % AKKA_VERSION % "compile"
    val akka_rest = "se.scalablesolutions.akka" % ("akka-rest"+AKKA_ART_EXT) % AKKA_VERSION % "compile"

    val scalaz_core = "com.googlecode.scalaz" % "scalaz-core_2.8.0.Beta1" % "5.0-SNAPSHOT"
    val commons_io = "commons-io" % "commons-io" % "1.4"
    val commons_codec = "commons-codec" % "commons-codec" % "1.4"
    val commons_fileupload = "commons-fileupload" % "commons-fileupload" % "1.2.1"

    val jetty_server = "org.eclipse.jetty"  % "jetty-server"   % "7.0.1.v20091125" % "test"
    val jetty_webapp = "org.eclipse.jetty"  % "jetty-webapp"   % "7.0.1.v20091125" % "test"
  

    override def compileOptions = super.compileOptions ++ Seq(Unchecked)
  }

  class AkkaAppProject(info: ProjectInfo) extends AkkaProject(info) 

  class HelloWorldProject(info: ProjectInfo) extends AkkaProject(info)
  class SimpleConsoleProject(info: ProjectInfo) extends AkkaProject(info)
  class StorageServiceProject(info: ProjectInfo) extends AkkaProject(info)

  override def mainClass = Some("se.scalablesolutions.akka.kernel.Main")

}
