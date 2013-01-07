package handling.tests

import syntax._
import handling._
import handling.exceptions._

import syntax._
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

object FlightTest {

  def main(args: Array[String]) : Unit = {
			succes();
	}

	def succes() {
	  var airline1 = Airline(Filled("SAB"),Empty())
	  var airline2 = Airline(Filled("RYA"),Empty())
	  var template1 = Template(Empty(),Filled("SAB1234"),Empty(),Empty(),Empty());
	  var template2 = Template(Filled(airline1),Empty(),Empty(),Empty(),Empty());
	  var template3 = Template(Filled(airline2),Empty(),Empty(),Empty(),Empty());
	  var departure = DateTime(Date(1,1,2013),Empty());
	  var arrival = DateTime(Date(1,1,2013),Filled(Time(Filled(1),Filled(5),Filled(23))));
//	  Handler.handle(AddFlight(Flight_data(template1,departure,Empty(),Empty())));
//	  Handler.handle(AddFlight(Flight_data(template1,departure,Filled(arrival),Empty())));
//	  Handler.handle(AddFlight(Flight_data(template2,departure,Filled(arrival),Empty())));
	  Handler.handle(AddFlight(Flight_data(template3,departure,Empty(),Filled(AirplaneType(Filled("BOEING777"))))));
	}

	def contains(name:String) : Boolean = {
			var result = 0;
			Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
					driver = "com.mysql.jdbc.Driver") withSession {
				result = count("select count(*) from flight where name='"+ name+ "'");
				println(result);

			}
			return result >= 1;
	}

	implicit val getCountResult = GetResult(r => Count(r.nextInt));
	def count(query : String) : Int = {
			val q = Q.queryNA[Count](query);
			return q.first.nr;
	}
  
}