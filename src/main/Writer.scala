package main
import handling.exceptions._
import syntax.Operation
import syntax._


object Writer {
	def successWriter(operation: Operation): String = {
		var success = "";
		operation match {
			case AddCity(data) => 
				success = "City added succesfully"
			case ChangeCity(city, to) => 
				success = "The city changes were made succesfully"
			case RemoveCity(city) => 
				success = "City removed succesfully"
	
			//AIRPORT
			case AddAirport(airport) =>
				success = "Airport added succesfully"
			case ChangeAirport(airportFrom, airportTo) =>
				success = "The airport changes were made succesfully"
			case RemoveAirport(airport) => 
				success = "Airport removed succesfully"
	
			//AIRPLANETYPE
			case AddAirplaneType(airplaneType , arrangement) =>
				success = "Airplane type and its seat configuration added succesfully"
			case ChangeAirplaneType(airplaneTypeFrom, airplaneTypeTo) =>
				success = "Airplane type changes were made succesfully"
			case RemoveAirplaneType(airplaneType) =>
				success= "Airplane type removed succesfully"
	
			//AIRLINE
			case AddAirline(airline) => 
				success = "Airline added succesfully"
			case ChangeAirline(airlineFrom, airlineTo) =>
				success = "Airline changes were made succesfully"
			case RemoveAirline(airline) => 
				success = "Airline removed succesfully"
	
			//SEATTYPE
			case AddSeatType(seatType) => 
				success = "Seat type added succesfully"
			case RemoveSeatType(seatType) =>
				success = "Seat type removed succesfully"
			case ChangeSeatType(from, to) =>
				success = "Seat type changes were made succesfully"
	
			//DISTANCE
			case AddDist(distance) => 
				success = "Distance added succesfully"
	
			//FLIGHTTIME
			case AddFlightTime(flightTime) =>
				success = "Flighttime added succesfully"
	
			//Flight
			case AddFlight(flight) => 
				success = "Flight added succesfully"
			case AddFlight2(flight, prices) =>
				success = "Flight and pricing information added succesfully"
			case ChangeFlight(flightFrom, flightTo) => 
				success = "Flight changes were made succesfully"
			case RemoveFlight(flight) => 
				success = "Flight removed succesfully"
	
			//Template
			case AddTemplate(template, prices, periods) =>
				success = "Template with pricing and periods added succesfully"
			case AddTemplatePeriods(template,periods) => 
				success = "Template periods added succesfully"
			case ChangeTemplate(template, change) => 
				success = "Template changes were made succesfully"
			case AddSeats(airplaneType, seat_datas) => 
				success = "Airplane type with seats data added succesfully"
			
			//SEAT INSTANCES
			case ChangeFlightSeatInstancesTo(flight,seatInstances_data) =>
				success = "Seat instances changes to a flight were made succesfully"
			case ChangeTemplateSeatInstancesTo(template,seatInstances_data) => 
				success = "Seat instances changes to a template were made succesfully"
		}
		return success;
	}
	
	def indent(input:String) : String = {
		//input.split('\n');
		var output = "";
		input.trim().split('\n').map(line => s"\t$line").foreach(line => output = output + line);
		return output;
	}


	def errorWriter(exception: Exception): String = {
		var error = "";
		exception match {
			case IllegalCityNameException(name) =>
				error = s"Illegal city name: $name"
			case AlreadyExistingCityNameException(name) =>
				error = s"There already exists a city with name $name"
			case NoSuchCityNameException(name) => 
				error = s"There exists no city with name $name"
			case NoSuchCityException(name) => 
				error =s"There exists no city with name $name"
			case NonUniqueCityNameException(name) => 
				error =s"The city with name $name is not unique"
			case ExistingReferenceException(name) => 
				error =s"The city with name $name has associated airports"
			
			//AIRPORT
			case IllegalAirportNameException(name) => 
				error = s"Illegal airport name: $name"
			case AlreadyExistingAirportException(name, short, cityName) =>
				error = s"The airport with name $name , code $short and cityName $cityName already exists"
			case NoSuchAirportNameException(arg) => 
				error = s"The airport with name $arg doesn't exist"
			case NoSuchAirportCodeException(arg) => 
				error = s"The airport with code $arg doesn't exist"
			case NotAllowedAirportNameException(arg) => 
				error = s"An airport with name $arg isn't allowed"
			case NotAllowedAirportCodeException(arg) => 
				error = s"An airport with code $arg isn't allowed"
			case NonUniqueFromAirportAirportException(from) => 
				error = s"Trying to change to multiple departure airports"
			case NoSuchFromAirportAirportException(from) => 
				error = s"Tring to change the departure airport to a non existing airport"
			case NonUniqueToAirportAirportException(from) => 
				error = s"Trying to change to multiple arrival airports"
			case NoSuchToAirportAirportException(from) => 
				error = s"Tring to change the arrival airport to a non existing airport"
			//case NotAllowedAirportCode(arg) => 
			case IllegalAirportShortException(arg) => 
				error = s"Illegal airport code: $arg"
			
			//AIRPLANETYPE
			case IllegalAirplaneTypeNameException(name) => 
				error = s"Illegal airplane type name: $name"
			case AlreadyExistingAirplaneTypeException(name) => 
				error = s"The airplanetype with name: $name alreay exists"
			
			//AIRLINE
			case IllegalAirlineNameException(name) => 
				error = s"Illegal airline name: $name"
			case AlreadyExistingAirlineNameException(name) =>
				error = s"The airline with name $name already exists"
			case AlreadyExistingAirlineCodeException(short) => 
				error = s"The airline code $short already exists"
			case IllegalAirlineCodeException(short) => 
				error = s"Illegal airline code: $short"
			
			//SEATTYPE
			case IllegalSeatTypeException(name) => 
				error = s"Illegal seat type name: $name"
			case AlreadyExistingSeatTypeException(name) =>
				error = s"The seat type with name $name already exists"
			case NoSuchSeatTypeException(prices) => 
				error = s"There is a non existing seat type"
			case NonUniqueSeatTypeException(prices) => 
				error = s"There is a seat type which is not unique"
			//DISTANCE
			case IllegalDistanceException(dist:Int) => 
				error = s"Illegal distance: $dist"
			
			//FLIGHTTIME
			case NoDurationException(duration) => 
				error = s"The flight time duration is empty"
			case IllegalDurationException(duration) => 
				error = s"Illegal duration of the flight time"
			
			//TEMPLATE
			case NoSuchTemplateException(template) => 
				error = s"The template doesn't exist"
			case NonUniqueTemplateException(template) =>
				error = s"The template is not unique"
			case IllegalFLNException(fln) => 
				error = s"Illegal flight number: $fln"
			case NoSuchAirlineException(id) => 
				error = s"The airport with $id doesn't exist"
			case NoSuchWeekdayException(day) => 
				error = s"Illegal weekday: $day"
			case NoSuchFromAirportException(template_data) =>
				error = s"The departure airport doesn't exist"
			case NoSuchToAirportException(template_data) => 
				error = s"The arrival airport doesn't exist"
			case NonUniqueFromAirportException(template_data) =>
				error = s"The departure airport isn't unique"
			case NonUniqueToAirportException(template_data) =>
				error = s"The arrival airport isn't unique"
			case NoDistancePresentException(fromId,toId,typeId) =>
				error = s"The distance from airport $fromId to airport $toId with airportType $typeId doesn't exist"
			case NoFlightTimePresentException(fromId,toId,typeId) => 
				error = s"The flight time from airport $fromId to aiport $toId with airportType $typeId doesn't exist"
			case NoCorrespondingSeatsException(prices,airplaneType) => 
				error = s"The given seats don't correspond to the seats of the flight"
			case NonUniqueNewTemplateException(change_change) => 
				error = s"Trying to change to multiple templates; change information matches multiple instances"
			case NoSuchNewTemplateException(change_change) => 
				error = s"Trying to change to nothing; change information matches no instances"
			case NonUniqueAirplaneTypeTemplateException(template_data) => 
				error = s"The airplaneType corresponding to the template data is not unique"
			case NoSuchAirplaneTypeTemplateException(template_data) => 
				error = s"The airplaneType corresponding to the template data doesn't exist"
			
			//SEATS
			case NonUniqueAirplaneTypeException(airplaneType) =>
				error = s"The airplane type selector information corresponds to multiple airplane types"
			case NoSuchAirplaneTypeException(airplaneType) => 
				error = s"The airplane type selector information corresponds to no airplane type"
			
			//FLIGHT
			case NonUniqueNewAirplaneTypeException(flightChange) => 
				error = s"The change flight information corresponds to multiple airplane types"
			case NoSuchNewAirplaneTypeException(flightChange) => 
				error = s"The change flight information corresponds to no airplane type"
			case IllegalArrivalTimeException(arrival,departure) => 
				error = s"The arrival time is before the departure time"
			case IllegalArrivalTimeDirectionException(id,time,direction) =>
				error = s"Trying to put an arrival time before a departure time or vice versa"
			
			//SEATINSTANCES
			case TemplateSeatNumberOutOfRangeException(template,seatInstances) => 
				error = s"The change seat instances information contains seatnumbers that are out of the range of the seats corresponding to the template"
			case FlightSeatNumberOutOfRangeException(flight,seatInstances) =>
				error = s"The change seat instances information contains seatnumbers that are out of the range of the seats corresponding to the flight"
			case OverlappingSeatInstancesException(seatInstances) => 
				error = s"The change seat instances information contains overlapping seat numbers"
			case SameSeatTypesException(seatInstances) => 
				error = s"The change seat instances information contains seats of the same seat type"
			
			
			case IllegalAmountOfSpecifiedSeatsException(seat,amt) => 
				error = s"Illegal amount of specified seats"
			case IllegalAmountOfSpecifiedSeatsException2(seat,amt) =>
				error = s"Illegal amount of specified seats"
			case e =>
				error = "An unknown error has occured." + e;
				if (error == "")
					error = "I still have to write a textual explanation for this error"
		}
		return error;
	}
}