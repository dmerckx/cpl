package handling.exceptions

import syntax.Time
import syntax.SeatInstance_data
import syntax.AirplaneType
import syntax.Flight_change
import syntax.Template
import syntax.SeatNumberInstances_data
import syntax.Template_change
import syntax.Flight
import syntax.DateTime
import syntax.Seat_data
import syntax.Template_data
import syntax.Airport

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
case class NoSuchAirportNameException(arg:String) extends Exception
case class NoSuchAirportCodeException(arg:String) extends Exception
case class NotAllowedAirportNameException(arg:String) extends Exception
case class NotAllowedAirportCodeException(arg:String) extends Exception
case class NonUniqueFromAirportAirportException(from: Airport) extends Exception
case class NoSuchFromAirportAirportException(from: Airport) extends Exception
case class NonUniqueToAirportAirportException(from: Airport) extends Exception
case class NoSuchToAirportAirportException(from: Airport) extends Exception
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
case class NoSuchTemplateException(template: Template) extends Exception
case class NonUniqueTemplateException(template: Template) extends Exception
case class IllegalFLNException(fln: String) extends Exception
case class NoSuchAirlineException(id: String) extends Exception
case class NoSuchWeekdayException(day: String) extends Exception
case class NoSuchFromAirportException(template: Template_data) extends Exception
case class NoSuchToAirportException(template: Template_data) extends Exception
case class NonUniqueFromAirportException(template: Template_data) extends Exception
case class NonUniqueToAirportException(template: Template_data) extends Exception
case class NoDistancePresentException(fromId: String,toId: String,typeId: Int) extends Exception
case class NoFlightTimePresentException(fromId: String,toId: String,typeId: Int) extends Exception
case class NoCorrespondingSeatsException(prices: List[SeatInstance_data],airplaneType: AirplaneType) extends Exception
case class NonUniqueNewTemplateException(change: Template_change) extends Exception
case class NoSuchNewTemplateException(change: Template_change) extends Exception
case class NonUniqueAirplaneTypeTemplateException(template: Template_data) extends Exception
case class NoSuchAirplaneTypeTemplateException(template: Template_data) extends Exception

//SEATS
case class NonUniqueAirplaneTypeException(airplaneType: AirplaneType) extends Exception
case class NoSuchAirplaneTypeException(airplaneType: AirplaneType) extends Exception

//FLIGHT
case class NonUniqueNewAirplaneTypeException(flightChange: Flight_change) extends Exception
case class NoSuchNewAirplaneTypeException(flightChange: Flight_change) extends Exception
case class IllegalArrivalTimeException(arrival: DateTime,departure: DateTime) extends Exception
case class IllegalArrivalTimeDirectionException(id: Int,time: DateTime,direction: Boolean) extends Exception

//SEATINSTANCES
case class TemplateSeatNumberOutOfRangeException(template: Template,seatInstances: SeatNumberInstances_data) extends Exception
case class FlightSeatNumberOutOfRangeException(flight: Flight,seatInstances: SeatNumberInstances_data) extends Exception
case class OverlappingSeatInstancesException(seatInstances: List[SeatInstance_data]) extends Exception
case class SameSeatTypesException(seatInstances: List[SeatInstance_data]) extends Exception


case class IllegalAmountOfSpecifiedSeatsException(seat: SeatInstance_data,amt: Int) extends Exception
case class IllegalAmountOfSpecifiedSeatsException2(seat: Seat_data,amt: Int) extends Exception
class Exceptions {
}
