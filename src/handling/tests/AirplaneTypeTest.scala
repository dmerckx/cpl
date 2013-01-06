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
		Handler.handle(RemoveCity(City(Filled("Valencia"))));
		Handler.handle(AddCity(City_data("Valencia")));
		if(contains("NewYorkAirport"))
			throw new FailedTestError("invalid change!");
		Handler.handle(AddAirport(Airport_data(City(Filled("Valencia")),"NewYorkAirport","JFK")));
		if(!contains("NewYorkAirport"))
			throw new FailedTestError("invalid change!");
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