package handling


import syntax.AddAirport
import syntax.Airport1
import syntax.Airport2
import syntax.Airport3
import syntax.Airport_data
import syntax.ChangeAirportCity
import syntax.ChangeAirportName
import syntax.ChangeAirportShort
import syntax.AddCity
import syntax.City_data
import syntax.City1
import syntax.City2
import syntax.City
import syntax.Operation
import syntax.ChangeTemplatePricesTo
import syntax.ChangeCityName
import syntax.ChangeCityCode
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
import handling.exceptions.AlreadyExistingAirportCode
import handling.exceptions.AlreadyExistingAirportName
import handling.exceptions.NoSuchAirportName
import syntax.Airport
import handling.exceptions.NoSuchAirportCity
import handling.exceptions.NoSuchAirportCode
import handling.exceptions.NotAllowedAirportName
import handling.exceptions.NotAllowedAirportCode
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation
import scala.slick.session.Session
import syntax.Template

case class TemplateException(templ:Template, msg:String) extends Exception;

object Handler {

	def handle(op: Operation) = {
		op match {
		case AddCity(data) => addCity(data)
		case ChangeCityName(city, to) => changeCityNameSuper(city, to)
		case ChangeCityCode(city, to:String) => changeCityCodeSuper(city, to)
		case RemoveCity(city) => removeCitySuper(city)

		case AddAirport(data) => addAirport(data)
		case ChangeAirportName(airport, toName) => changeAirportNameSuper(airport, toName)
		case ChangeAirportShort(airport, toShort) => changeAirportShortSuper(airport, toShort)
		case ChangeAirportCity(airport, toCity) => changeAirportCitySuper(airport, toCity)
		//		case ChangeAirportName(name, to) => changeAirportName(name, to)
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

		println(select("*", "city", "name=" + data.name));
		
		if (evalGet(select("*", "city", "name='" + data.name + "'")))
			throw new AlreadyExistingCityNameException(data.name);
		if (evalGet(select("*", "city", "idCity='" + data.short + "'")))
			throw new AlreadyExistingCityShortException(data.short);

		//If all checks are ok, add this city to the database 
		val query = insert("city", makeValues(List(data.short, data.name)));
		executeUpdateQuery(query);
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
		}
	}

	def changeCityName(city: City1, to: String) = {
		if (!cityExists(city))
			throw new NoSuchCityException(city);

		if (evalGet(select("*", "city", "name='" + to + "'")))
			throw new NotAllowedCityNameException(to);

		executeUpdateQuery(update("city", "name='" + to + "'", "name='" + city.name + "'"));
	}

	def changeCityName(city: City2, to: String) = {
		if (!cityExists(city))
			throw new NoSuchCityException(city);

		if (evalGet(select("*", "city", "name='" + to + "'")))
			throw new NotAllowedCityNameException(city.short);

		executeUpdateQuery(update("city", "name='" + to + "'", "idCity='" + city.short + "'"));
	}

	def changeCityCodeSuper(city:City, to:String) {
		city match {
		case city:City1 => changeCityCode(city, to)
		case city:City2 => changeCityCode(city, to)
		}
	}

	def changeCityCode(city:City1, to:String) {
		if (!cityExists(city))
			throw new NoSuchCityException(city);

		if (evalGet(select("*", "city", "idCity='" + to + "'")))
			throw new NotAllowedCityCodeException(to);

		executeUpdateQuery(update("city", "idCity='" + to + "'", "name='" + city.name + "'"));
	}

	def changeCityCode(city:City2, to:String) {
		if (!cityExists(city))
			throw new NoSuchCityException(city);

		if (evalGet(select("*", "city", "idCity='" + to + "'")))
			throw new NotAllowedCityCodeException(to);

		executeUpdateQuery(update("city", "idCity='" + to + "'", "idCity='" + city.short  + "'"));
	}

	//TODO naam veranderen
	def removeCitySuper(city:City) {
		city match {
		case city:City1 => removeCity(city)
		case city:City2 => removeCity(city)
		}
	}

	def removeCity(city:City1) {
		if (!cityExists(city))
			throw new NoSuchCityException(city);

		executeUpdateQuery(delete("city", "name='" + city.name + "'"));
	}

	def removeCity(city:City2) {
		if (!cityExists(city))
			throw new NoSuchCityException(city);

		executeUpdateQuery(delete("city", "idCity='" + city.short + "'"));
	}

	def cityExistsSuper(city:City) : Boolean = {
			city match {
			case city:City1 => return cityExists(city);
			case city:City2 => return cityExists(city);
			}
	}

	def cityExists(city:City1) : Boolean = {
			return evalGet(select("*", "city", "name='" + city.name + "'"));
	}

	def cityExists(city:City2) : Boolean = {
			return evalGet(select("*", "city", "idCity='" + city.short + "'"));
	}

	// ----- End Cities ------ //

	// ----- Begin Airports ------ //

	def addAirport(data: Airport_data) = {
		if(!cityExistsSuper(data.city))
			throw new NoSuchCityException(data.city);
		if(evalGet(select("*","aiport","name='" + data.name + "'")))
			throw new AlreadyExistingAirportName(data.name);
		if(evalGet(select("*","airport", "code='" + data.short + "'")))
			throw new AlreadyExistingAirportCode(data.short);

		//If all checks are ok, add this airport to the database 
		val query = insert("airport", makeValues(List(data.short, data.city, data.name)));
		executeUpdateQuery(query);
		println("Add airport with query: " + query);
	}

	def changeAirportNameSuper(airport:Airport, toName:String) = {
		if(evalGet(select("*","airport", "name='" + toName + "'")))
			throw new NotAllowedAirportName(toName);
		airport match {
		case airport:Airport1 => changeAirportName(airport,toName)
		case airport:Airport2 => changeAirportName(airport,toName)
		case airport:Airport3 => changeAirportName(airport,toName)
		}
	}

	def changeAirportName(airport:Airport1, toName:String) = {
		if(!evalGet(select("*","airport","name='" + airport.name + "'")))
			throw new NoSuchAirportName(airport.name);
		//TODO optional
		executeUpdateQuery(update("airport","name='" + toName + "'","name='" + airport.name + "'"))
	}

	def changeAirportName(airport:Airport2, toName:String) = {
		if(!evalGet(select("*","airport","city='" + airport.city + "'")))
			throw new NoSuchAirportCity(airport.city);
		//TODO optional
		executeUpdateQuery(update("airport","name='" + toName + "'","city='" + airport.city + "'"))
	}

	def changeAirportName(airport:Airport3, toName:String) = {
		if(!evalGet(select("*","airport","code='" + airport.short + "'")))
			throw new NoSuchAirportCode(airport.short);
		//TODO optional
		executeUpdateQuery(update("airport","name='" + toName + "'","code='" + airport.short + "'"))
	}

	def changeAirportShortSuper(airport:Airport, toShort:String) = {
		if(evalGet(select("*","airport", "code='" + toShort + "'")))
			throw new NotAllowedAirportCode(toShort);
		airport match {
		case airport:Airport1 => changeAirportShort(airport,toShort)
		case airport:Airport2 => changeAirportShort(airport,toShort)
		case airport:Airport3 => changeAirportShort(airport,toShort)
		}
	}

	def changeAirportShort(airport:Airport1, toShort:String) = {
		if(!evalGet(select("*","airport","name='" + airport.name + "'")))
			throw new NoSuchAirportName(airport.name);
		//TODO optional
		executeUpdateQuery(update("airport","code='" + toShort + "'","name='" + airport.name + "'"))
	}

	def changeAirportShort(airport:Airport2, toShort:String) = {
		if(!evalGet(select("*","airport","city='" + airport.city + "'")))
			throw new NoSuchAirportCity(airport.city);
		//TODO optional
		executeUpdateQuery(update("airport","code='" + toShort + "'","city='" + airport.city + "'"))
	}

	def changeAirportShort(airport:Airport3, toShort:String) = {
		if(!evalGet(select("*","airport","code=" + airport.short + "'")))
			throw new NoSuchAirportCode(airport.short);
		//TODO optional
		executeUpdateQuery(update("airport","code='" + toShort + "'","code='" + airport.short + "'"))
	}

	def changeAirportCitySuper(airport:Airport, city:City) = {
		airport match {
		case airport:Airport1 => changeAirportCity(airport,city)
		case airport:Airport2 => changeAirportCity(airport,city)
		case airport:Airport3 => changeAirportCity(airport,city)
		}
	}

	def changeAirportCity(airport:Airport1, toCity:City) =  {
		if(!evalGet(select("*","AIRPORT","name=" + airport.name)))
			throw new NoSuchAirportName(airport.name);
		//TODO optional
		executeUpdateQuery(update("airport","city='" + toCity + "'","name='" + airport.name + "'"))
	}

	def changeAirportCity(airport:Airport2, toCity:City) =  {
		if(!evalGet(select("*","airport","city='" + airport.city + "'")))
			throw new NoSuchAirportCity(airport.city);
		//TODO optional
		executeUpdateQuery(update("airport","city='" + toCity,"city='" + airport.city + "'"))
	}

	def changeAirportCity(airport:Airport3, toCity:City) =  {
		if(!evalGet(select("*","airport","code='" + airport.short + "'")))
			throw new NoSuchAirportCode(airport.short);
		//TODO optional
		executeUpdateQuery(update("airport","city='" + toCity + "'","code='" + airport.short + "'"))
	}

	def airportExists(airport:Airport1) : Boolean = {
		return evalGet(select("*","airport","name='" + airport.name + "'"));
		//TODO optional
	}

	def airportExists(airport:Airport2) : Boolean = {
		return evalGet(select("*","airport","city='" + airport.city + "'"));
		//TODO optional
	}

	def airportExists(airport:Airport3) : Boolean = {
		return evalGet(select("*","airport","code='" + airport.short + "'"));
		//TODO optional
	}

	// ----- End Airports ------ //

	def changeTemplatePricesTo(templ:Template, to: List[PricePeriod]) = {
		//Check template..
		checkTemplate(templ);

		//Check price periods..
	}

	// ------ CHECKS ----- //

	def checkTemplate(templ:Template) = {
		//Check database to see if this template corresponds to a correct template
//		val query = "SELECT * FROM Templates WHERE flightnr=\"" + templ.flightnr + "\"";
//		println("Check template with query: " + query);
//TODO aanpassen aan nieuwe template.
		//If no such flightnr can be found..
		throw new TemplateException(templ, "No template with such flightnr excists");
	}

	def evalGet(query: String) : Boolean = {
		return executeCount(query) >= 1
	}

	def executeCount(query: String): Int = {
		//perform a get and return whether or not it was succesful.
		var variable1 = 0;
		Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
				driver = "com.mysql.jdbc.Driver") withSession {
			variable1 = (Q.u + query).execute.asInstanceOf[Int];
		}
		//TODO return
		return variable1;
	}

	def executeUpdateQuery(query: String) : Boolean = {
		//evaluta the query but don't return any result
		Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
				driver = "com.mysql.jdbc.Driver") withSession {
			(Q.u + query).execute
			return true;
		}
		println("query: " + query);
		return false;
	}

	//	Q.queryNA[Coffee]("select * from coffees") foreach { c =>
	//  println("  " + c.name + "\t" + c.supID + "\t" + c.price + "\t" + c.sales + "\t" + c.total)
	//}

	def select(select: String, from: String, where: String): String = {
		return "select " + select + " from " + from + " where " + where;
	}

	def insert(into: String, values: String): String = {
		return "insert into " + into + " values " + values;
	}

	def update(update: String, set: String, where:String): String = {
		return "update " + update + " SET " + set + " where " + where;
	} 

	def delete(from:String, where:String) : String = {
		return "delete from " + from + " where "+ where;
	}

}
