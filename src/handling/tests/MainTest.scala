package handling.tests

import handling._
import syntax._

import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import Q.interpolation

object MainTest {

  def main(args: Array[String]) {
    var date1 = Date(1,1,2013);
    var date2 = Date(1,1,2014);
    var time1 = Time(Filled(16),Empty(),Empty());
    var time2 = Time(Filled(16),Filled(1),Empty());
    var time3 = Time(Empty(),Empty(),Empty());
    var datetime1 = DateTime(date1,Filled(time1));
    var datetime2 = DateTime(date1,Filled(time2));
    var d1 = DateTime(Date(20,3,2014),Empty());
    var d2 = DateTime(Date(20,3,2015),Filled(Time(Filled(0),Empty(),Empty())));
    println(d1.compare(d2));
    println(d2.compare(d2));
     println(d1.compare(d1));
     println(d2.compare(d1));
//    Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
//				driver = "com.mysql.jdbc.Driver") withSession {
////			Handler.getAirportIds(Airport(Filled(City(Filled("Valencia"))),Filled("Val2"),Filled("DCF"))).foreach(r => println(r));
////			println("--------");
////			Handler.getAirportIds(Airport(Empty(),Empty(),Empty())).foreach(r => println(r));
//			
//			Handler.getAirplaneTypeIds(AirplaneType(Filled("1"))).foreach(r => println(r));
//			
//			println("based on airline");
//			println("--------------------");
//			Handler.getTemplateIds(Template(Filled(Airline(Empty(),Filled("SABENA"))),Empty(),Empty(),Empty(),Empty())).foreach(template => println(template));
//			println();
//			
//			println("based on fln");
//			println("--------------------");
//			Handler.getTemplateIds(Template(Empty(),Filled("SAB1234"),Empty(),Empty(),Empty())).foreach(template => println(template));
//			println();
//			
//			println("based on airportfrom");
//			println("--------------------");
//			Handler.getTemplateIds(Template(Empty(),Empty(),Filled(Airport(Empty(),Filled("BrusselsAirport"),Empty())),Empty(),Empty())).foreach(template => println(template));
//			println();
//			
//			println("based on airportto");
//			println("--------------------");
//			Handler.getTemplateIds(Template(Empty(),Empty(),Empty(),Filled(Airport(Empty(),Filled("NewYorkAirport"),Empty())),Empty())).foreach(template => println(template));
//			println();
//			
//			println("based on airplanetype");
//			println("--------------------");
//			Handler.getTemplateIds(Template(Empty(),Empty(),Empty(),Empty(),Filled(AirplaneType(Filled("Boeing777"))))).foreach(template => println(template));
//			
//		}
	  
  }
  
}