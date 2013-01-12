package main
import handling.exceptions._
import syntax.Operation

object Writer {
	def successWriter(operation: Operation): String = {
		var success = "";
		operation match {
			case _ => success = "You are successful. I don't know at what.";
		}
		return success;
	}

	def errorWriter(exception: Exception): String = {
		var error = "";
		exception match {
			case IllegalCityNameException(name) =>
			case AlreadyExistingCityNameException(name) => 
			case NoSuchCityNameException(name) => 
			case NoSuchCityException(name) => 
			case NonUniqueCityNameException(name) => 
			case ExistingReferenceException(name) => 
			
			//case NotAllowedCityNameException(name) => 
			//case NotAllowedCityCodeException(short) => 
			
			
			//AIRPORT
			case IllegalAirportNameException(name) => 
			case AlreadyExistingAirportException(name, short, cityName) => 
			case NoSuchAirportNameException(arg) => 
			case NoSuchAirportCodeException(arg) => 
			case NotAllowedAirportNameException(arg) => 
			case NotAllowedAirportCodeException(arg) => 
			case NonUniqueFromAirportAirportException(from) => 
			case NoSuchFromAirportAirportException(from) => 
			case NonUniqueToAirportAirportException(from) => 
			case NoSuchToAirportAirportException(from) => 
			//case NotAllowedAirportCode(arg) => 
			case IllegalAirportShortException(arg) => 
			
			//AIRPLANETYPE
			case IllegalAirplaneTypeNameException(name) => 
			case AlreadyExistingAirplaneTypeException(name) => 
			
			//AIRLINE
			case IllegalAirlineNameException(name) => 
			case AlreadyExistingAirlineNameException(name) => 
			case AlreadyExistingAirlineCodeException(short) => 
			case IllegalAirlineCodeException(short) => 
			
			//SEATTYPE
			case IllegalSeatTypeException(name) => 
			case AlreadyExistingSeatTypeException(name) => 
			case NoSuchSeatTypeException(prices) => 
			case NonUniqueSeatTypeException(prices) => 
			//DISTANCE
			case IllegalDistanceException(dist:Int) => 
			
			//FLIGHTTIME
			case NoDurationException(duration) => 
			case IllegalDurationException(duration) => 
			
			//TEMPLATE
			case NoSuchTemplateException(template) => 
			case NonUniqueTemplateException(template) => 
			case IllegalFLNException(fln) => 
			case NoSuchAirlineException(id) => 
			case NoSuchWeekdayException(day) => 
			case NoSuchFromAirportException(template_data) => 
			case NoSuchToAirportException(template_data) => 
			case NonUniqueFromAirportException(template_data) => 
			case NonUniqueToAirportException(template_data) => 
			case NoDistancePresentException(fromId,toId,typeId) => 
			case NoFlightTimePresentException(fromId,toId,typeId) => 
			case NoCorrespondingSeatsException(prices,airplaneType) => 
			case NonUniqueNewTemplateException(change_change) => 
			case NoSuchNewTemplateException(change_change) => 
			case NonUniqueAirplaneTypeTemplateException(template_data) => 
			case NoSuchAirplaneTypeTemplateException(template_data) => 
			
			//SEATS
			case NonUniqueAirplaneTypeException(airplaneType) => 
			case NoSuchAirplaneTypeException(airplaneType) => 
			
			//FLIGHT
			case NonUniqueNewAirplaneTypeException(flightChange) => 
			case NoSuchNewAirplaneTypeException(flightChange) => 
			case IllegalArrivalTimeException(arrival,departure) => 
			case IllegalArrivalTimeDirectionException(id,time,direction) => 
			
			//SEATINSTANCES
			case TemplateSeatNumberOutOfRangeException(template,seatInstances) => 
			case FlightSeatNumberOutOfRangeException(flight,seatInstances) => 
			case OverlappingSeatInstancesException(seatInstances) => 
			case SameSeatTypesException(seatInstances) => 
			
			
			case IllegalAmountOfSpecifiedSeatsException(seat,amt) => 
			case IllegalAmountOfSpecifiedSeatsException2(seat,amt) => 
			case e =>
				error = "An unknown error has occured." + e;
				if (error == "")
					error = "I still have to write a textual explanation for this error"
		}
		return error;
	}
}