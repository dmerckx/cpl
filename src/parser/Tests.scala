package parser;

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import syntax.Operation
import syntax._;

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
   
   //-----Simple expression
    //Simple tests
   parseSucces("ADD CITY {name:Brussels, short:BRU}",
    			AddCity(City_data("Brussels", "BRU"))) 
   parseSucces("ADD CITY {name:Malinas, short:MAL}",
    			AddCity(City_data("Malinas", "MAL")))
    //Reverse order
   parseSucces("ADD CITY {short:MAL, name:Malinas}",
    			AddCity(City_data("Malinas", "MAL")))
    			
    			
   //-----Expression with nested type & optional parameters
    //Standard tests
   parseSucces("ADD AIRPORT {name:Brussels_Airport, city:{name:brussels, short:bru}, short:BRU}",
    			AddAirport(Airport_data(City1("brussels",Filled("bru")),"Brussels_Airport","BRU")))
   parseSucces("ADD AIRPORT {name:Brussels_Airport, city:{short:bru}, short:BRU}",
    			AddAirport(Airport_data(City2("bru"),"Brussels_Airport","BRU")))
    //Optional parameters unfilled
   parseSucces("ADD AIRPORT {name:Brussels_Airport, city:{name:brussels}, short:BRU}",
    			AddAirport(Airport_data(City1("brussels",Empty()),"Brussels_Airport","BRU")))
    //Reverse order
   parseSucces("ADD AIRPORT {city:{short:bru}, short:BRU, name:Brussels_Airport}",
    			AddAirport(Airport_data(City2("bru"),"Brussels_Airport","BRU")))
   parseSucces("ADD AIRPORT {name:Brussels_Airport, short:BRU, city:{short:bru}}",
    			AddAirport(Airport_data(City2("bru"),"Brussels_Airport","BRU")))
    			
}