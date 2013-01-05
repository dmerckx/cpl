package handling.tests

import handling._
import handling.exceptions._

import syntax._

import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

case class FailedTestError(arg:String) extends Exception

object AirportTest {

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

	def fail() {
		try {
			var name = "Madrid";
			Handler.handle(AddCity(City_data(name)));
			Handler.handle(AddCity(City_data(name)));
		} catch {
		case e:AlreadyExistingCityNameException => println("ok");
		case _ => throw new FailedTestError("non unique city name was added to the database");
		}
	}

	def nothing() {
		var name = "Madrid";
		var toName = "Barcelona";
		Handler.removeCity(City(Filled(name)));
		Handler.removeCity(City(Filled(toName)));
		Handler.handle(AddCity(City_data(name)));
		Handler.handle(ChangeCity(City(Empty()),City(Filled(toName))));
		if(contains(toName))
			throw new FailedTestError("invalid change!");
		Handler.handle(ChangeCity(City(Filled(name)),City(Empty())));
		if(contains(toName))
			throw new FailedTestError("invalid change!");
		if(!contains(name))
			throw new FailedTestError("invalid change!");
	}

	def contains(name:String) : Boolean = {
			var result = 0;
			Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
					driver = "com.mysql.jdbc.Driver") withSession {
				result = count("select count(*) from airport where name='"+ name+ "'");
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