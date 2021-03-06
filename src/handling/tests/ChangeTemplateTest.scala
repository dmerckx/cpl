package handling.tests

import parser.Parser
import handling.Handler
import main.Main
import main.Interpreter

object ChangeTemplateTest {
  	def main(args: Array[String]) : Unit = {
  	  success();
  	}
  	
  	def success() = {
  		println("Starting...")
  		val input = """
ADD CITY {name: Brussels};
ADD CITY {name: Edinburgh};
ADD AIRPORT {
	short: ZAV,
	name: Zaventem,
	city: {name: Brussels}
};
ADD AIRPORT {
	short: EDI,
	name: "Edinburgh Airport",
	city: {name: Edinburgh}
};
ADD AIRLINE {
	short: BM,
	name: "British Midlands Airways"
};
ADD SEAT TYPE business;
ADD SEAT TYPE economy;
ADD AIRPLANE TYPE {
	name: "Boeing 727"
} WITH SEATS [
	{type: business, amt: 24},
	{type: economy, amt: 123}
];
ADD FLIGHT TIME {
	from: {short: ZAV},
	to: {short: EDI},
  	type: {name: "Boeing 727"},
  	time: {h: 1, m: 22}
};
ADD DISTANCE {
	from: {short: ZAV},
	to: {short: EDI},
	dist: 757
};
ADD TEMPLATE {
	fln: BM1628,
	from: {short: ZAV},
	to: {short: EDI},
	type: {name: "Boeing 727"}
} WITH SEAT INSTANCES [
	{type: business, price: {euro: 340}},
	{type: economy, price: {euro: 210}} 
] AND WITH PERIODS [
	{weekday: monday, departure: {h: 9, m: 55}},
	{weekday: wednesday, departure: {h: 9, m: 55}},
	{weekday: friday, departure: {h: 9, m: 55}}
]
  			""";
  		val messages = Interpreter.interpret(input);
  		messages.foreach(s => s match {
  			case (false,message) => System.out.println(message);
  			case (true,message) => System.err.println(message);
  		});
	//from: {short: ZAV},
	//to: {short: EDI},
  	  //Handler.handle(Parser.parse("ADD CITY {name = Leuven}"));
  	}
}