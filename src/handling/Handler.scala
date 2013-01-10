package handling

import syntax._
//Database
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import Q.interpolation

//Exceptions
import handling.exceptions._

import java.util.HashMap;
import java.util.Calendar
import java.text.SimpleDateFormat;

case class Count(nr: Int);
case class Id(id: Int);
case class Code(id:String);

case class TemplateId(idAirline: String, idTemplate: Int);
case class Duration(duration:java.sql.Time);
case class SeatNumber(nr: Int);

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

		//Flight
		case AddFlight(flight) => addFlight(flight);
		case AddFlight2(flight, prices) => addFlight2(flight, prices);
		case ChangeFlight(flightFrom, flightTo) => changeFlight(flightFrom, flightTo);
		case RemoveFlight(flight) => removeFlight(flight);

		//Template
		case AddTemplate(template, prices, periods) => addTemplate(template,prices,periods);
		case AddTemplatePeriods(template,periods) => addTemplatePeriods(template,periods);

		case AddSeats(airplaneType, seat_datas) => addSeats(airplaneType,seat_datas);
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
			println(name);
			if(!hasUniqueResult(select("count(*)", "city", "name='" + name + "'")))
				throw new NonUniqueCityNameException();

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
			if(areReferencesTo(name))
				throw new ExistingReferenceException();
			execute(remove_City, name);
		}
	}

	def areReferencesTo(name:String) : Boolean = {
			var result = false;
			Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
					driver = "com.mysql.jdbc.Driver") withSession {
				var airportIds = getCodes("select idAirport from (Airport JOIN City ON airport.city=city.idCity) where city.name='" + name + "'");
				for(Id <- airportIds) {
					println("id: " + Id+"");
					val airport = Airport(Empty(),Empty(),Filled(Id));
					if(getTemplateIds(Template(Empty(),Empty(),Filled(airport),Empty(),Empty())).size > 0 || getTemplateIds(Template(Empty(),Empty(),Empty(),Filled(airport),Empty())).size > 0) {
						result= true;
					}
				}
			}
			return result;
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

	def getAirportFromIds(dist: Dist_data) : List[String] = {
			return getAirportIds(dist.from);
	}

	def getAirportToIds(dist: Dist_data) : List[String] = {
			return getAirportIds(dist.to);
	}

	def insert(dist: Dist_data) : Unit = {
			val airportFromList = getAirportFromIds(dist);
			val airportToList = getAirportToIds(dist);
			for(idFrom <- airportFromList) {
				for(idTo <- airportToList) {
					(Q.u + "INSERT INTO distance('idFromCity','idToCity','distance') VALUES ('" + idFrom + "','" + idTo + "','" + (dist.dist+"") + "')").execute();
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
	// FlightTimes /////////
	////////////////////////////////////////////////////////////////////////////////

	def getAirportFromIds(flightTime: FlightTime_data) : List[String] = {
			return getAirportIds(flightTime.from);
	}

	def getAirportToIds(flightTime: FlightTime_data) : List[String] = {
			return getAirportIds(flightTime.to); 
	}

	def getAirplaneTypeIds(flightTime: FlightTime_data) : List[Int] = {
			return getAirplaneTypeIds(flightTime.airplaneType);
	}

	def insert(flightTime: FlightTime_data) : Unit = {
			//TODO unique flight checking!
			val duration = flightTime.time;
			duration match {
			case Time(Empty(),Empty(),Empty()) => throw new NoDurationException();
			case _ => 
			}
			val airportFromList = getAirportFromIds(flightTime);
			val airportToList = getAirportToIds(flightTime);
			val airplaneTypeList = getAirplaneTypeIds(flightTime);
			val durationString = createDurationString(duration);
			for (fromId <- airportFromList) {
				for (toId <- airportToList) {
					for (typeId <- airplaneTypeList) {
						val query = "INSERT INTO flighttime(`idFromCity`,`idToCity`,`idAirplaneType`,`duration`) VALUES ('" + fromId + "','" + toId + "','" + (typeId+"") + "','" + durationString + "')";
						(Q.u + query).execute();			
					}
				}
			}
	}	

	def addFlightTime(flightTime: FlightTime_data) {
		execute[FlightTime_data](insert,flightTime);
	}

	def isValidHours(duration: Time) : Boolean = {
			var result = true;
			duration match {
			case Time(Empty(),_,_) => 
			case Time(Filled(h),_,_) => result = (h >= 0 && h < 24)
			}
			return result;
	}

	def isValidMinutes(duration: Time) : Boolean = {
			var result = true;
			duration match {
			case Time(_,Empty(),_) => 
			case Time(_,Filled(m),_) => result = (m >= 0 && m < 60)
			}
			return result;
	}

	def isValidSeconds(duration: Time) : Boolean = {
			var result = true;
			duration match {
			case Time(_,_,Empty()) => 
			case Time(_,_,Filled(s)) => result = (s >= 0 && s < 60)
			}
			return result;
	}

	def isValidDuration(duration: Time) : Boolean = {
			return (isValidHours(duration) && isValidMinutes(duration) && isValidSeconds(duration));
	}

	def getDurationHours(duration: Time) : String = {
			var result = "";
			duration match {
			case Time(Empty(),_,_) => result = "00"
			case Time(Filled(h),_,_) => (if (h/10 == 0) {result = ("0"+h+"")} else {result = (h+"")})
			}
			return result;
	}

	def getDurationMinutes(duration: Time) : String = {
			var result = "";
			duration match {
			case Time(_,Empty(),_) => result = "00"
			case Time(_,Filled(m),_) => (if (m/10 == 0) {result = ("0"+m+"")} else {result = (m+"")})
			}
			return result;
	}

	def getDurationSeconds(duration: Time) : String = {
			var result = "";
			duration match {
			case Time(_,_,Empty()) => result = "00"
			case Time(_,_,Filled(s)) => (if (s/10 == 0) {result = ("0"+s+"")} else {result = (s+"")})
			}
			return result;
	}

	def createDurationString(duration: Time) : String = {
			if (!isValidDuration(duration)) {
				throw new IllegalDurationException();
			}
			else {
				return getDurationHours(duration) + ":" + getDurationMinutes(duration) + ":" + getDurationSeconds(duration);
			}
	}

	////////////////////////////////////////////////////////////////////////////////
	// Template /////////
	////////////////////////////////////////////////////////////////////////////////

	def getAirportFromIds(template: Template_data) : List[String] = {
			return getAirportIds(template.from);
	}

	def getAirportToIds(template: Template_data) : List[String] = {
			return getAirportIds(template.to); 
	}

	def getAirplaneTypeIds(template: Template_data) : List[Int] = {
			return getAirplaneTypeIds(template.airplaneType);
	}

	def getAirlineIdFromFLN(fln: String) : String = {
			var result = fln.substring(0,3);
			if (fln.substring(2,3).matches("[0-9]")) {
				return fln.substring(0,2);
			}
			else {
				return result;
			}
	}

	def getTemplateIdFromFLN(fln: String) : String = {
			val flnLength = fln.length();
			var result = fln.substring(flnLength - 4, flnLength);
			if (fln.substring(flnLength - 4, flnLength - 3).matches("[A-Z]")) {
				return fln.substring(flnLength - 3, flnLength);
			}
			else {
				return result;
			}
	}

	def isValidFLN(fln: String) : Boolean = {
			if (!fln.matches("[A-Z]{2,3}[0-9]{3,4}")) {
				return false;
			}
			else {
				return true;
			}
	}

	def addTemplatePeriods(template:Template,periods:List[Period_data]) {
		var templateIds = getTemplateIds(template);
		for(Id <- templateIds) {
			addPeriods(periods,Id.idAirline,Id.idTemplate);
		}
	}

	def addPeriods(periods:List[Period_data],airlineId:String,templateId:Int) {
		for (period <- periods) {
			var periodQuery = getPeriodQuery(period,airlineId,templateId);
			println("period query" + periodQuery);
			(Q.u + periodQuery).execute();
		}
	}

	def addTemplate(template: Template_data, prices:List[SeatInstance_data], periods: List[Period_data]) {
		Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
				driver = "com.mysql.jdbc.Driver") withSession {
			val airportFromList = getAirportFromIds(template);
			if(airportFromList.size > 1) 
				throw new NonUniqueFromAirport();
			if(airportFromList.size == 0)
				throw new NoSuchFromAirport();

			val airportToList = getAirportToIds(template);
			if(airportToList.size > 1)
				throw new NonUniqueToAirport();
			if(airportToList.size == 0) 
				throw new NoSuchToAirport();

			val airplaneTypeList = getAirplaneTypeIds(template);
			if(airplaneTypeList.size > 1)
				throw new NonUniqueAirplaneType();
			if(airplaneTypeList.size == 0)
				throw new NoSuchAirplaneType();

			val fln = template.fln;
			if (!isValidFLN(fln)) 
				throw new IllegalFLNException(fln)

			val airlineId = getAirlineIdFromFLN(fln);
			val templateId = getTemplateIdFromFLN(fln);
			if (!isExistingAirline(airlineId))
				throw new NoSuchAirlineException(airlineId)		

			val fromId = airportFromList.head;
			val toId = airportToList.head;
			val typeId = airplaneTypeList.head;
			val templateIdInt = Integer.parseInt(templateId);

			if(!areUniqueSeatTypes(prices))
				throw new NonUniqueSeatTypeException();

			if(!areExistingSeatTypes(prices))
				throw new NoSuchSeatTypeException();

			if(!areCorrespondingSeats(prices, template.airplaneType))
				throw new NoCorrespondingSeatsException();

			if(count("Select Count(*) from Flighttime where (idFromAirport='" + fromId +"' and idToAirport='" + toId + "' and idAirplaneType='" + typeId + "')") != 1)
				throw new NoFlightTimePresentException();

			if(count("Select Count(*) from Distance where (idFromAirport='" + fromId +"' and idToAirport='" + toId +"')") != 1)
				throw new NoDistancePresentException();

			var query = "INSERT INTO template(`idAirline`,`idTemplate`,`idAirplaneType`,`idAirportFrom`,`idAirportTo`) VALUES('" + airlineId + "','" + templateId + "','" + typeId + "','" + fromId + "','" + toId + "')";
			(Q.u + query).execute();

			addPeriods(periods,airlineId,templateIdInt);

			for (seat <- prices) {
				var priceVal = extractPrice(seat);
				extractSeatNumbers(seat, template.airplaneType).foreach(seatNb => (Q.u + "insert into seatinstancetemplate(`idAirline`,`idTemplate`,`seatnumber`,`idAirplanetype`,`price`) values ('" + airlineId + "','" + (templateId+"") + "','" + (seatNb+"") + "','" + (typeId+"") + "','" + (priceVal+"") +"')").execute)
			}
		}
	}

	def areCorrespondingSeats(arrangement:List[SeatInstance_data], airplaneType:AirplaneType) : Boolean = {
			var query = "select count(*) from seat where (seatNumber='";
			var totalSeats = 0;
			for(seat <- arrangement) {
				var seatNbs = extractSeatNumbers(seat, airplaneType);
				seatNbs.foreach(seatNb => query += seatNb+"' or SeatNumber='");
				totalSeats += seatNbs.size;
			}
			if(arrangement.size >= 1) {
				query = query.substring(0, query.length()-16);
				query += ")";
				if(count(query) == totalSeats)
					return true;
				else
					return false;
			} else
				return true;
	}

	def areValidSeatTypes(func: (String => Boolean), param:List[SeatInstance_data]) : Boolean = {
		var result = true;
		for(seat <- param) {
			seat match {
			case SeatTypeInstances_data(seatType,_) => result = result && func(seatType);
			case _ => result = result && true;
			}
		}
		return result;
	}

	def areUniqueSeatTypes(arrangement:List[SeatInstance_data]) : Boolean = {
		return areValidSeatTypes(isUniqueSeatType,arrangement);
	}

	def isUniqueSeatType(seatType:String) : Boolean = {
		return !(count("select count(*) from seattype where name='" + seatType + "'") > 1);
	}

	def areExistingSeatTypes(arrangement:List[SeatInstance_data]) : Boolean = {
		return areValidSeatTypes(isExistingSeatType,arrangement);
	}

	def isExistingSeatType(seatType:String) : Boolean = {
		return count("select count(*) from seattype where name='" + seatType + "'") >= 1;
	}

	def extractPrice(seatInstance:SeatInstance_data) : Double = {
		var price: Price = Dollar(Filled(0),Filled(0));
	seatInstance match {
	case SeatNumberInstances_data(_,_,priceVal) => price = priceVal;
	case SeatTypeInstances_data(_,priceVal) => price = priceVal;
	}
	var result = 0d;
	price match {
	case Dollar(Filled(dollar), Filled(cent)) => result = dollar + cent/100;
	case Dollar(Empty(), Filled(cent)) => result = cent/100;
	case Dollar(Filled(dollar), Empty()) => result = dollar;
	case Euro(Filled(euro), Empty()) => result = toDollar(euro);
	case Euro(Empty(), Filled(cent)) => result = toDollar(cent/100);
	case Euro(Filled(euro), Filled(cent)) => result = toDollar(euro + cent/100);
	case _ =>
	}
	return result;
	}

	def toDollar(amount:Double) : Double = {
		return amount*1.3;
	}

	def extractSeatNumbers(seat:SeatInstance_data, airplaneType:AirplaneType) : List[Int] = {
		seat match {
		case SeatNumberInstances_data(number,Filled(amt),price) => return getSeatNumbers(airplaneType,Seat_data(Filled(number),Filled(amt),""));
		case SeatNumberInstances_data(number,Empty(),price) => return getSeatNumbers(airplaneType,Seat_data(Filled(number),Empty(),""));
		case SeatTypeInstances_data(seatType,price) => return getSeatNumbers(airplaneType, seatType);
		}
	}

	def getDateString(date: Date) : String = {
		var days = "";
		var months = "";
		var years = "";
		date match {
		case Date(d,_,_) => (if (d/10 == 0) {days = "0" + (d+"")} else {days = (d+"")})
		case _ => 
		}
		date match {
		case Date(_,m,_) => (if (m/10 == 0) {months = "0" + (m+"")} else {months = (m+"")})
		case _ =>
		}
		date match {
		case Date(_,_,y) => years = (y+"")
		case _ =>
		}
		return years + "-" + months + "-" + days;
	}

	def getPeriodQuery(period: Period_data, airlineId: String, templateId: Int) : String = {
		var dateFromString = "";
		var dateToString = "";
		var weekday = 0;
		var timeString = "";
		period match {
		case Period_data(Filled(Date(d,m,y)),_,_,_) => dateFromString = getDateString(Date(d,m,y))
		case _ =>
		}
		period match {
		case Period_data(_,Filled(Date(d,m,y)),_,_) => dateToString = getDateString(Date(d,m,y))
		case _ => 
		}
		period match {
		case Period_data(_,_,Filled(d),_) => weekday = Integer.parseInt(d);
		case _ =>
		}
		if(weekday<0 || weekday >7 )
			throw new NoSuchWeekdayException(weekday+"");
		period match {
		case Period_data(_,_,_,t) => timeString = createDurationString(t)
		case _ =>
		}
		var variablesString = "(`idAirline`,`idTemplate`";
		var valuesString = "('" + airlineId + "','" + templateId + "'";
		if (!dateFromString.equals("")) {
			variablesString = variablesString + ",`fromDate`";
			valuesString += ",'" + dateFromString + "'";		  
		}
		else {
			variablesString = variablesString + ",`fromDate`";
			valuesString += ",'" + getCurrentDate() + "'";
		}
		if (!dateToString.equals("")) {
			variablesString = variablesString + ",`toDate`";
			valuesString += ",'" + dateToString + "'";
		}
		if (weekday != 0) {
			variablesString = variablesString + ",`day`";
			valuesString += ",'" + weekday + "'";
		}
		variablesString = variablesString + ",`flightStartTime`)";
		valuesString += ",'" + timeString + "')";
		var query = "INSERT INTO period" + variablesString + " VALUES " + valuesString;
		return query;
	}

	def isExistingAirline(airlineId: String) : Boolean = {
		if (hasUniqueResult(select("count(*)", "airline","idAirline='" + airlineId + "'"))) {
			return true
		} 
		else {
			return false;
		}
	}


	def getCurrentDate() : String = {
			return (new SimpleDateFormat("yyyy-MM-dd")).format(Calendar.getInstance().getTime());
	}

	////////////////////////////////////////////////////////////////////////////////
	// AirplaneType /////////
	////////////////////////////////////////////////////////////////////////////////

	def insert(airplaneType: AirplaneType_data): Unit = {
			(Q.u + "INSERT INTO airplanetype(`name`) VALUES ('" + airplaneType.name + "')").execute;
	}

	def getInitialSeatNumber(airplaneType:AirplaneType) : Int = {
			val list =  getSeatNumbers(airplaneType);
			var defaultSeatNumber = 1;
			if(list.size >= 1){
				defaultSeatNumber =list.max + 1;
			}
			return defaultSeatNumber;
	}

	def getSeatNumbers(airplaneType: AirplaneType, seat:Seat_data) : List[Int] = {
			var defaultSeatNumber = getInitialSeatNumber(airplaneType);
			var defaultAmount = 1;
			var result = List[Int]();
			seat match {
			case Seat_data(Filled(number),_, _) => defaultSeatNumber = number;
			case _ => 
			}
			seat match {
			case Seat_data(_,Filled(amount), _) => defaultAmount = amount;
			case _ => 
			}
			while(result.size != defaultAmount) {
				result = defaultSeatNumber :: result;
				defaultSeatNumber = defaultSeatNumber + 1;
			}
			return result;
	}

	def getSeatNumbers(airplaneType: AirplaneType, seatType: String) : List[Int] = {
			var result = List[Int]();
			getAirplaneTypeIds(airplaneType).foreach(id => result = getSeatNumbers("select seatNumber from (seat JOIN seattype on seat.idSeatType=seatType.idseatType) where (name='" + seatType + "' and idAirplaneType='" + (id+"") + "')") ::: result);
			return result;
	}


	def addAirplaneType(airplaneType:AirplaneType_data, arrangement: List[Seat_data]) {
		if(airplaneType.name != null) {
			if (!airplaneType.name.matches("[a-zA-Z0-9]+"))
				throw new IllegalAirplaneTypeNameException(airplaneType.name);

			if (hasUniqueResult(select("count(*)", "airplanetype", "(name='" + airplaneType.name + "')")))
				throw new AlreadyExistingAirplaneTypeException(airplaneType.name);

			execute[AirplaneType_data](insert, airplaneType);

			Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
					driver = "com.mysql.jdbc.Driver") withSession {
				insert(arrangement,getAirplaneTypeIds(AirplaneType(Filled(airplaneType.name))).head,AirplaneType(Filled(airplaneType.name)));
			}
		}
	}

	def changeAirplaneType(airplaneFrom:AirplaneType, airplaneTo:AirplaneType) {
		//TODO
	}

	def removeAirplaneType(airplane:AirplaneType) {
		//TODO
	}

	////////////////////////////////////////////////////////////////////////////////
	// Seats ////////////
	////////////////////////////////////////////////////////////////////////////////	

	def insert(arrangement:List[Seat_data],airplaneId:Int, airplaneType:AirplaneType) = {
		for(seat <- arrangement)
			getIds("Select idSeatType from SeatType where name='" + seat.seatType +"'").foreach(seatType =>
			for(seatNumber <- getSeatNumbers(airplaneType, seat)) {
				(Q.u + "INSERT INTO seat(`idSeatType`,`idAirplaneType`,`seatNumber`) VALUES ('" + (seatType+"") + "','" + (airplaneId+"") + "','" + (seatNumber+"") + "')").execute;
			});
	}

	def addSeats(airplaneType:AirplaneType,arrangement:List[Seat_data]) {
		Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
				driver = "com.mysql.jdbc.Driver") withSession {
			var airplaneTypeIds = getAirplaneTypeIds(airplaneType);
			if(airplaneTypeIds.size > 1)
				throw new NonUniqueAirplaneTypeException();
			if(airplaneTypeIds.size ==0)
				throw new NoSuchAirplaneTypeException();
			var airplane = airplaneTypeIds.head;

			insert(arrangement,airplane,airplaneType);
		}
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
			if(hasUniqueResult(select("Count(*)","SeatType","name='" + name + "'")))
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
	// Flight /////////
	////////////////////////////////////////////////////////////////////////////////

	def insert(flight:Flight_data) = {
		if(getTemplateIds(flight.template).size > 1)
			throw new NonUniqueTemplateException();
		if(getTemplateIds(flight.template).size == 0)
			throw new NoSuchTemplateException();
		var arrivalTime = extractArrivalTime(flight);
		var airplaneTypes = extractAirplaneTypes(flight);
		var insert = "`cancelled`, `departure`";
		var values = (0+"") + ",'" + getDateTime(flight.departure) + "'";
		if(!arrivalTime.equals("")) {
			insert += ",`arrival`";
			values += ",'" + arrivalTime + "'";
		}
		if(airplaneTypes.size >= 1) {
			insert += ",`idAirplaneType`";
			airplaneTypes.foreach(id => values += ",'" + id + "'");
		}
		insert += ",`idAirline`,`idTemplate`";
		getTemplateIds(flight.template).foreach(id =>
		(Q.u + "insert into flight("+ insert +") values (" + values  + ",'" + id.idAirline +"','" + (id.idTemplate+"") +"')").execute);
	}

	def addFlight(flight:Flight_data) {
		var map = new HashMap[TemplateId,java.sql.Time]();

		execute[Flight_data](insert,flight);

		//		getTemplateIds(flight.template).foreach(id => 
		//		map.put(id,getDuration("Select duration from (template join flighttime on (idAirportFrom=idFromAirport and idAirportTo=idToAirport)) where (idAirline='"+ id.idAirline + "' and idTemplate='" + id.idTemplate + "'"))); 
	}

	def extractArrivalTime(flight:Flight_data) : String = {
			var arrivalTime = "";
			flight match {
			case Flight_data(_,_,Filled(arrival),_) => arrivalTime = getDateTime(arrival);
			case _ => 
			}
			return arrivalTime
	}

	def extractAirplaneTypes(flight:Flight_data) : List[Int] = {
			var airplaneTypes = List[Int]();
			flight match {
			case Flight_data(_,_,_,Filled(airplaneTypeVal)) => airplaneTypes = getAirplaneTypeIds(airplaneTypeVal);
			case _ => 
			}
			return airplaneTypes
	}

	def getDateTime(dateTime: DateTime) : String = {
			var time = createDurationString(Time(Filled(0),Filled(0),Filled(0)));
			dateTime match {
			case DateTime(_,Filled(timeVal)) => time = createDurationString(timeVal);
			case _ => 
			}
			return getDateString(dateTime.date) + " " + time;
	}

	def addFlight2(Flight:Flight_data, prices: List[SeatInstance_data]) {

		if(!areUniqueSeatTypes(prices))
			throw new NonUniqueSeatTypeException();

		if(!areExistingSeatTypes(prices))
			throw new NoSuchSeatTypeException();
		//TODO
		//		extractAirplaneTypes(flight);
		//		
		//		if(!areCorrespondingSeats(prices, ))
		//			throw new NoCorrespondingSeatsException();
		//		
		//		addFlight(flight);
		//		
		//		
		//		for (seat <- prices) {
		//			var priceVal = extractPrice(seat);
		//			extractSeatNumbers(seat, template.airplaneType).foreach(seatNb => (Q.u + "insert into seatinstance(`seatnumber`,`idAirplanetype`,`price`) values ('" + airlineId + "','" + (templateId+"") + "','" + (seatNb+"") + "','" + (typeId+"") + "','" + (priceVal+"") +"')").execute)
		//		}
	}

	def changeFlight(flightSelector:Flight, changeFlight:Flight_change) {

		// make flights of the given timeperiod and template(if present)
		initFlights(flightSelector);
		// select necessary flights from actual flight view
//		getFlightIds(flightSelector);
		Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
				driver = "com.mysql.jdbc.Driver") withSession {
			(Q.u + makeUpdateQuery(getFlightIds(flightSelector),changeFlight)).execute;
		}
		// make changes
		//TODO
	}

	def makeUpdateQuery(idsToUpdate:List[Int],flight:Flight_change) : String = {
		var airplaneType: AirplaneType = getAirplaneType(flight);
		var departure:DateTime = getDeparture(flight);
		var arrival:DateTime = getArrival(flight);
		var update = "update Flight set ";
		if(arrival != null) {
//		  update += 
		}
	return "";
	}
	
	def initFlights(flightSelector:Flight) {
		var timePeriod: TimePeriod = getTimePeriod(flightSelector);
	var template: Template = getTemplate(flightSelector);
	var departure:DateTime = getDeparture(flightSelector);
	var arrival:DateTime = getArrival(flightSelector);
	var templateIds = List[TemplateId]();
	
	if(template != null) 
		templateIds = getTemplateIds(template);
	else {
		//match all templates
		templateIds = getTemplateIds(Template(Empty(),Empty(),Empty(),Empty(),Empty()))
	}
	initFlights(templateIds, getMostRestrictiveTimePeriod(departure, arrival, timePeriod));
	}

	def getMostRestrictiveTimePeriod(departure:DateTime,arrival:DateTime,timePeriod:TimePeriod) : TimePeriod = {
	  //TODO could check if the departure and or arrival is contained in the given timeperiod
	  if(timePeriod != null)
	    return timePeriod;
	  if(departure != null)
	    return TimePeriod(departure,departure);
	  if(arrival != null)
	    return TimePeriod(arrival,arrival);
	  return null;
	}
	
	def initFlights(templateIds:List[TemplateId],timePeriod:TimePeriod) {
			for(Id <- templateIds) {
			  println(Id);
			  val query = "call generate_template_flights('" + Id.idAirline +"','" + (Id.idTemplate+"") + "','" + (getDateTime(timePeriod.from)) +"','" + getDateTime(timePeriod.to) +"')";
			  println(query);
			  execute(query);
			}
	}
	
	def removeFlight(flightSelector:Flight) {

	}

	////////////////////////////////////////////////////////////////////////////////
	// Database ////////////
	////////////////////////////////////////////////////////////////////////////////	

	def getTemplate(flight:Flight) : Template = {
	  flight match {
	  		case Flight1(Filled(t), _,_,_,_) => return t;
	  		case Flight2(Filled(t),_,_,_,_) => return t;
	  		case Flight3(Filled(t),_,_,_,_) => return  t;
	  		case _ => return null;
	  }
	}
	
	def getDeparture(flight:Flight) : DateTime = {
	  flight match {
	  		case Flight1(_,d,_,_,_) => return d;
	  		case Flight2(_,Filled(d),_,_,_) => return d;
	  		case Flight3(_,Filled(d),_,_,_) => return d;
	  		case _ => return null;
	  }
	}
	
	def getArrival(flight:Flight) : DateTime = {
	  flight match {
	  		case Flight1(_,_,Filled(ar),_,_) => return ar;
	  		case Flight2(_,_,ar,_,_) => return ar;
	  		case Flight3(_,_,Filled(ar),_,_) => return ar;
	  		case _ => return null;
	  }
	}
	
	def getAirplaneType(flight:Flight) : AirplaneType = {
	  flight match {
	  		case Flight1(_,_,_,Filled(ai),_) => return ai;
	  		case Flight2(_,_,_,Filled(ai),_) => return ai;
	  		case Flight3(_,_,_,Filled(ai),_) => return ai;
	  		case _ => return null;
	  }
	}
	
	def getTimePeriod(flight:Flight) : TimePeriod =  {
	  flight match {
	  		case Flight1(_,_,_,_,Filled(du)) => return du;
	  		case Flight2(_,_,_,_,Filled(du)) => return du;
	  		case Flight3(_,_,_,_,du) => return du;
	  		case _ => return null;
	  }
	}
	
	def getFlightIds(flight:Flight) : List[Int] =  {
			var template: Template = getTemplate(flight);
	var departure: DateTime = getDeparture(flight);
	var arrival: DateTime = getArrival(flight);
	var airplaneType: AirplaneType = getAirplaneType(flight);
	var timePeriod: TimePeriod = getTimePeriod(flight);
	
	var where = "";
	if(departure != null)
		where += "departure='" + getDateTime(departure) + "'";
	if(arrival != null) {
		if(!where.equals(""))
			where += " and ";
		where += "arrival='" + getDateTime(arrival) + "'";
	}
	if(airplaneType != null) {
		var ids = getAirplaneTypeIds(airplaneType);
		if(!where.equals("")&& ids.size>=1)
			where += " and (";
		ids.foreach(id =>  where += "idAirplaneType='" + id + "' or ");
		if(ids.size>=1)
			where = where.substring(0, where.length()-4);
		where += ")";
	}
	if(timePeriod != null) {
		if(!where.equals(""))
			where += " and ";
		where += "(departure >'" + getDateTime(timePeriod.from) + "' and departure <'" + getDateTime(timePeriod.to) +"')";
	}
	if(!where.equals(""))  
		where = "select idFlight from ActualFlight where " + where;
	else
		where =  "select idFlight from ActualFlight";
	println("getFlightids selector: " + where);
	return getIds(where);
	}

	def getTemplateIds(template:Template) : List[TemplateId] = {
			var result = List[TemplateId]();
			var airlineIds = List[String]();
			var flnVal = "";
			var airportFromIds = List[String]();
			var airportToIds = List[String]();
			var airplaneTypeIds = List[Int]();
			template match {
			case Template(Filled(airline),_,_,_,_) => airlineIds = getAirlineIds(airline);
			case _ => 
			}
			template match {
			case Template(_,Filled(fln),_,_,_) => flnVal = fln;
			case _ => 
			}
			template match {
			case Template(_,_,Filled(from),_,_) => airportFromIds = getAirportIds(from);
			case _ => 
			}
			template match {
			case Template(_,_,_,Filled(to),_) => airportToIds = getAirportIds(to);
			case _ => 
			}
			template match {
			case Template(_,_,_,_,Filled(airplaneType)) => airplaneTypeIds = getAirplaneTypeIds(airplaneType);
			case _ => 
			}

			var select = "";
			select+= addOr(airlineIds,"idAirline");
			select+= addOr(airportFromIds,"idAirportFrom");
			select+= addOr(airportToIds,"idAirportTo");
			select+= addOr(airplaneTypeIds.asInstanceOf[List[Integer]],"idAirplaneType");

			if(!flnVal.equals("")) {
				select += addAnd(select);
				select += "idAirline='" + getAirlineIdFromFLN(flnVal) + "'";
				select = select + addAnd(select);
				select += "idTemplate='" + getTemplateIdFromFLN(flnVal) + "'";
			}
			if(!select.equals(""))
				result = getTemplateIds("Select idAirline,idTemplate from template where " + select);
			else
				result = getTemplateIds("Select idAirline,idTemplate from template");
			return result;
	}


	def addAnd(string:String) : String = {
			var result = "";
			if(!string.equals(""))
				result = " and ";
			return result;
	}

	def addOr(list:List[Object], name:String) : String = {
			var result = "";
			if(list.size >= 1) {
				result += addAnd(result);
				result +="(";
				list.foreach(attribute => result += (name + "='" + attribute + "' or "));
				result = result.substring(0, result.length()-4); //remove last "or"
				result +=")";
			}
			return result;
	}

	def getAirlineIds(airline:Airline) : List[String] = {
			var result = List[String]();
			var name ="";
			var id = "";
			airline match {
			case Airline(_,Filled(airlineName)) => name = airlineName;
			case Airline(Filled(idAirline),_) => id = idAirline;
			case _ => 
			}
			var where ="";
			if(!name.equals("")) {
				where += "name='" + name + "'";
			}
			if(!id.equals("")) {
				where += addAnd(where);
				where += "idAirline='" + id +"'"; 
			}
			if(!where.equals(""))
				result = getCodes("select idAirline from airline where " + where);
			else
				result = getCodes("select idAirline from airline");
			return result;
	}

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
				select += "idAirport='";
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
			if(!select.equals(""))
				result = getCodes("Select idAirport from airport where " + select);
			else
				result = getCodes("Select idAirport from airport");
			return result;
	}

	def getSeatNumbers(airplaneType:AirplaneType) : List[Int] = {
			var result = List[Int]();
			val ids = getAirplaneTypeIds(airplaneType);
			for(Id <- ids) {
				result = getIds("Select seatNumber from seat where idAirplaneType='" + (Id+"") + "'") ::: result;
			}
			return result;
	}

	def getAirplaneTypeIds(airplaneType:AirplaneType) : List[Int] = {
			var name = "";
			airplaneType match {
			case AirplaneType(Filled(airplaneTypeName)) => name = airplaneTypeName;
			case AirplaneType(Empty()) => 
			}
			var result = List[Int]();
			if(!name.equals(""))
				result = getIds("Select idAirplaneType from airplaneType where name='" + name + "'");
			else
				result = getIds("Select idAirplaneType from airplaneType");
			return result;
	}

	def execute[Type](func: (Type => Unit), data: Type) {
		Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
				driver = "com.mysql.jdbc.Driver") withSession {
			func(data);
		}
	}

	def execute(query:String) {
		Database.forURL("jdbc:mysql://localhost/mydb?user=root&password=",
				driver = "com.mysql.jdbc.Driver") withSession {
			(Q.u + query).execute
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

	implicit val getSeatNumberResult = GetResult(r => SeatNumber(r.nextInt));
	def getSeatNumbers(query: String): List[Int] = {
			var result = List[Int]();
			val q = Q.queryNA[SeatNumber](query);
			result = q.first.nr :: result;
			return result;
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

	implicit val getTemplateIdResult = GetResult(r => TemplateId(r.nextString,r.nextInt));
	def getTemplateIds(query:String) : List[TemplateId] = {
			var result = List[TemplateId]();
			Q.queryNA[TemplateId](query).foreach( r => result = r :: result);
			return result;
	}

	implicit val getDurationResult = GetResult(r => Duration(r.nextTime()));
	def getDuration(query:String) : java.sql.Time = {
			val q = Q.queryNA[Duration](query);
			return q.first.duration;
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
