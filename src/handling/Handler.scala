package handling

import syntax.AddAirport
import syntax.AddCity
import syntax.Airport_data
import syntax.City_data
import syntax.Operation
import syntax.ChangeTemplatePricesTo
import syntax.Template
import syntax.PricePeriod


case class TemplateException(templ:Template, msg:String) extends Exception;

object Handler {

  def handle(op:Operation) = {
    op match {
      case AddCity(data) => addCity(data) 
      case AddAirport(data) => addAirport(data)
      //...
      case ChangeTemplatePricesTo(templ, to) => changeTemplatePricesTo(templ, to)
    }
  }
  
  // ----- OPERATIONS ----- //
  
  def addCity(data:City_data) = {
	  //Check if the name of this city etc is correct (only a-zA-Z), if not throw error
	  	
	  //If all checks are ok, add this city to the database 
	  val query = "INSERT (...) INTO cities";
	  println("Add city with query: " + query);
  }
  
  def addAirport(data:Airport_data) = {
	  //Do query and shizzle
	  println("data: " + data);
  }
  
  def changeTemplatePricesTo(templ:Template, to:List[PricePeriod]) = {
	  //Check template..
	  checkTemplate(templ);
    
	  //Check price periods..
  }
  
  
  // ------ CHECKS ----- //
  
  def checkTemplate(templ:Template) = {
    //Check database to see if this template corresponds to a correct template
    val query = "SELECT * FROM Templates WHERE flightnr=\"" + templ.flightnr + "\"";
    println("Check template with query: " + query);  
    
    //If no such flightnr can be found..
    throw new TemplateException(templ, "No template with such flightnr excists");
  }
}