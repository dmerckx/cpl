package handling.tests

import handling._
import handling.exceptions._

import syntax._

import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

//TODO fix tests
object AirplaneTypeTest {

	def main(args: Array[String]) : Unit = {
			succes();
	}

	def succes() {
	  Handler.handle(AddSeatType("Business"));
	  Handler.handle(AddAirplaneType(AirplaneType_data("Type"), List(Seat_data(Filled(8),Filled(10),"Business"))));
		
	}

	def contains(name:String) : Boolean = {
			var result = 0;
			Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
					driver = "com.mysql.jdbc.Driver") withSession {
				result = count("select count(*) from airplanetype where name='"+ name+ "'");
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