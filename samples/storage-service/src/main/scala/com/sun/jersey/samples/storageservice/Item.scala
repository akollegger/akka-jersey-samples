package com.sun.jersey.samples.storageservice

import java.util.{Calendar, GregorianCalendar}
import java.text.SimpleDateFormat

case class Item(name:String, uri:String, mimeType:String, 
  lastModified:GregorianCalendar, var digest:String) {
  def this(name:String, uri:String) = this(name, uri, null, null, null)
  def this(name:String, uri:String, mimeType:String, lastModified:GregorianCalendar) = this(name, uri, mimeType, lastModified, null)

  implicit def cal2wrapper(calToWrap:GregorianCalendar) = new CalendarWrapper(calToWrap)

  def toXML = {
    <item>
      <digest>{digest}</digest>
      <lastModified>{lastModified.toIso8601}</lastModified>
      <mimeType>{mimeType}</mimeType>
      <name>{name}</name>
      <uri>{uri}</uri>
    </item>
  }


}

class CalendarWrapper(val wrappedCalendar:GregorianCalendar) {

  private val iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz")

  def toIso8601 = iso8601Format.format(wrappedCalendar.getTime)

}

