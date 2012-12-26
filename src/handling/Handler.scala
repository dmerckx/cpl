package handling

import syntax.AddAirport
import syntax.AddCity
import syntax.Airport_data
import syntax.City_data
import syntax.City1
import syntax.City2
import syntax.City3
import syntax.City
import syntax.Operation
import syntax.ChangeTemplatePricesTo
import syntax.ChangeCityName
import syntax.ChangeCityCode
import syntax.Template
import syntax.PricePeriod
import syntax.RemoveCity
import handling.exceptions.IllegalCityNameException
import handling.exceptions.IllegalCityCodeException
import handling.exceptions.AlreadyExistingCityNameException
import handling.exceptions.NoSuchCityException
import com.sun.org.apache.xalan.internal.xsltc.compiler.ForEach
import handling.exceptions.AlreadyExistingCityShortException
import handling.exceptions.NotAllowedCityNameException
import handling.exceptions.NotAllowedCityCodeException

case class TemplateException(templ: Template, msg: String) extends Exception;

object Handler {

	def handle(op: Operation) = {
		op match {
		case AddCity(data) => addCity(data)
		case ChangeCityName(city, to) => changeCityNameSuper(city, to)
		case ChangeCityCode(city, to:String) => changeCityCodeSuper(city, to)
		case RemoveCity(city) => removeCitySuper(city)

		case AddAirport(data) => addAirport(data)
		//...
		case ChangeTemplatePricesTo(templ, to) => changeTemplatePricesTo(templ, to)
		}
	}

	// ----- OPERATIONS ----- //

	// ----- Begin Cities ------ //

	/**
	 * If there is a city that has either the same name or the same code, then the add will not succeed. 
	 * So each city has to have a unique name and code (the combination is not sufficient).
	 */
	def addCity(data: City_data) = {
		//Check if the properties of this city etc are correct (only a-zA-Z), if not throw error.
		if (data.name == null || !data.name.matches("[a-zA-Z]+"))
			throw new IllegalCityNameException(data.name);
		if (data.short == null || data.short.length() != 3)
			throw new IllegalCityCodeException(data.short);

		if (evalGet(select("*", "CITY", "name=" + data.name)))
			throw new AlreadyExistingCityNameException(data.name);
		if (evalGet(select("*", "CITY", "idCity=" + data.short)))
			throw new AlreadyExistingCityShortException(data.short);

		//If all checks are ok, add this city to the database 
		val query = insert("CITY", makeValues(List(data.short, data.name)));
		executeQuery(query);
		println("Add city with query: " + query);
	}

	def makeValues(list: List[Object]): String = {
		var result = "(";
		list.foreach(obj =>
		result = result + "'" + obj.toString() + "',");
		result = result.substring(0, result.length() - 1) + ")";
		return result;
	}
	//TODO andere naam!
	def changeCityNameSuper(city: City, to: String) = {
		city match {
		case city:City1 => changeCityName(city, to)
		case city:City2 => changeCityName(city, to)
		case city:City3 => changeCityName(city, to)
		}
	}

	def changeCityName(city: City1, to: String) = {
		if (!cityExists(city))
			throw new NoSuchCityException(city);

		if (evalGet(select("*", "CITY", "name=" + to)))
			throw new NotAllowedCityNameException(to);

		executeQuery(update("CITY", "name=" + to, "name=" + city.name));
	}

	def changeCityName(city: City2, to: String) = {
		if (!cityExists(city))
			throw new NoSuchCityException(city);

		if (evalGet(select("*", "CITY", "name=" + to)))
			throw new NotAllowedCityNameException(city.short);

		executeQuery(update("CITY", "name=" + to, "idCity=" + city.short));
	}

	def changeCityName(city: City3, to: String) = {
		if (!cityExists(city))
			throw new NoSuchCityException(city);

		if (evalGet(select("*", "CITY", "name=" + to)))
			throw new NotAllowedCityNameException(city.short);

		executeQuery(update("CITY", "name=" + to, "idCity=" + city.short  + " AND " + "name=" + city.name));
	}

	def changeCityCodeSuper(city:City, to:String) {
		city match {
		case city:City1 => changeCityCode(city, to)
		case city:City2 => changeCityCode(city, to)
		case city:City3 => changeCityCode(city, to)
		}
	}

	def changeCityCode(city:City1, to:String) {
		if (!cityExists(city))
			throw new NoSuchCityException(city);

		if (evalGet(select("*", "CITY", "idCity=" + to)))
			throw new NotAllowedCityCodeException(to);

		executeQuery(update("CITY", "idCity=" + to, "name=" + city.name));
	}

	def changeCityCode(city:City2, to:String) {
		if (!cityExists(city))
			throw new NoSuchCityException(city);

		if (evalGet(select("*", "CITY", "idCity=" + to)))
			throw new NotAllowedCityCodeException(to);

		executeQuery(update("CITY", "idCity=" + to, "idCity=" + city.short));
	}

	def changeCityCode(city:City3, to:String) {
		if (!cityExists(city))
			throw new NoSuchCityException(city);

		if (evalGet(select("*", "CITY", "idCity=" + to)))
			throw new NotAllowedCityCodeException(to);

		executeQuery(update("CITY", "idCity=" + to, "idCity=" + city.short + " AND " + "name=" + city.name));
	}

	//TODO naam veranderen
	def removeCitySuper(city:City) {
		city match {
		case city:City1 => removeCity(city)
		case city:City2 => removeCity(city)
		case city:City3 => removeCity(city)
		}
	}

	def removeCity(city:City1) {
		if (!cityExists(city))
			throw new NoSuchCityException(city);

		executeQuery(delete("CITY", "name=" + city.name));
	}

	def removeCity(city:City2) {
		if (!cityExists(city))
			throw new NoSuchCityException(city);

		executeQuery(delete("CITY", "idCity=" + city.short));
	}

	def removeCity(city:City3) {
		if (!cityExists(city))
			throw new NoSuchCityException(city);

		executeQuery(delete("CITY", "name=" + city.name + " AND " + "idCity=" + city.short));
	}

	def cityExistsSuper(city:City) : Boolean = {
	   city match {
	     case city:City1 => return cityExists(city);
	     case city:City2 => return cityExists(city);
	     case city:City3 => return cityExists(city);
	  }
	}
	
	def cityExists(city:City1) : Boolean = {
			return evalGet(select("*", "CITY", "name=" + city.name));
	}

	def cityExists(city:City2) : Boolean = {
			return evalGet(select("*", "CITY", "idCity=" + city.short));
	}

	def cityExists(city:City3) : Boolean = {
			return evalGet(select("*", "CITY", "idCity=" + city.short + " AND " + "name=" + city.name));
	}

	// ----- End Cities ------ //

	// ----- Begin Airports ------ //

	def addAirport(data: Airport_data) = {
		if(!cityExistsSuper(data.city))
			throw new NoSuchCityException(data.city);
	}

	// ----- End Airports ------ //

	def changeTemplatePricesTo(templ: Template, to: List[PricePeriod]) = {
		//Check template..
		checkTemplate(templ);

		//Check price periods..
	}

	// ------ CHECKS ----- //

	def checkTemplate(templ: Template) = {
		//Check database to see if this template corresponds to a correct template
		val query = "SELECT * FROM Templates WHERE flightnr=\"" + templ.flightnr + "\"";
		println("Check template with query: " + query);

		//If no such flightnr can be found..
		throw new TemplateException(templ, "No template with such flightnr excists");
	}

	def evalGet(query: String): Boolean = {
		//perform a get and return whether or not it was succesful.
		return false;
	}

	def executeQuery(query: String) = {
		//evaluta the query but don't return any result
		println("query: " + query);
	}

	def select(select: String, from: String, where: String): String = {
		return "SELECT " + select + " FROM " + from + " WHERE " + where;
	}

	def insert(into: String, values: String): String = {
		return "INSERT INTO " + into + " VALUES " + values;
	}

	def update(update: String, set: String, where:String): String = {
		return "UPDATE " + update + " SET " + set + " WHERE " + where;
	} 

	def delete(from:String, where:String) : String = {
		return "DELETE FROM " + from + " WHERE "+ where;
	}

}