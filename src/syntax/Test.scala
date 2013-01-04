package syntax

import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}

object Test {

  def main(args: Array[String]): Unit = {
	Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
                  driver = "com.mysql.jdbc.Driver") withSession {
	  val q2 = Q.query[Double, (String, String)]("""
	      SELECT 
	      """);
	}
  }
}