package handling

import syntax._
//Database
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import Q.interpolation

//Exceptions
import handling.exceptions._

case class Count(nr: Int);
case class Id(id: Int);
case class Code(id:String);

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

		//AIRPLANETYPE
		case AddAirplaneType(airplaneType , arrangement) => addAirplaneType(airplaneType, arrangement);
		case ChangeAirplaneType(airplaneTypeFrom, airplaneTypeTo) => changeAirplaneType(airplaneTypeFrom, airplaneTypeTo);
		case RemoveAirplaneType(airplaneType) => removeAirplaneType(airplaneType);

		//AIRLINE
		case AddAirline(airline) => addAirline(airline);
		case ChangeAirline(airlineFrom, airlineTo) => changeAirline(airlineFrom, airlineTo) ;
		case RemoveAirline(airline) => removeAirline(airline);

		//SEATTYPE
		case AddSeatType(seatType) => addSeatType(seatType);
		case RemoveSeatType(seatType) => removeSeatType(seatType);
		case ChangeSeatType(from, to) => changeSeatType(from, to);
		
		//DISTANCE
		case AddDist(distance) => addDist(distance);
		
		//FLIGHTTIME
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

	def insert(airport: Airport_data): Unit = {
			val cityName = getCityName(airport);
			val id = getIds("SELECT idCity FROM CITY WHERE (city.name='" + cityName +"')").foreach(id => 
			(Q.u + "INSERT INTO airport(`code`,`city`,`name`) VALUES ('" + airport.short + "','" + (id+"") + "','" + airport.name + "')").execute); 
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
		//TODO
	}

	def changeAirport(airportFrom: Airport,airportTo: Airport) {
		//TODO
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// Distances /////////
	////////////////////////////////////////////////////////////////////////////////
	
	def getAirportFromIds(dist: Dist_data) : List[Int] = {
		getAirportIds(dist.from);
	}
	
	def getAirportToIds(dist: Dist_data) : List[Int] = {
		getAirportIds(dist.to);
	}
	
	def insert(dist: Dist_data) : Unit = {
		val airportFromList = getAirportFromIds(dist);
		val airportToList = getAirportToIds(dist);
		for(idFrom <- airportFromList) {
			for(idTo <- airportToList) {
				(Q.u + "INSERT INTO distance('idFromCity','idToCity','distance') VALUES ('" + (idFrom + "") + "','" + (idTo + "") + "','" + (dist.dist+"") + "')").execute();
			}
		}
	}
	
	def addDist(dist: Dist_data) {
		if (dist.dist <= 0) {
			throw new IllegalDistanceException(dist.dist);
		}
		execute[Dist_data](insert, dist);
	}

	////////////////////////////////////////////////////////////////////////////////
	// AirplaneType /////////
	////////////////////////////////////////////////////////////////////////////////

	def insert(airplaneType: AirplaneType_data): Unit = {
			//TODO 
			(Q.u + "INSERT INTO airplanetype(`name`) VALUES ('" + airplaneType.name + "')").execute;
	}

	def addAirplaneType(airplaneType:AirplaneType_data, arrangement: List[Seat_data]) {
		//TODO handle list
		if(airplaneType.name != null) {
			if (!airplaneType.name.matches("[a-zA-Z]+"))
				throw new IllegalAirplaneTypeNameException(airplaneType.name);

			if (hasUniqueResult(select("count(*)", "airplanetype", "(name='" + airplaneType.name + "')")))
				throw new AlreadyExistingAirplaneTypeException(airplaneType.name);

			execute[AirplaneType_data](insert, airplaneType);
		}
	}

	def changeAirplaneType(airplaneFrom:AirplaneType, airplaneTo:AirplaneType) {

	}

	def removeAirplaneType(airplane:AirplaneType) {

	}

	////////////////////////////////////////////////////////////////////////////////
	// Airline /////////
	////////////////////////////////////////////////////////////////////////////////

	def insert(airline: Airline_data): Unit = {
			(Q.u + "INSERT INTO Airline(`name`, `idAirline`) VALUES ('" + airline.name + "','" + airline.short+ "')").execute;
	}

	def addAirline(airline:Airline_data) {
		if(airline.name != null) {
			if (!airline.name.matches("[a-zA-Z]+"))
				throw new IllegalAirlineNameException(airline.name);
			if(airline.short.length() > 3 || airline.short.length() == 0 )
				throw new IllegalAirlineCodeException(airline.short);
			if (hasUniqueResult(select("count(*)", "airline", "(name='" + airline.name + "')")))
				throw new AlreadyExistingAirlineNameException(airline.name);
			if (hasUniqueResult(select("count(*)", "airline", "(name='" + airline.short + "')")))
				throw new AlreadyExistingAirlineCodeException(airline.short);

			execute[Airline_data](insert, airline);
		}
	}

	def changeAirline(airlineFrom:Airline, airlineTo:Airline) {
		//TODO
	}

	def removeAirline(airline:Airline) {
		//TODO
	}

	////////////////////////////////////////////////////////////////////////////////
	// SeatType /////////
	////////////////////////////////////////////////////////////////////////////////

	def insert(name:String) = (Q.u + "INSERT INTO SEATTYPE(`name`) VALUES ('" + name + "')").execute;

	def addSeatType(name:String) {
		if(name != null) {
			if (!name.matches("[a-zA-Z]+"))
				throw new IllegalSeatTypeException(name);
			if(hasUniqueResult(select("name","SeatType","name='" + name + "'")))
				throw new AlreadyExistingSeatTypeException(name:String);

			Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
					driver = "com.mysql.jdbc.Driver") withSession {
				insert(name);
			}
		}
	}


	def removeSeatType(name:String) {
		//TODO
	}

	def changeSeatType(seatTypeFrom: String, seatTypeTo:String) {
		//TODO
	}

	////////////////////////////////////////////////////////////////////////////////
	// Dist /////////
	////////////////////////////////////////////////////////////////////////////////


	////////////////////////////////////////////////////////////////////////////////
	// Database ////////////
	////////////////////////////////////////////////////////////////////////////////	

	def getCityIds(city:City) : List[Int] = {
			var cityName ="";
			var result = List[Int]();
			city match {
			case City(Filled(name)) => cityName = name;
			case City(Empty()) => //donothing
			}

			if(!cityName.equals("")) {
				result = getIds("SELECT idCity FROM CITY WHERE (city.name='" + cityName +"')") ::: result;
			}
			return result;
	}

	def getCityName(airport:Airport_data) : String =  {
			var cityName = "";
			airport match {
			case Airport_data(City(Filled(city)), name, short) => cityName = city;
			case Airport_data(City(Empty()), name, short) => //nothing
			}
			return cityName;
	}

	def getAirportIds(airport:Airport) : List[String] = {
			var airportName ="";
			var airportShort = "";
			var airportCities = List[Int]();
			airport match {
			case Airport(Filled(city), Filled(name), Filled(short)) => (airportName = name, airportShort = short, airportCities = getCityIds(city));
			case Airport(Filled(city),Filled(name),Empty()) => (airportName = name, airportCities = getCityIds(city));
			case Airport(Filled(city),Empty(), Filled(short)) => (airportShort = short, airportCities = getCityIds(city));
			case Airport(Empty(),Filled(name), Filled(short)) =>(airportName = name, airportShort = short);
			case Airport(Filled(city),Empty(), Empty()) => (airportCities = getCityIds(city));
			case Airport(Empty(), Filled(name), Empty()) => (airportName = name);
			case Airport(Empty(), Empty(), Filled(short)) => (airportShort = short);
			case Airport(Empty(),Empty(),Empty()) => //match all
			}
			var result = List[String]();
			var select = "";
			if(!airportName.equals("")) {
			  select += "name='";
			  select += airportName + "'";
			}
			if(!airportShort.equals("")) {
			  if(!select.equals(""))
			    select+= " and ";
			  select += "code='";
			  select += airportShort + "'";
			}
			
			if(airportCities.length >= 1) {
			  if(!select.equals(""))
			    select+= " and ";
			  select+="("
			  airportCities.foreach(id => select += "city='" + id +"' or " );
			  select = select.substring(0, select.length()-4); //remove last "or"
			  select+=")";
			}
			println(select);
			if(!select.equals(""))
			  result = getCodes("Select code from airport where " + select);
			else
			  result = getCodes("Select code from airport");
			return result;
	}

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
	def getIds(query:String) : List[Int] = {
			var result = List[Int]();
			Q.queryNA[Id](query).foreach( r => result = r.id :: result);
			return result;
	}
	
	implicit val getCodeResult = GetResult(r => Code(r.nextString));
	def getCodes(query:String) : List[String] = {
			var result = List[String]();
			Q.queryNA[Code](query).foreach( r => result = r.id :: result);
			return result;
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

