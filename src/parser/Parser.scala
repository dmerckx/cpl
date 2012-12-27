package parser

import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import syntax.AddCity
import syntax.CityData
import syntax.AirportData
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

case class UnknownAttribute(name:String) extends Exception
case class SkippedAttribute(name:String) extends Exception

object Parser extends StandardTokenParsers {
  type ParserType[T] = Parser[T];
  
  lexical.delimiters += ("{", "}", ",", ":")
  lexical.reserved += ("ADD","CITY", "AIRPORT", "hoihoi")

  def parseOp(): Parser[Operation]  =
    "ADD" ~> "CITY" ~> parseCityData ^^ {c => new AddCity(c)} |
    "ADD" ~> "AIRPORT" ~> parseAirportData ^^ {a => new AddAirport(a)}
            
  /*def parseCityData(): Parser[City_data] =
    "{" ~> ident ~ ":" ~ ident ~ "," ~ ident ~ ":" ~ ident <~ "}" ^^ {
      case "name"~":"~name~","~"short"~":"~short => new City_data(name,short)
      case "short"~":"~short~","~"name"~":"~name => new City_data(name,short)}*/
 
  def parseAtts(parseAtt:Parser[(String,Any)]):Parser[Map[String,Any]] = {
    parseAtt ~ "," ~ parseAtts(parseAtt) ^^ {case pair~","~map => map + pair} |
    parseAtt ^^ {pair => Map(pair)}}
  
  def select[T](key:String, map:Map[String,Any]):T = {
    println(" -- selects " + key + " from " + map);
    if(map.contains(key))
      map.get(key).get match {case t:T => t}
    else
      throw new SkippedAttribute(key)
  }
    
  def selectOpt[T](key:String, map:Map[String,Any]):Opt[T] = {
    println(" -- selectOpt " + key + " from " + map);
    if(map.contains(key))
      map.get(key).get match {case s:T => Filled(s)}
    else
      Empty[T]
  }
  
  val parseCity: Parser[City] =
    "{" ~> parseAtts(city1) <~ "}" ^^ {atts =>
    	new City1(select("name",atts))} |
    "{" ~> parseAtts(city2) <~ "}" ^^ {atts =>
    	new City2(select("short",atts))}
  val city1:Parser[(String,Any)] = {
    ident ~ ":" ~ ident ^? {	//^? here: if this fails we can backtrack to try city2
     case "name"~":"~name => ("name", name)}}
  val city2:Parser[(String,Any)] = {
    ident ~ ":" ~ ident ^^ {	//^^ here: if this fails the statement was simply incorrect
     case "short"~":"~short => ("short", short)
     case attName~":"~_ => throw new UnknownAttribute(attName)}}
  
  val parseCityData: Parser[CityData] ={
    "{" ~> parseAtts(cityData) <~ "}" ^^ {atts =>
        println(atts);
    	new CityData(select("name",atts), select("short",atts))}}
  val cityData:Parser[(String,Any)] = {
    ident ~ ":" ~ ident ^^ {
     case "name"~":"~name => ("name", name)
     case "short"~":"~short => ("short", short)
     case name~":"~_ => throw new UnknownAttribute(name)}}

  val parseAirportData: Parser[AirportData] =
    "{" ~> parseAtts(airportData) <~ "}" ^^ {atts =>
        new AirportData(select("city",atts), select("name",atts), select("short",atts))}
  val airportData:Parser[(String,Any)] = 
    ident ~ ":" ~ ident ^^ {
     case "name"~":"~name => ("name", name)
     case "short"~":"~short => ("short", short)
     case name~":"~_ => throw new UnknownAttribute(name)} |
    ident ~ ":" ~ parseCity ^^ {
     case "city"~":"~city => ("city", city)
     case name~":"~_ => throw new UnknownAttribute(name)} 
  
  def parse (s: String) = {
    val tokens = new lexical.Scanner(s)
    printTokens(tokens);
    println(phrase(parseOp));
    phrase(parseOp)(tokens)
  }
  
  def printTokens(sc: lexical.Scanner):Unit = {
    if(!sc.atEnd){
      println(sc.first);
      printTokens(sc.rest);
    }
  }
  
  def test (exprString : String) = {
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
  }

  def main (args: Array[String]) = {
     test ("ADD AIRPORT {name:BRUSSELS, city:{short:deef}, short:BRU}")
  }   
}
  
