package handling.tests

import syntax.Flight1
import syntax.Empty
import syntax.Filled
import syntax.DateTime
import syntax.Date
import syntax.Time
import syntax.SeatNumberInstances_data
import syntax.Euro
import handling.Handler
import syntax.ChangeFlightSeatInstancesTo
import syntax.SeatTypeInstances_data


object ChangeFlightSeatInstancesTest {
  def main(args: Array[String]) : Unit = {
		succes();
		fail();
		nothing();
	}

	def succes() {
//	  val flight = Flight1(Empty(),DateTime(Date(1,1,2013),Filled(Time(Filled(13),Filled(0),Filled(0)))),Empty(),Empty(),Empty());
//	  val list = List(SeatNumberInstances_data(2,Filled(3),Euro(Filled(100),Empty())));
//	  Handler.handle(ChangeFlightSeatInstancesTo(flight,list));
	  val flight2 = Flight1(Empty(),DateTime(Date(1,1,2013),Filled(Time(Filled(13),Filled(0),Filled(0)))),Empty(),Empty(),Empty());
	  val list2 = List(SeatTypeInstances_data("Business",Euro(Filled(200),Empty())));
	  Handler.handle(ChangeFlightSeatInstancesTo(flight2,list2));
	}
	
	def fail() {
	  
	}
	
	def nothing() {
	  
	}
}