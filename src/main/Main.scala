package main

import handling.Handler

object Main {
	def main(args: Array[String]): Unit = {
		if (args.length != 1 && args.length != 4) {
			println("Usage: java -jar airyscript.jar <path-to-filename> <db-host> <db-username> <db-password>\n\t or java -jar airyscript.jar <path-to-filename>")
		} else {
			println("Welcome to AiryScript v1.0");
			println("-------------------------");
			val fileName = args(0);
			if (args.length == 5) {
				Handler.databaseLocation = args(2);
				//Handler.databaseName 	 = args(3);
				Handler.databaseUsername = args(3);
				Handler.databasePassword = args(4);
			}
			val source = scala.io.Source.fromFile(fileName)
			val lines = source.mkString
			source.close ()
			val messages = Interpreter.interpret(lines);
			messages.foreach(s => s match {
  				case (false,message) => System.out.println(message);
  				case (true,message)  => System.err.println(message);
			});
		}
	}
}