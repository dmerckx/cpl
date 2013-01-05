package handling.tests;

import handling._
import handling.exceptions._

import syntax._
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

object CityTest  {

	def main(args: Array[String]) : Unit = {
			succes();
			fail();
			nothing();
	}

	def succes() {
		var name = "Madrid";
		Handler.removeCity(City(Filled(name)));
		if(contains(name))
			throw new FailedTestError("city not removed from the database");
		Handler.removeCity(City(Filled("Barcelona")));
		if(contains("Barcelona"))
			throw new FailedTestError("city not removed from the database");
		Handler.handle(AddCity(City_data(name)));
		if(!contains(name))
			throw new FailedTestError("city was not in the database");
		Handler.handle(ChangeCity(City(Filled(name)),City(Filled("Barcelona"))));
		if(contains(name))
			throw new FailedTestError("old city was not updated in the database");
		if(!contains("Barcelona"))
			throw new FailedTestError("new city was not in the database");
		Handler.removeCity(City(Filled("Barcelona")));
		if(contains("Barcelona"))
			throw new FailedTestError("new city was not in the database");
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
				result = count("select count(*) from city where name='"+ name+ "'");
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


