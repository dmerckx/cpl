package handling.tests;

import handling._
import handling.exceptions._

import syntax.FlightTime_data
import syntax._
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

object FlightTimeTest {
	def main(args: Array[String]) : Unit = {
		succes();
		fail();
		nothing();
	}

	def succes() {
		Handler.handle(AddFlightTime(FlightTime_data(Airport(Empty(),Empty(),Filled("LON")),Airport(Empty(),Empty(),Filled("BER")),AirplaneType(Filled("Jumbo")),Time(Filled(01),Filled(12),Empty()))));
	  
	}
	
	def fail() {
	  
	}
	
	def nothing() {
	  
	}
  
}