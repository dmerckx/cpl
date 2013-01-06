package parser;

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import syntax.Operation
import syntax.Type
import syntax._

@RunWith(classOf[JUnitRunner])
class Tests extends FunSuite with ShouldMatchers {

	
    def parseSucces(expr:String, outp: => Operation) {
    	test("parse expression: " + expr + " <--> " + outp) {
        	val out = outp
            val e:Operation = Parser.parse(expr) match{
        	  case PSucces(o) => o
        	  case PFail(msg) =>
        	    fail("'parse failed: " + msg + "'")
        	}
        	println(e.toString());
        	println(outp.toString());
        	
        	if(!e.toString().equals(outp.toString()))
        	  fail("expressions not equal");
        }
    }
    
    def parseTypeSucces(expr:String, typeName:String, outp: => Type) {
    	test("parse type expression: " + expr + " <--> " + outp) {
        	val out = outp
            val t:Type = Parser.parseType(expr, typeName)
        	println(t.toString());
        	println(outp.toString());
        	
        	if(!t.toString().equals(outp.toString()))
        	  fail("expressions not equal");
        }
    }
    
    def parseFail(expr:String, outp: => Operation) {
    	test("fail parse expression: " + expr + " <--> " + outp) {
        	val out = outp
            val e:Operation = Parser.parse(expr) match{
        	  case PSucces(o) => o
        	  case PFail(msg) =>
        	    fail("'parse failed: " + msg + "'")
        	}
        	println(e.toString());
        	println(outp.toString());
        	
        	if(e.toString().equals(outp.toString()))
        	  fail("expressions are equal, but they should not be");
        }
    }
   
//   //-----Simple expression
//    //Simple tests
//    //Reverse order
//   parseSucces("ADD CITY {short:MAL, name:Malinas}",
//    			AddCity(City_data("Malinas", "MAL")))
//    //Parse with quotation marks
//   parseSucces("ADD CITY {short:\"MAL\", name:Malinas}",
//    			AddCity(City_data("Malinas", "MAL")))
//   parseSucces("ADD CITY {short:NY, name:\"New York\"}",
//    			AddCity(City_data("New York", "NY")))
//    			
//    			
//   //-----Expression with nested type & optional parameters
//    //Standard tests
//   parseSucces("ADD AIRPORT {name:Brussels_Airport, city:{name:brussels, short:bru}, short:BRU}",
//    			AddAirport(Airport_data(City(Filled("brussels"),Filled("bru")),"Brussels_Airport","BRU")))
//   parseSucces("ADD AIRPORT {name:Brussels_Airport, city:{short:bru}, short:BRU}",
//    			AddAirport(Airport_data(City(Empty(),Filled("bru")),"Brussels_Airport","BRU")))
//    //Optional parameters unfilled
//   parseSucces("ADD AIRPORT {name:Brussels_Airport, city:{name:brussels}, short:BRU}",
//    			AddAirport(Airport_data(City(Filled("brussels"),Empty()),"Brussels_Airport","BRU")))
//    //Reverse order
//   parseSucces("ADD AIRPORT {city:{short:bru}, short:BRU, name:Brussels_Airport}",
//    			AddAirport(Airport_data(City(Empty(),Filled("bru")),"Brussels_Airport","BRU")))
//   parseSucces("ADD AIRPORT {name:Brussels_Airport, short:BRU, city:{short:bru}}",
//    			AddAirport(Airport_data(City(Empty(),Filled("bru")),"Brussels_Airport","BRU")))
    			
   //-----Different operations-----
   //-----CITIES-----
   parseSucces("ADD CITY {name:Brussels}",
		   AddCity(City_data("Brussels")))
   parseSucces("CHANGE CITY {name:Bru} TO {name:Brussel}",
		   ChangeCity(City(Filled("Bru")),City(Filled("Brussel")))) 
   parseSucces("REMOVE CITY { }",
		   RemoveCity(City(Empty()))) 
    			
   //-----Airports-----
   parseSucces("ADD AIRPORT {city:{name:Brussels}, name:\"Brussels airport\", short:BRU}",
		   AddAirport(Airport_data(City(Filled("Brussels")), "Brussels airport", "BRU")))
   parseSucces("CHANGE AIRPORT {short:BRU} TO {city:{name:Zaventem}}",
		   ChangeAirport(
				   Airport(Empty(), Empty(), Filled("BRU")),
				   Airport(Filled(City(Filled("Zaventem"))), Empty(), Empty())))
   parseSucces("REMOVE AIRPORT {name:\"Brussels airport\"}",
		   RemoveAirport(Airport(Empty(), Filled("Brussels airport"), Empty())))
   
   //-----Distance-----
   parseSucces("ADD DISTANCE {from:{short:BRU}, to:{city:{name:Amsterdam}}, dist:1000}",
		   AddDist(Dist_data(
		       Airport(Empty(), Empty(), Filled("BRU")),
		       Airport(Filled(City(Filled("Amsterdam"))), Empty(), Empty()),
		       1000)))
   parseSucces("CHANGE DISTANCE {from:{short:BRU}, to:{short:AMS}} TO {dist:1000}",
		   ChangeDistTo(
		       Dist(
			       Filled(Airport(Empty(), Empty(), Filled("BRU"))),
			       Filled(Airport(Empty(), Empty(), Filled("AMS"))),
			       Empty()),
			   Dist(
			       Empty(),
			       Empty(),
			       Filled(1000))))
   parseSucces("REMOVE DISTANCE {from:{short:BRU}}",
		   RemoveDist(
		       Dist(
			       Filled(Airport(Empty(), Empty(), Filled("BRU"))),
			       Empty(),
			       Empty())))
  
		   
   //-----Different types------
   
//   parseTypeSucces("{name:\"New York\"}", "city",
//		   		City(Filled("New York"), Empty()))
//   parseTypeSucces("{short:\"NY\"}", "city",
//		   		City(Empty(), Filled("NY")))
//   parseTypeSucces("{name:\"New York\", short:\"NY\"}", "cityData",
//		   		City_data("New York", "NY"))
//   parseTypeSucces("{name:\"Brussels airport\", city:{name:Brussels}, short:BRU}", "airport",
//		   		Airport(Filled("Brussels airport"), Filled(City(Filled("Brussels"), Empty())), Filled("BRU")))
//   parseTypeSucces("{name:\"Brussels airport\", city:{name:Brussels}, short:BRU}", "airportData",
//		   		Airport_data(City(Filled("Brussels"), Empty()), "Brussels airport", "BRU"))
//   parseTypeSucces("{price:100, seats:[1, 2]}", "prices",
//		   		Prices1(100, List(1, 2)))
    			
}