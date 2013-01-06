package handling.exceptions

//CITY
case class IllegalCityNameException(name:String) extends Exception
case class AlreadyExistingCityNameException(name:String) extends Exception
case class NoSuchCityNameException(name:String) extends Exception
case class NoSuchCityException(name:String) extends Exception


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

//DISTANCE
case class IllegalDistanceException(dist:Int) extends Exception

//FLIGHTTIME
case class NoDurationException() extends Exception
case class IllegalDurationException() extends Exception

class Exceptions {

}