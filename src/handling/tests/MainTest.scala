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
		}
	  
  }
  
}