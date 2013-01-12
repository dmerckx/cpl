package handling.exceptions

import syntax.Time
import syntax.SeatInstance_data
import syntax.AirplaneType

//CITY
case class IllegalCityNameException(name:String) extends Exception
case class AlreadyExistingCityNameException(name:String) extends Exception
case class NoSuchCityNameException(name:String) extends Exception
case class NoSuchCityException(name:String) extends Exception
case class NonUniqueCityNameException(name: String) extends Exception
case class ExistingReferenceException(name: String) extends Exception

//case class NotAllowedCityNameException(name:String) extends Exception
//case class NotAllowedCityCodeException(short:String) extends Exception


//AIRPORT
case class IllegalAirportNameException(name:String) extends Exception
case class AlreadyExistingAirportException(name:String, short:String, cityName:String) extends Exception
case class NoSuchAirportName(arg:String) extends Exception
case class NoSuchAirportCode(arg:String) extends Exception
case class NotAllowedAirportName(arg:String) extends Exception
case class NotAllowedAirportCode(arg:String) extends Exception
//case class NotAllowedAirportCode(arg:String) extends Exception
case class IllegalAirportShortException(arg:String) extends Exception

//AIRPLANETYPE
case class IllegalAirplaneTypeNameException(name:String) extends Exception
case class AlreadyExistingAirplaneTypeException(name:String) extends Exception

//AIRLINE
case class IllegalAirlineNameException(name:String) extends Exception
case class AlreadyExistingAirlineNameException(name:String) extends Exception
case class AlreadyExistingAirlineCodeException(short:String) extends Exception
case class IllegalAirlineCodeException(short:String) extends Exception

//SEATTYPE
case class IllegalSeatTypeException(name:String) extends Exception
case class AlreadyExistingSeatTypeException(name:String) extends Exception
case class NoSuchSeatTypeException(prices: List[SeatInstance_data]) extends Exception
case class NonUniqueSeatTypeException(prices: List[SeatInstance_data]) extends Exception
//DISTANCE
case class IllegalDistanceException(dist:Int) extends Exception

//FLIGHTTIME
case class NoDurationException(duration: Time) extends Exception
case class IllegalDurationException(duration: Time) extends Exception

//TEMPLATE
case class NoSuchTemplateException() extends Exception
case class NonUniqueTemplateException() extends Exception
case class IllegalFLNException(fln: String) extends Exception
case class NoSuchAirlineException(id: String) extends Exception
case class NoSuchWeekdayException(day: String) extends Exception
case class NoSuchFromAirport() extends Exception
case class NoSuchToAirport() extends Exception
case class NonUniqueFromAirport() extends Exception
case class NonUniqueToAirport() extends Exception
case class NonUniqueAirplaneType() extends Exception
case class NoSuchAirplaneType() extends Exception
case class NoDistancePresentException(fromId: String,toId: String,typeId: Int) extends Exception
case class NoFlightTimePresentException(fromId: String,toId: String,typeId: Int) extends Exception
case class NoCorrespondingSeatsException(prices: List[SeatInstance_data],airplaneType: AirplaneType) extends Exception

//SEATS
case class NonUniqueAirplaneTypeException(airplaneType: AirplaneType) extends Exception
case class NoSuchAirplaneTypeException(airplaneType: AirplaneType) extends Exception

//FLIGHT
case class NonUniqueNewAirplaneTypeException() extends Exception
case class NoSuchNewAirplaneTypeException() extends Exception
case class IllegalArrivalTimeException() extends Exception

//SEATINSTANCES
case class SeatNumberOutOfRangeException() extends Exception
case class OverlappingSeatInstancesException() extends Exception
case class SameSeatTypesException() extends Exception


case class IllegalAmountOfSpecifiedSeatsException() extends Exception
class Exceptions {
}
