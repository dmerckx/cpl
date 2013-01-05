package handling

import syntax.Operation
import syntax.Type

//Opt syntax
import syntax.Empty
import syntax.Filled

// City imports (types & operations)
import syntax.AddCity
import syntax.City_data
import syntax.City
import syntax.ChangeCity
import syntax.RemoveCity

// Airport imports (types & operations)
import syntax.AddAirport
import syntax.ChangeAirport
import syntax.RemoveAirport
import syntax.Airport
import syntax.Airport_data

//Database
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import Q.interpolation

//Exceptions
import handling.exceptions._

case class Count(nr: Int);
case class Id(id: Int);

object Handler {

	/**
	 * Main method of the handler. All output of the parser will be processed by this method to be mapped on an actual database operation.
	 */
	def handle(op: Operation) = {
		op match {
			//CITY
		case AddCity(data) => addCity(data);
		case ChangeCity(city, to) => changeCity(city, to);
		case RemoveCity(city) => removeCity(city);
		//AIRPORT
		case AddAirport(airport) => addAirport(airport);
		case ChangeAirport(airportFrom, airportTo) => changeAirport(airportFrom, airportTo);
		case RemoveAirport(airport) => removeAirport(airport);
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	// City ///////////////
	////////////////////////////////////////////////////////////////////////////////

	def insert(c: City_data): Unit = { (Q.u + "INSERT INTO city(`name`) VALUES (" +? c.name + ")").execute; }
	def remove_City(c_name: String) = (Q.u + "DELETE FROM CITY WHERE name='" + c_name + "'").execute;
	def update_City(c_name: String, c_toName: String) = (Q.u + "UPDATE CITY SET name='" + c_toName + "'" + " WHERE " + "name='" + c_name + "'").execute;

	def addCity(data: City_data) = {
		//Check if the properties of this city etc are correct (only a-zA-Z), if not throw error.
		if (data.name != null) {

			if (!data.name.matches("[a-zA-Z]+"))
				throw new IllegalCityNameException(data.name);

			if (hasUniqueResult(select("count(*)", "city", "name='" + data.name + "'")))
				throw new AlreadyExistingCityNameException(data.name);

			//all checks are ok, add this city to the database 
			execute[City_data](insert, data)

		}
	}

	def changeCity(citySelector: City, cityChange: City) {
		citySelector match {
		case City(Filled(name)) => changeCity(name, cityChange);
		case City(Empty()) => //nothing
		}
	}

	def changeCity(name: String, cityChange: City) {
		cityChange match {
		case City(Filled(toName)) => changeCity(name, toName);
		case City(Empty()) => //nothing
		}
	}

	def changeCity(name: String, toName: String) {
		if (name != null) {

			if (!name.matches("[a-zA-Z]+"))
				throw new IllegalCityNameException(name);

			existsCityName(name);

			if (exists(select("count(*)", "city", "name='" + toName + "'")))
				throw new AlreadyExistingCityNameException(toName);

			//TODO meer generisch maken
			Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
					driver = "com.mysql.jdbc.Driver") withSession {
				update_City(name, toName);
			}
		}
	}

	def removeCity(citySelector: City) {
		citySelector match {
		case City(Filled(name)) => removeCity(name);
		case City(Empty()) => //nothing
		}
	}

	def removeCity(name: String) {
		if (name != null) {
			//			existsCityName(name);
			execute(remove_City, name);
		}
	}

	def existsCityName(cityName: String) {
		if (!exists(select("count(*)", "city", "name='" + cityName + "'")))
			throw new NoSuchCityNameException(cityName);
	}

	////////////////////////////////////////////////////////////////////////////////
	// Airport ////////////
	////////////////////////////////////////////////////////////////////////////////

	def getCityName(airport:Airport_data) : String =  {
			var cityName = "";
			airport match {
				case Airport_data(City(Filled(city)), name, short) => cityName = city;
				case Airport_data(City(Empty()), name, short) => //nothing
			}
			return cityName;
	}

	def insert(airport: Airport_data): Unit = {
			val cityName = getCityName(airport);
			val id = getId("SELECT idCity FROM CITY WHERE (city.name='" + cityName +"')");
			(Q.u + "INSERT INTO airport(`code`,`city`,`name`) VALUES ('" + airport.short + "','" + (id+"") + "','" + airport.name + "')").execute; 
	}

	def addAirport(airport: Airport_data) {
		//Check if the properties of this airport etc are correct (only a-zA-Z), if not throw error.
		var cityName = getCityName(airport);
		if (airport.name != null && cityName != "") {

			if (!airport.name.matches("[a-zA-Z]+"))
				throw new IllegalAirportNameException(airport.name);

			if (!hasUniqueResult(select("count(*)", "city", "name='" + cityName + "'")))
				throw new NoSuchCityException(cityName);

			if (hasUniqueResult(select("count(*)", "airport", "(name='" + airport.name + "'" + " and " + "code='" + airport.short + "')")))
				throw new AlreadyExistingAirportException(airport.name, airport.short, cityName);

			//all checks are ok, add this airport to the database 
			execute[Airport_data](insert, airport)
		}
	}

	def removeAirport(airport: Airport) {

	}

	def changeAirport(airportFrom: Airport,airportTo: Airport) {

	}

	////////////////////////////////////////////////////////////////////////////////
	// Database ////////////
	////////////////////////////////////////////////////////////////////////////////	

	def execute[Type](func: (Type => Unit), data: Type) {
		Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
				driver = "com.mysql.jdbc.Driver") withSession {
			func(data);
		}
	}

	/**
	 * Returns true if the specified select query returns at least one result.
	 */
	def exists(query: String): Boolean = {
			var result = false;
			Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
					driver = "com.mysql.jdbc.Driver") withSession {
				result = (count(query) >= 1);
			}
			return result;
	}

	/**
	 * This method verifies if the specified select query returned exactly one result.
	 */
	def hasUniqueResult(query: String): Boolean = {
			var result = false;
			Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
					driver = "com.mysql.jdbc.Driver") withSession {
				result = (count(query) == 1);
			}
			return result;
	}

	implicit val getCountResult = GetResult(r => Count(r.nextInt));
	def count(query: String): Int = {
			val q = Q.queryNA[Count](query);
			return q.first.nr;
	}
	
	implicit val getIdResult = GetResult(r => Id(r.nextInt));
	def getId(query:String) : Int = {
		val q = Q.queryNA[Id](query);
		return q.first.id;
	}
	
	def select(select: String, from: String, where: String): String = {
			return "select " + select + " from " + from + " where " + where;
	}

	def insert(into: String, values: String): String = {
			return "insert into " + into + " values " + values;
	}

	def update(update: String, set: String, where: String): String = {
			return "update " + update + " SET " + set + " where " + where;
	}

	def delete(from: String, where: String): String = {
			return "delete from " + from + " where " + where;
	}

}

