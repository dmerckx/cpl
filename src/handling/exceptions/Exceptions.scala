package handling.exceptions

import syntax.City

case class IllegalCityNameException(arg:String) extends Exception
case class IllegalCityCodeException(arg:String) extends Exception
case class AlreadyExistingCityNameException(arg:String) extends Exception
case class AlreadyExistingCityShortException(arg:String) extends Exception
case class NoSuchCityNameException(arg:String) extends Exception
case class NoSuchCityCodeException(arg:String) extends Exception
case class NotAllowedCityNameException(arg:String) extends Exception
case class NotAllowedCityCodeException(arg:String) extends Exception
case class NoSuchCityException(city:City) extends Exception

//case class AlreadyExistingCityNameException(arg:String) extends Exception



class Exceptions {

}