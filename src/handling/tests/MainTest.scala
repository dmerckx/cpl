package handling.tests

import handling._
import syntax._

import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import Q.interpolation

object MainTest {

  def main(args: Array[String]) {
    Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
				driver = "com.mysql.jdbc.Driver") withSession {
//			Handler.getAirportIds(Airport(Filled(City(Filled("Valencia"))),Filled("Val2"),Filled("DCF"))).foreach(r => println(r));
//			println("--------");
//			Handler.getAirportIds(Airport(Empty(),Empty(),Empty())).foreach(r => println(r));
			
			Handler.getAirplaneTypeIds(AirplaneType(Filled("1"))).foreach(r => println(r));
			
			println("based on airline");
			println("--------------------");
			Handler.getTemplateIds(Template(Filled(Airline(Empty(),Filled("SABENA"))),Empty(),Empty(),Empty(),Empty())).foreach(template => println(template));
			println();
			
			println("based on fln");
			println("--------------------");
			Handler.getTemplateIds(Template(Empty(),Filled("SAB1234"),Empty(),Empty(),Empty())).foreach(template => println(template));
			println();
			
			println("based on airportfrom");
			println("--------------------");
			Handler.getTemplateIds(Template(Empty(),Empty(),Filled(Airport(Empty(),Filled("BrusselsAirport"),Empty())),Empty(),Empty())).foreach(template => println(template));
			println();
			
			println("based on airportto");
			println("--------------------");
			Handler.getTemplateIds(Template(Empty(),Empty(),Empty(),Filled(Airport(Empty(),Filled("NewYorkAirport"),Empty())),Empty())).foreach(template => println(template));
			println();
			
			println("based on airplanetype");
			println("--------------------");
			Handler.getTemplateIds(Template(Empty(),Empty(),Empty(),Empty(),Filled(AirplaneType(Filled("Boeing777"))))).foreach(template => println(template));
			
		}
	  
  }
  
}