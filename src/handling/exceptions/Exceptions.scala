package handling.exceptions

case class IllegalCityNameException(arg:String) extends Exception
case class IllegalCityCodeException(arg:String) extends Exception
case class AlreadyExistingCityNameException(arg:String) extends Exception
case class AlreadyExistingCityShortException(arg:String) extends Exception
case class NoSuchCityException(arg:String) extends Exception
case class NotAllowedCityNameException(arg:String) extends Exception
case class NotAllowedCityCodeException(arg:String) extends Exception

//case class AlreadyExistingCityNameException(arg:String) extends Exception



class Exceptions {

}