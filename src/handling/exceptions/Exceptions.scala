package handling.exceptions

import syntax.City

case class IllegalCityNameException(name:String) extends Exception
case class IllegalCityCodeException(code:String) extends Exception
case class AlreadyExistingCityNameException(name:String) extends Exception
case class AlreadyExistingCityShortException(short:String) extends Exception
case class NoSuchCityNameException(name:String) extends Exception
case class NoSuchCityCodeException(short:String) extends Exception
case class NotAllowedCityNameException(name:String) extends Exception
case class NotAllowedCityCodeException(short:String) extends Exception
case class NoSuchCityException(city:City) extends Exception
case class AlreadyExistingAirportName(name:String) extends Exception
case class AlreadyExistingAirportCode(short:String) extends Exception
case class NoSuchAirportName(arg:String) extends Exception
//case class NoSuchAirportName(arg:String) extends Exception



class Exceptions {

}