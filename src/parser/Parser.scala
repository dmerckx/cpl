package parser

import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import syntax.AddCity
import syntax.Operation
import syntax.Type
import syntax.Opt
import syntax.Empty
import syntax.Filled
import syntax.AddAirport
import syntax.City
import syntax.City
import syntax.City1
import syntax.City2
import syntax.AddAirport
import syntax.Airport_data
import syntax.City_data

sealed abstract class ParseRes;
case class PSucces(op:Operation) extends ParseRes;
case class PFail(msg:String) extends ParseRes;

case class UnknownAttribute(name:String) extends Exception
case class SkippedAttribute(name:String) extends Exception

object Parser extends StandardTokenParsers {
  type ParserType[T] = Parser[T];
  
  lexical.delimiters += ("{", "}", ",", ":")
  lexical.reserved += ("ADD","CITY", "AIRPORT", "hoihoi")

  def parseOp(): Parser[Operation]  =
    "ADD" ~> "CITY" ~> parseCityData ^^ {c => new AddCity(c)} |
    "ADD" ~> "AIRPORT" ~> parseAirportData ^^ {a => new AddAirport(a)}
            
 
  def parseAtts(parseAtt:Parser[(String,Any)]):Parser[Map[String,Any]] = {
    parseAtt ~ "," ~ parseAtts(parseAtt) ^^ {case pair~","~map => map + pair} |
    parseAtt ^^ {pair => Map(pair)}}
  
  def select[T](key:String, map:Map[String,Any]):T = {
    println(" -- selects " + key + " from " + map);
    if(map.contains(key))
      map.get(key).get match {case t:T => t}
    else
      throw new MatchError("hoi")
  }
    
  def selectOpt[T](key:String, map:Map[String,Any]):Opt[T] = {
    println(" -- selectOpt " + key + " from " + map);
    if(map.contains(key))
      map.get(key).get match {case s:T => Filled(s)}
    else
      Empty[T]
  }
  
  val parseCity: Parser[City] =
    "{" ~> parseAtts(city) <~ "}" ^^ {atts =>
      if(!atts.contains("name")) City2(select("short",atts))
      else City1(select("name",atts), selectOpt("short",atts))}
  val city:Parser[(String,Any)] = {
    ident ~ ":" ~ ident ^? {
     case "name"~":"~name => ("name", name)
     case "short"~":"~short => ("short", short)}}
  
  val parseCityData: Parser[City_data] ={
    "{" ~> parseAtts(cityData) <~ "}" ^^ {atts =>
        println(atts);
    	new City_data(select("name",atts), select("short",atts))}}
  val cityData:Parser[(String,Any)] = {
    ident ~ ":" ~ ident ^^ {
     case "name"~":"~name => ("name", name)
     case "short"~":"~short => ("short", short)
     case name~":"~_ => throw new UnknownAttribute(name)}}

  val parseAirportData: Parser[Airport_data] =
    "{" ~> parseAtts(airportData) <~ "}" ^^ {atts =>
        new Airport_data(select("city",atts), select("name",atts), select("short",atts))}
  val airportData:Parser[(String,Any)] = 
    ident ~ ":" ~ ident ^^ {
     case "name"~":"~name => ("name", name)
     case "short"~":"~short => ("short", short)
     case name~":"~_ => throw new UnknownAttribute(name)} |
    ident ~ ":" ~ parseCity ^^ {
     case "city"~":"~city => ("city", city)
     case name~":"~_ => throw new UnknownAttribute(name)} 
  
  /*def parse (s: String) = {
    val tokens = new lexical.Scanner(s)
    printTokens(tokens);
    println(phrase(parseOp));
    phrase(parseOp)(tokens)
  }*/
  
  def parse (s: String):ParseRes = {
    val tokens = new lexical.Scanner(s)
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
  
  /*def test (exprString : String) = {
     try{
       parse (exprString) match {
         case Success(op, _) => println("Operation: " + op)
         case Failure(msg, n) =>
           println("Failure: " + msg + " next: " + n)
           println("Tokens left");
           printTokens(n.asInstanceOf[lexical.Scanner])
         case Error(msg, _) => println("Error: " + msg)
       }
     }
     catch{
       case UnknownAttribute(n) => println("Unkown attribute used:" + n);
       case SkippedAttribute(n) => println("Attribute was not provided:" + n)
     }
  }*/

  def main (args: Array[String]) = {
    val x = 5
    x match {
      case 2 => 
    }
    println("hello"); 
    //test ("ADD AIRPORT {name:BRUSSELS, city:{short:deef}, short:BRU}")
  }   
}
  
