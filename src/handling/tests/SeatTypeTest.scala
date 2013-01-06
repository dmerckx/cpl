package handling.tests

import handling._
import handling.exceptions._
import syntax._

import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

object SeatTypeTest {

  def main(args: Array[String]) : Unit = {
			succes();
			fail();
	}

	def succes() {
	  Handler.handle(AddSeatType("Business"));
	if(!contains("Business"))
	  throw new FailedTestError("non succesful add of seat type");
	}

	
	def fail() {
	  try {
			var name = "Madrid";
			Handler.handle(AddSeatType("Business"));
			Handler.handle(AddSeatType("Business"));
		} catch {
		case e:AlreadyExistingSeatTypeException =>
		case _ => throw new FailedTestError("non unique seat type was added to the database");
		}
	}
	def contains(name:String) : Boolean = {
			var result = 0;
			Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
					driver = "com.mysql.jdbc.Driver") withSession {
				result = count("select count(*) from seattype where name='"+ name+ "'");
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