package handling;

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import syntax.Operation
import syntax._;
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation


case class FailedTestError(arg:String) extends Exception

object Tests  {

	def main(args: Array[String]) {
		parseSucces();
	}

	def parseSucces() {
	  Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
					driver = "com.mysql.jdbc.Driver") withSession {
			}
		Handler.handle(AddCity(City_data("Malaga", "MAL")));
		if(!contains())
			throw new FailedTestError("city was not in the database");
	}

	def contains() : Boolean = {
			Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
					driver = "com.mysql.jdbc.Driver") withSession {
				var result = (Q.u + "select * from city where name=Malaga").execute
						println(result);
				return result.asInstanceOf[Int] >= 1;
			}
			return false;
	}

}
