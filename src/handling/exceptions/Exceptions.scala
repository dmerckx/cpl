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


class Exceptions {

}