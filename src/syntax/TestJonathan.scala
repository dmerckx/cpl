package syntax

import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}

object TestJonathan {
  case class Count(nr: Int);
  
  def insert(c: City_data) = (Q.u + "INSERT INTO city(`name`) VALUES (" +? c.name + ")").execute;
  
  implicit val getCountResult = GetResult(r => Count(r.nextInt));
  def count(query : String) : Int = {
    val q = Q.queryNA[Count](query);
    try {
    	return q.first.nr;
    } catch {
    	case _: NoSuchElementException => return 0;
    }
  }
  
  def realCount(fromWhere : String) : Int = {
    val q = Q.queryNA[Count]("SELECT count(*) " + fromWhere);
    return q.first.nr;
  }
  
  def main(args: Array[String]): Unit = {
	Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
                  driver = "com.mysql.jdbc.Driver") withSession {
	  
	  // Deletion (you can also do this using the three methods for insertion)
	  // Delete all entries from the city table
  	  // Note the use of .first instead of .execute to obtain the number of affected rows
	  val nDelete = (Q.u + "DELETE FROM city").first
	  println(nDelete + " rows deleted from city.");
	  
	  // Insertion method 1
	  // Note the use of .first instead of .execute to obtain the number of affected rows
	  val nInsert =  Q.updateNA("INSERT INTO city(`name`) VALUES('Leuven - 1')").first
	  println(nInsert + " rows inserted into city.");
	  // Insertion method 2 - IDENTICAL to insertion method 1
	  // Use execute if you don't care about the number of affected rows.
	  (Q.u + ("INSERT INTO city(`name`) VALUES ('Leuven - 2')")).execute;
	  // Insertion method 3 - requires definition of own insertion method, quite clean
	  // I'd recommend this method.
	  insert(new City_data("Leuven - 3"));
	  
	  // Now to querying...
	  val q2 = Q.queryNA[Count]("""
	      SELECT count(*) FROM city""");
	  val c : Count = q2.first;
	  println("There are currently " + c.nr + " cities in the database.")
	  // Method 2 - using the helper method I made
	  // I'd recomend this method.
	  val c2 : Int = count("SELECT count(*) FROM city");
	  println("There are currently " + c2 + " cities in the database.")
	}
  }
}