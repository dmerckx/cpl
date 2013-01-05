package parser

import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import syntax._

sealed abstract class ParseRes;
case class PSucces(op:Operation) extends ParseRes;
case class PFail(msg:String) extends ParseRes;

case class UnknownAttribute(name:String) extends Exception
case class SkippedAttribute(name:String) extends Exception
case class TooLittleAttributes() extends Exception
case class DoubleAttribute() extends Exception

//TODO: use custom Lexical instead of StdLexical
object Parser extends StandardTokenParsers {
  type ParserType[T] = Parser[T];
  
  lexical.delimiters += ("{", "}", ",", ":", "[", "]")
  lexical.reserved += ("ADD","CITY", "AIRPORT", "TO")
  
   /**
   * Parse a list of attributes
   */
  def parseAtts(p:Parser[(String,Any)]):Parser[Map[String,Any]] =
    "{" ~> parseAttsRec(p) <~ "}" ^^ {atts =>
      if(atts.size == 0) throw new TooLittleAttributes()
      atts}
    
  /**
   * Parse a list of attributes recursively
   */
  def parseAttsRec(parseAtt:Parser[(String,Any)]):Parser[Map[String,Any]] = {
    parseAtt ~ "," ~ parseAttsRec(parseAtt) ^^ {
      case pair~","~map =>
        if(map.contains(pair._1)) throw new DoubleAttribute()
        map + pair} |
    parseAtt ^^ {pair => Map(pair)}}
  
  /**
   * Select a value from a map. Throws an error if the value could not be found.
   */
  def sel[T](key:String, map:Map[String,Any]):T = {
    println(" -- selects " + key + " from " + map);
    if(map.contains(key))
      map.get(key).get match {case t:T => t}
    else
      throw new MatchError("")
  }
  
  /**
   * Select an optional value from a map.
   */
  def selOpt[T](key:String, map:Map[String,Any]):Opt[T] = {
    println(" -- selectOpt " + key + " from " + map);
    if(map.contains(key))
      map.get(key).get match {case s:T => Filled(s)}
    else
      Empty[T]
  }
  
  /**
   * Parse a type
   */
  def parseType[T <: Type](attName:String, typeParser:Parser[T]):Parser[(String, T)] =
    ident ~ ":" ~ typeParser ^^ {
    case attName~":"~typ => (attName, typ)}
  
  /**
   * Parse a string
   */
  def parseStr(attName:String):Parser[(String, String)] =
    ident ~ ":" ~ ident ^^ {
    case attName~":"~str => (attName, str)} |
    ident ~ ":" ~ stringLit ^^ {
    case attName~":"~str => (attName, str)}
  
  def parseInt(attName:String):Parser[(String, Int)] =
    ident ~ ":" ~ numericLit ^^ {
    case attName~":"~int => (attName, int.toInt)}
    
  def parseIntList(attName:String):Parser[(String, List[Int])] = {
    def par:Parser[List[Int]] = {
      numericLit ~ "," ~ par ^^ {case int~","~rest => int.toInt :: rest} |
      numericLit ^^ {case int => List(int.toInt)}
    }
    
    ident <~ ":" <~ "[" <~ "]" ^^ {case attName => (attName, List())} |
    ident ~ ":" ~ "[" ~ par ~ "]" ^^ {case attName~":"~"["~list~"]" => (attName, list)} 
  }
  
  
  // ----- ALL THE ACTUAL OPERATORS ----- //
    
  def parseOp(): Parser[Operation]  =
    "ADD" ~> "CITY" ~> parseCityData ^^ {c => AddCity(c)} |
    "CHANGE" ~> "CITY" ~> parseCity ~ "TO" ~ parseCity ^^ {case f~"TO"~t => ChangeCity(f, t)} |
    "REMOVE" ~> "CITY" ~> parseCity ^^ {c => RemoveCity(c)} |
    "ADD" ~> "AIRPORT" ~> parseAirportData ^^ {a => new AddAirport(a)} |
    "CHANGE" ~> "AIRPORT" ~> parseAirport ~ "TO" ~ parseAirport ^^ {case f~"TO"~t => ChangeAirport(f,t)}
    "REMOVE" ~> "AIRPORT" ~> parseAirport ^^ {a => new RemoveAirport(a)}
    
  // ----- ALL THE ACTUAL TYPES ----- //
  
  val cityAtts:Parser[(String,Any)] =
    parseStr("name") | 
    parseInt("id")
  val parseCity: Parser[City] =
    parseAtts(cityAtts) ^^ {as => City(selOpt[String]("name",as), selOpt[Int]("id",as))}
  val parseCityData: Parser[City_data] ={
    parseAtts(cityAtts) ^^ {as => new City_data(sel[String]("name",as), selOpt[Int]("short",as))}}
  
  val airportAtts:Parser[(String,Any)] = 
    parseStr("name") |
    parseStr("short") |
    parseType("city", parseCity)
  val parseAirport: Parser[Airport] =
    parseAtts(airportAtts) ^^ {as => Airport(selOpt("name",as), selOpt("city",as), selOpt("short",as))}
  val parseAirportData: Parser[Airport_data] =
    parseAtts(airportAtts) ^^ {as => Airport_data(sel("city",as), sel("name",as), sel("short",as))}
    
  val prices1:Parser[(String,Any)] =
    parseInt("price") |
    parseIntList("seats") 
  val prices2:Parser[(String,Any)] =
    parseInt("price") 
  val parsePrices: Parser[Prices] = 
    parseAtts(prices1) ^? {case as => Prices1(sel("price",as), sel("seats", as))}
    parseAtts(prices2) ^^ {as => Prices2(sel("price",as), sel("type", as))}
  
  
  /**
   * Used for tests
   */
  def parseType(s:String, typeName:String):Type ={
    val tokens = new lexical.Scanner(s)
    printTokens(tokens);
    
    val parseFunc:Parser[Type] = typeName match {
      case "city" => parseCity
      case "cityData" => parseCityData
      case "airport" => parseAirport
      case "airportData" => parseAirportData
      case "prices" => parsePrices
    }
    phrase(parseFunc)(tokens) match {
      case Success(typ, _) => typ
    }
  } 
  
  def parse (s: String):ParseRes = {
    val tokens = new lexical.Scanner(s)
    printTokens(tokens);
    try{
	    phrase(parseOp)(tokens) match {
	     	case Success(op, _) => PSucces(op)
	     	case Failure(msg, n) => PFail(msg)
	     	case Error(msg, _) => PFail(msg)
	    }
    }
    catch {
       case UnknownAttribute(n) => PFail("unknown attribute: " + n)
       case SkippedAttribute(n) => PFail("skipped attribute: " + n)
    }
  }
  
  def printTokens(sc: lexical.Scanner):Unit = {
    if(!sc.atEnd){
      println(sc.first);
      printTokens(sc.rest);
    }
  }

  def main (args: Array[String]) = {
    println("hello"); 
  }   
}
  