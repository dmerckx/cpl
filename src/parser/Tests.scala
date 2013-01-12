//package parser;
//
//import org.scalatest.junit.JUnitRunner
//import org.junit.runner.RunWith
//import org.scalatest.matchers.ShouldMatchers
//import org.scalatest.FunSuite
//import syntax.Operation
//import syntax.Type
//import syntax._
//
//@RunWith(classOf[JUnitRunner])
//class Tests extends FunSuite with ShouldMatchers {
//
//    def parse(expr:String){
//      test("try parse expression: " + expr) {
//            val e:Operation = Parser.parse(expr, false) match{
//        	  case PSucces(o) => o
//        	  case PFail(msg) =>
//        	    fail("'parse failed: " + msg + "'")
//        	}
//        }
//    }
//  
//    def parseAndPrint(expr:String) {
//    	test("try parse expression: " + expr) {
//            val e:Operation = Parser.parse(expr, true) match{
//        	  case PSucces(o) => o
//        	  case PFail(msg) =>
//        	    fail("'parse failed: " + msg + "'")
//        	}
//        }
//    }
//	
//    def parseSucces(expr:String, outp: => Operation) {
//    	test("parse expression: " + expr + " <--> " + outp) {
//        	val out = outp
//            val e:Operation = Parser.parse(expr) match{
//        	  case PSucces(o) => o
//        	  case PFail(msg) =>
//        	    fail("'parse failed: " + msg + "'")
//        	}
//        	
//        	if(!e.toString().equals(outp.toString()))
//        	  fail("expressions not equal");
//        }
//    }
//    
//    def parseSuccesAndPrint(expr:String, outp: => Operation) {
//    	test("parse expression: " + expr + " <--> " + outp) {
//        	val out = outp
//            val e:Operation = Parser.parse(expr, true) match{
//        	  case PSucces(o) => o
//        	  case PFail(msg) =>
//        	    fail("'parse failed: " + msg + "'")
//        	}
//        	println(e.toString());
//        	println(outp.toString());
//        	
//        	if(!e.toString().equals(outp.toString()))
//        	  fail("expressions not equal");
//        }
//    }
//    
//    def parseTypeSucces(expr:String, typeName:String, outp: => Type) {
//    	test("parse type expression: " + expr + " <--> " + outp) {
//        	val out = outp
//            val t:Type = Parser.parseType(expr, typeName)
//        	println(t.toString());
//        	println(outp.toString());
//        	
//        	if(!t.toString().equals(outp.toString()))
//        	  fail("expressions not equal");
//        }
//    }
//    
//    def parseFail(expr:String, outp: => Operation) {
//    	test("fail parse expression: " + expr + " <--> " + outp) {
//        	val out = outp
//            val e:Operation = Parser.parse(expr) match{
//        	  case PSucces(o) => o
//        	  case PFail(msg) =>
//        	    fail("'parse failed: " + msg + "'")
//        	}
//        	println(e.toString());
//        	println(outp.toString());
//        	
//        	if(e.toString().equals(outp.toString()))
//        	  fail("expressions are equal, but they should not be");
//        }
//    }
//   
//   //-----Different operations-----
//   //-----CITIES-----
//   parseSucces("ADD CITY {name:Brussels}",
//		   AddCity(City_data("Brussels")))
//   parseSucces("CHANGE CITY {name:Bru} TO {name:Brussel}",
//		   ChangeCity(City(Filled("Bru")),City(Filled("Brussel")))) 
//   parseSucces("REMOVE CITY { }",
//		   RemoveCity(City(Empty()))) 
//    			
//   //-----Airports-----
//   parseSucces("ADD AIRPORT {city:{name:Brussels}, name:\"Brussels airport\", short:BRU}",
//		   AddAirport(Airport_data(City(Filled("Brussels")), "Brussels airport", "BRU")))
//   parseSucces("CHANGE AIRPORT {short:BRU} TO {city:{name:Zaventem}}",
//		   ChangeAirport(
//				   Airport(Empty(), Empty(), Filled("BRU")),
//				   Airport(Filled(City(Filled("Zaventem"))), Empty(), Empty())))
//   parseSucces("REMOVE AIRPORT {name:\"Brussels airport\"}",
//		   RemoveAirport(Airport(Empty(), Filled("Brussels airport"), Empty())))
//   
//   //-----Distance-----
//   parseSucces("ADD DISTANCE {from:{short:BRU}, to:{city:{name:Amsterdam}}, dist:1000}",
//		   AddDist(Dist_data(
//		       Airport(Empty(), Empty(), Filled("BRU")),
//		       Airport(Filled(City(Filled("Amsterdam"))), Empty(), Empty()),
//		       1000)))
//   parseSucces("CHANGE DISTANCE {from:{short:BRU}, to:{short:AMS}} TO {dist:1000}",
//		   ChangeDistTo(
//		       Dist(
//			       Filled(Airport(Empty(), Empty(), Filled("BRU"))),
//			       Filled(Airport(Empty(), Empty(), Filled("AMS"))),
//			       Empty()),
//			   Dist(
//			       Empty(),
//			       Empty(),
//			       Filled(1000))))
//   parseSucces("REMOVE DISTANCE {from:{short:BRU}}",
//		   RemoveDist(
//		       Dist(
//			       Filled(Airport(Empty(), Empty(), Filled("BRU"))),
//			       Empty(),
//			       Empty())))
//  
//			       
//	parseSucces("ADD AIRLINE {"+
//	"	short: BM,"+
//	"	name: \"British Midlands Airways\""+
//	"}"	,
//		AddAirline(
//		    Airline_data(
//		        "British Midlands Airways",
//		    	"BM"
//		    )
//		    ))
//   
//    parseSucces("ADD AIRPLANE TYPE {"+
//	"	name: \"Boeing 727\""+
//	"} WITH SEATS ["+
//	"	{type: business, amt: 24},"+
//	"	{type: economy, amt: 123}"+
//	"]",
//		AddAirplaneType(
//		    AirplaneType_data("Boeing 727"),
//		    List(
//		        Seat_data(Empty(), Filled(24), "business"),
//		        Seat_data(Empty(), Filled(123), "economy")
//		        )
//		    ))
//		
//	
//    parse("ADD FLIGHT {"+
//	"   departure: {date: {d:15, m:04, y:1990}, time: {h:15,m:20}}," +
//	"   arrival: {date: {d:16, m:04, y:1990}, time: {h:4,m:20}}," +
//	"   type: {name: \"Boeing 727\"}, "+
//	"	template: {fln : BM1628} "+
//	"}")
//	
//	 parse("CHANGE FLIGHT{"+
//	"      departure: {date: {d:15, m:04, y:1990}, time: {h:15,m:20}}"+
//	"} TO {" +
//	"      departure: {date: {d:15, m:04, y:1990}, time: {h:15,m:58}}"+
//	"}")
//	
//	parse("CHANGE TEMPLATE{"+
//	"      fln : BM1628"+
//	"} TO {" +
//	"      from: {name: Brussels}"+
//	"}")
//	
//   //-----Uit de opgave----
//			       
//   parseSucces("ADD FLIGHT TIME{" +
//	"      from: {name: \"Brussels airport\"},"+
//	"      to: {name: Edinburgh},"+
//	"      type: {name: \"Boeing 727\"},"+
//	"      time: {h: 1, m: 22}"+
//	" }",
//		AddFlightTime(
//		    FlightTime_data(
//		       Airport(Empty(), Filled("Brussels airport"), Empty()),
//		       Airport(Empty(), Filled("Edinburgh"), Empty()),
//		       AirplaneType(Filled("Boeing 727")),
//		       Time(Filled(1), Filled(22), Empty())
//		    )
//		))
//		
//    parseSucces("ADD DISTANCE{" +
//	"      from: {name: \"Brussels airport\"},"+
//	"      to: {name: Edinburgh},"+
//	"      dist: 1000"+
//	" }",
//		AddDist(
//		    Dist_data(
//		       Airport(Empty(), Filled("Brussels airport"), Empty()),
//		       Airport(Empty(), Filled("Edinburgh"), Empty()),
//		       1000
//		    )
//		))
//			       
//   parseSucces("ADD TEMPLATE {" +
//   	"	  fln: BM1628,"+
//	"      from: {name: \"Brussels airport\"},"+
//	"      to: {name: Edinburgh},"+
//	"      type: {name: \"Boeing 727\"}"+
//	"  } WITH SEAT INSTANCES ["+
//	"      {type: economy, price: {euro: 210}},"+
//	"      {type: business, price: {euro: 340}}"+
//	"  ] AND WITH PERIODS ["+
//	"      {weekday: monday, 	departure:{h:09, m:55}},"+
//	"      {weekday: wednesday, departure:{h:09, m:55}},"+
//	"      {weekday: friday, 	departure:{h:09, m:55}}"+
//	"  ]",
//		AddTemplate(
//		   Template_data(
//		       "BM1628",
//		       Airport(Empty(), Filled("Brussels airport"), Empty()),
//		       Airport(Empty(), Filled("Edinburgh"), Empty()),
//		       AirplaneType(Filled("Boeing 727"))
//		       ),
//		   List(
//		       SeatTypeInstances_data("economy", Euro(Filled(210), Empty())),
//		       SeatTypeInstances_data("business", Euro(Filled(340), Empty()))
//		   ),
//		   List(
//		       Period_data(Empty(), Empty(), Filled("monday"), Time(Filled(9),Filled(55),Empty())),
//		       Period_data(Empty(), Empty(), Filled("wednesday"), Time(Filled(9),Filled(55),Empty())),
//		       Period_data(Empty(), Empty(), Filled("friday"), Time(Filled(9),Filled(55),Empty()))
//		   )
//		))
//		
//	/*	+
//	"  CHANGE SEAT INSTANCES OF FLIGHT {"+
//	"      template: {fln: SAB888},"+
//	"      during: {"+
//	"          from: { d: 15, m:  6, y: 2012 },"+
//	"          to: { d: 9, m: 12, y: 2016 }"+
//	"     }"+
//	"  } TO ["+
//	"   {type: business, price: {dollar: 268}},"+
//	"    {type: economy, price: {dollar: 370}}"+
//	"  ]"*/
//		   
//   //-----Different types------
//   
////   parseTypeSucces("{name:\"New York\"}", "city",
////		   		City(Filled("New York"), Empty()))
////   parseTypeSucces("{short:\"NY\"}", "city",
////		   		City(Empty(), Filled("NY")))
////   parseTypeSucces("{name:\"New York\", short:\"NY\"}", "cityData",
////		   		City_data("New York", "NY"))
////   parseTypeSucces("{name:\"Brussels airport\", city:{name:Brussels}, short:BRU}", "airport",
////		   		Airport(Filled("Brussels airport"), Filled(City(Filled("Brussels"), Empty())), Filled("BRU")))
////   parseTypeSucces("{name:\"Brussels airport\", city:{name:Brussels}, short:BRU}", "airportData",
////		   		Airport_data(City(Filled("Brussels"), Empty()), "Brussels airport", "BRU"))
////   parseTypeSucces("{price:100, seats:[1, 2]}", "prices",
////		   		Prices1(100, List(1, 2)))
//    			
//}
