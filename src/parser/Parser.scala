//package parser
//
//import scala.util.parsing.combinator.syntactical.StandardTokenParsers
//import syntax._
//
//sealed abstract class ParseRes;
//case class PSucces(op:Operation) extends ParseRes;
//case class PFail(msg:String) extends ParseRes;
//
//case class UnknownAttribute(name:String) extends Exception
//case class SkippedAttribute(name:String) extends Exception
//case class TooLittleAttributes() extends Exception
//case class DoubleAttribute() extends Exception
//
////TODO: use custom Lexical instead of StdLexical (remove 'comments functionality' etc)
////TODO: remove prints
//object Parser extends StandardTokenParsers {
//  type ParserType[T] = Parser[T];
//  type Att = Parser[(String,Any)]
//  
//  
//   /**
//   * Parse a list of attributes
//   */
//  def parseAtts(p:Parser[(String,Any)]):Parser[Map[String,Any]] = {
//    "{" ~> parseAttsRec(p) <~ "}" ^^ {atts =>
//      atts} |
//    "{" ~ "}" ^^ {_ => Map()}
//  }
//    
//  /**
//   * Parse a list of attributes recursively
//   */
//  def parseAttsRec(parseAtt:Parser[(String,Any)]):Parser[Map[String,Any]] = {
//    parseAtt ~ "," ~ parseAttsRec(parseAtt) ^^ {
//      case pair~","~map =>
//        if(map.contains(pair._1)) throw new DoubleAttribute()
//        map + pair} |
//    parseAtt ^^ {pair => Map(pair)}}
//  
//  /**
//   * Select a value from a map. Throws an error if the value could not be found.
//   */
//  def sel[T](key:String, map:Map[String,Any]):T = {
//    if(print) println(" -- select " + key + " from " + map);
//    if(map.contains(key))
//      map.get(key).get match {case t:T => t}
//    else
//      throw new MatchError("")
//  }
//  
//  /**
//   * Select an optional value from a map.
//   */
//  def selOpt[T](key:String, map:Map[String,Any]):Opt[T] = {
//    if(print) println(" -- selectopt " + key + " from " + map);
//    if(map.contains(key))
//      map.get(key).get match {case s:T => Filled(s)}
//    else
//      Empty[T]
//  }
//  
//  /**
//   * Parse a type
//   */
//  def parseType[T <: Type](attName:String, typeParser:Parser[T]):Parser[(String, T)] = {
//    ident ~ ":" ~ typeParser ^? {
//    case att~":"~typ if att==attName => 
//      if(print) println(" -- parser type -> "+ att +" vs " +attName  + ": " + typ);
//      (attName, typ)}
//  }
//  
//  /**
//   * Parse a string
//   */
//  val parseStrDirect:Parser[String] =
//    "{" ~> (ident | stringLit) <~ "}" ^^ {s => s} | 
//    (ident | stringLit) ^^ {s => s} 
//    
//  def parseStr(attName:String):Parser[(String, String)] =
//    ident ~ ":" ~ (ident | stringLit) ^?
//    {case att~":"~str if att==attName => if(print) println(" --" + attName + " :" + str);
//    						(attName, str)}
//    
//  def parseInt(attName:String):Parser[(String, Int)] =
//    ident ~ ":" ~ numericLit ^? {
//    case att~":"~int if att==attName=> (attName, int.toInt)}
//  
//  def typeList[T <: Type](typ:Parser[T]):Parser[List[T]] = {
//    def par:Parser[List[T]] = {
//      typ ~ "," ~ par ^^ {case v~","~rest => v :: rest} |
//      typ ^^ {v => List(v)}
//    }
//    "[" ~ "]" ^^ {_ => List()} |
//    "[" ~> par <~ "]" ^^ {l => l} |
//    typ ^^ {v => List(v)}
//  }
//  
//  
//  lexical.delimiters += ("{", "}", ",", ":", "[", "]")
//  lexical.reserved += ("ADD", "CHANGE", "REMOVE", "TO", "FROM", "WITH", "INSTANCES", "AND",  
//		  				"CITY", "AIRPORT", "AIRPLANE", "TYPE", "DISTANCE", "SEAT", "SEATS", "PERIOD", "PERIODS", "TEMPLATE", "FLIGHT")
//  
//  // ----- ALL THE ACTUAL OPERATORS ----- //
//    
//  def parseOp(): Parser[Operation]  =
//    "ADD" ~> "CITY" ~> cityData ^^ {c => AddCity(c)} |
//    "CHANGE" ~> "CITY" ~> city ~ "TO" ~ city ^^ {case f~"TO"~t => ChangeCity(f, t)} |
//    "REMOVE" ~> "CITY" ~> city ^^ {c => RemoveCity(c)} |
//    "ADD" ~> "AIRPORT" ~> airportData ^^ {a => AddAirport(a)} |
//    "CHANGE" ~> "AIRPORT" ~> airport ~ "TO" ~ airport ^^ {case f~"TO"~t => ChangeAirport(f,t)} |
//    "REMOVE" ~> "AIRPORT" ~> airport ^^ {a => RemoveAirport(a)} |
//    "ADD" ~> "DISTANCE" ~> distData ^^ {d => AddDist(d)} |
//    "CHANGE" ~> "DISTANCE" ~> dist ~ "TO" ~ dist ^^ {case f~"TO"~t => ChangeDistTo(f,t)} |
//    "REMOVE" ~> "DISTANCE" ~> dist ^^ {d => RemoveDist(d)} |
//    "ADD" ~> "FLIGHT" ~> "TIME" ~> flightTimeData ^^ {f => AddFlightTime(f)} |
//    "CHANGE" ~> "DISTANCE" ~> flightTime ~ "TO" ~ flightTime ^^ {case f~"TO"~t => ChangeFlightTime(f,t)} |
//    "REMOVE" ~> "DISTANCE" ~> flightTime ^^ {f => RemoveFlightTime(f)} |
//    "ADD" ~> "AIRPLANE" ~> "TYPE" ~> airplaneTypeData ~ "WITH" ~ "SEATS" ~ typeList(seatData) ^^ {case a~_~_~s => AddAirplaneType(a, s)} |
//    "CHANGE" ~> "AIRPLANE" ~> "TYPE" ~> flightTime ~ "TO" ~ flightTime ^^ {case f~"TO"~t => ChangeFlightTime(f,t)} |
//    "REMOVE" ~> "AIRPLANE" ~> "TYPE" ~> airplaneType ^^ {a => RemoveAirplaneType(a)} |
//    "ADD" ~> "SEAT" ~> seatData ~ "TO" ~ "AIRPLANE" ~ "TYPE" ~ airplaneType ^^ {case s~_~_~_~a => AddSeats(a,List(s))} |
//    "ADD" ~> "SEATS" ~> typeList(seatData) ~ "TO" ~ "AIRPLANE" ~ "TYPE" ~ airplaneType ^^ {case s~"OF"~_~_~a => AddSeats(a,s)} |
//    "CHANGE" ~> "SEAT" ~> seat ~ "OF" ~ "AIRPLANE" ~ "TYPE" ~ airplaneType ~ "TO" ~ seat ^^ {case s1~"OF"~_~_~a~_~s2 => ChangeSeats(a,List(s1),s2)} |
//    "CHANGE" ~> "SEATS" ~> typeList(seat) ~ "OF" ~ "AIRPLANE" ~ "TYPE" ~ airplaneType ~ "TO" ~ seat ^^ {case s1~_~_~_~a~_~s2 => ChangeSeats(a,s1,s2)} |
//    "CHANGE" ~> "SEATS" ~> "OF" ~> "AIRPLANE" ~> "TYPE" ~> airplaneType ~ "TO" ~ typeList(seatData) ^^ {case a~_~s => ChangeSeatsTo(a,s)} |
//    "REMOVE" ~> "SEAT" ~> seat ~ "FROM" ~ "AIRPLANE" ~ "TYPE" ~ airplaneType ^^ {case s~"FROM"~"AIRPLANE"~"TYPE"~a => RemoveSeats(a, List(s))} |
//    "REMOVE" ~> "SEATS" ~> typeList(seat) ~ "FROM" ~ "AIRPLANE" ~ "TYPE" ~ airplaneType ^^ {case s~"FROM"~"AIRPLANE"~"TYPE"~a => RemoveSeats(a, s)} |
//    "ADD" ~> "SEAT" ~> "TYPE" ~> parseStrDirect ^^ {s => AddSeatType(s)} |
//    "CHANGE" ~> "SEAT" ~> "TYPE" ~> parseStrDirect ~ "TO" ~ parseStrDirect ^^ {case f~"TO"~t => ChangeSeatType(f,t)} |
//    "REMOVE" ~> "SEAT" ~> "TYPE" ~> parseStrDirect ^^ {case s => RemoveSeatType(s)} |
//    "ADD" ~> "AIRLINE" ~> airlineData ^^ {a => AddAirline(a)} |
//    "CHANGE" ~> "AIRLINE" ~> airline ~ "TO" ~ airline ^^ {case f~"TO"~t => ChangeAirline(f,t)} |
//    "REMOVE" ~> "AIRLINE" ~> airline ^^ {a => RemoveAirline(a)} |
//    "ADD" ~> "TEMPLATE" ~> templateData ~ "WITH" ~ "SEAT" ~ "INSTANCES" ~ typeList(seatInstanceData) ~ "AND" ~ "WITH" ~ "PERIODS" ~ typeList(periodData) ^^
//    					{case t~_~_~_~s~_~_~_~p => AddTemplate(t, s, p)} |
//    "CHANGE" ~> "TEMPLATE" ~> template ~ "TO" ~ templateChange ^^ {case f~"TO"~t => ChangeTemplate(f,t)} |
//    "REMOVE" ~> "TEMPLATE" ~> template ^^ {t => RemoveTemplate(t)} |
//    "ADD" ~> "FLIGHT" ~> flightData ^^ {f => AddFlight(f)} |
//    "ADD" ~> "FLIGHT" ~> flightData ~ "WITH" ~ "SEAT" ~ "INSTANCES" ~ typeList(seatInstanceData) ^^ {case f~_~_~_~s => AddFlight2(f,s)} |
//    "CHANGE" ~> "FLIGHT" ~> flight ~ "TO" ~ flightChange ^^ {case f~"TO"~t => ChangeFlight(f,t)} |
//    "REMOVE" ~> "FLIGHT" ~> flight ^^ {f => RemoveFlight(f)} |
//    "ADD" ~> "PERIOD" ~> periodData ~ "TO" ~ "TEMPLATE" ~ template ^^ {case p~_~_~t => AddTemplatePeriods(t,List(p))} |
//    "ADD" ~> "PERIODS" ~> typeList(periodData) ~ "TO" ~ "TEMPLATE" ~ template ^^ {case p~_~_~t => AddTemplatePeriods(t,p)} |
//    "CHANGE" ~> "PERIOD" ~> period ~ "OF" ~ "TEMPLATE" ~ template ~ "TO" ~ period ^^ {case p1~_~_~t~_~p => ChangeTemplatePeriods(t, List(p1), p)} |
//    "CHANGE" ~> "PERIODS" ~> typeList(period) ~ "OF" ~ "TEMPLATE" ~ template ~ "TO" ~ period ^^ {case p1~_~_~t~_~p => ChangeTemplatePeriods(t, p1, p)} |
//    "CHANGE" ~> "PERIODS" ~> "OF" ~> "TEMPLATE" ~> template ~ "TO" ~ typeList(periodData) ^^ {case t~"TO"~p => ChangeTemplatePeriodsTo(t, p)} |
//    "REMOVE" ~> "PERIOD" ~> period ~ "FROM" ~ "TEMPLATE" ~ template ^^ {case p~_~_~t => RemoveTemplatePeriods(t, List(p))} |  
//    "REMOVE" ~> "PERIODS" ~> typeList(period) ~ "FROM" ~ "TEMPLATE" ~ template ^^ {case p~_~_~t => RemoveTemplatePeriods(t, p)} |
//    "CHANGE" ~> "SEAT" ~> "INSTANCE" ~> seatInstance ~ "OF" ~ "TEMPLATE" ~ template ~ "TO" ~ seatInstanceData ^^
//    					{case s1~"OF"~_~t~"TO"~s2 => ChangeTemplateSeatInstances(t, List(s1), s2)} |
//    "CHANGE" ~> "SEAT" ~> "INSTANCES" ~> typeList(seatInstance) ~ "OF" ~ "TEMPLATE" ~ template ~ "TO" ~ seatInstanceData ^^
//    					{case s1~"OF"~_~t~"TO"~s2 => ChangeTemplateSeatInstances(t, s1, s2)} |
//    "CHANGE" ~> "SEAT" ~> "INSTANCES" ~> "OF" ~> "TEMPLATE" ~> template ~ "TO" ~ typeList(seatInstanceData) ^^
//    					{case t~"TO"~s => ChangeTemplateSeatInstancesTo(t, s)} |
//    "CHANGE" ~> "SEAT" ~> "INSTANCE" ~> seatInstance ~ "OF" ~ "FLIGHT" ~ flight ~ "TO" ~ seatInstanceData ^^
//    					{case s1~"OF"~_~f~"TO"~s2 => ChangeFlightSeatInstances(f, List(s1), s2)} | 
//    "CHANGE" ~> "SEAT" ~> "INSTANCES" ~> typeList(seatInstance) ~ "OF" ~ "FLIGHT" ~ flight ~ "TO" ~ seatInstanceData ^^
//    					{case s1~"OF"~_~f~"TO"~s2 => ChangeFlightSeatInstances(f, s1, s2)} |
//    "CHANGE" ~> "SEAT" ~> "INSTANCES" ~> "OF" ~> "FLIGHT" ~> flight ~ "TO" ~ typeList(seatInstanceData) ^^
//    					{case f~"TO"~s => ChangeFlightSeatInstancesTo(f, s)}
//    
//    
//  // ----- ALL THE ACTUAL TYPES ----- //
//    
//  // Helper Types
//  val timeAtts = parseInt("h") | parseInt("m") | parseInt("s")
//  val time =
//    parseAtts(timeAtts) ^^ {as => Time(selOpt[Int]("h", as), selOpt[Int]("m", as), selOpt[Int]("s", as))}
//  
//  val dateAtts = parseInt("d") | parseInt("m") | parseInt("y")
//  val date =
//    parseAtts(dateAtts) ^^ {as => Date(sel[Int]("d",as), sel[Int]("m",as), sel[Int]("y",as))}
//  
//  val dateTimeAtts = parseType("date", date) | parseType("time", time)
//  val dateTime:Parser[DateTime] =
//    parseAtts(dateTimeAtts) ^^ {as => DateTime(sel[Date]("date", as), selOpt[Time]("time", as))}
//  
//  val euroAtts = parseInt("euro") | parseInt("cent")
//  val dollarAtts = parseInt("euro") | parseInt("cent")
//  val price:Parser[Price] =
//    parseAtts(euroAtts) ^? {case as => Euro(selOpt[Int]("euro", as), selOpt[Int]("cent", as))}
//    parseAtts(dollarAtts) ^^ {as => Dollar(selOpt[Int]("dollar", as), selOpt[Int]("cent", as))}
//  
//  val timePerdiodAtts = parseType("from",dateTime) | parseType("to", dateTime)
//  val timePeriod =
//    parseAtts(timePerdiodAtts) ^^ {as => TimePeriod(sel[DateTime]("from", as), sel[DateTime]("to", as))}
//  
//  
//  //City
//  val cityAtts = parseStr("name")
//  val city =
//    parseAtts(cityAtts) ^^ {as => City(selOpt[String]("name",as))}
//  val cityData =
//    parseAtts(cityAtts) ^^ {as => City_data(sel[String]("name",as))}
//  
//  //Airport
//  val airportAtts = 
//    parseStr("name") |
//    parseStr("short") |
//    parseType("city", city)
//  val airport =
//    parseAtts(airportAtts) ^^ {as => 
//      Airport(selOpt[City]("city",as), selOpt[String]("name",as), selOpt[String]("short",as))}
//  val airportData =
//    parseAtts(airportAtts) ^^ {as =>
//      Airport_data(sel[City]("city",as), sel[String]("name",as), sel[String]("short",as))}
//    
//  //Distance
//  val distAtts =
//    parseType("from", airport) |
//    parseType("to", airport) |
//    parseInt("dist")
//  val dist =
//    parseAtts(distAtts) ^^ {as => Dist(selOpt[Airport]("from",as), selOpt[Airport]("to",as), selOpt[Int]("dist",as))}
//  val distData =
//    parseAtts(distAtts) ^^ {as => Dist_data(sel[Airport]("from",as), sel[Airport]("to",as), sel[Int]("dist",as))}
//  
//  //AirplaneType
//  val airplaneTypeAtts =
//    parseStr("name")
//  val airplaneType =
//    parseAtts(airplaneTypeAtts) ^^ {as => AirplaneType(selOpt[String]("name",as))}
//  val airplaneTypeData =
//    parseAtts(airplaneTypeAtts) ^^ {as => AirplaneType_data(sel[String]("name",as))}
//  
//  //Seats
//  val seatAtts =
//    parseInt("number") |
//    parseStr("seatType")
//  val seatDataAtts =
//    seatAtts |
//    parseInt("amt")
//  val seat =
//    parseAtts(seatAtts) ^^ {as => Seat(selOpt[Int]("number", as), selOpt[String]("seatType", as))}
//  val seatData = 
//    parseAtts(seatDataAtts) ^^ {as => Seat_data(selOpt[Int]("number", as), selOpt[Int]("amt", as), sel[String]("seatType", as))}
//  
//  //FlightTime
//  val flightTimeAtts =
//    parseType("from", airport) |
//    parseType("to", airport) |
//    parseType("airplaneType:", airplaneType) |
//    parseType("time", time)
//  val flightTime =
//    parseAtts(flightTimeAtts) ^^ {as => FlightTime(selOpt[Airport]("from",as), selOpt("to", as),
//    									selOpt[AirplaneType]("airplaneType", as), selOpt[Time]("time", as))}
//  val flightTimeData =
//    parseAtts(flightTimeAtts) ^^ {as => FlightTime_data(sel[Airport]("from",as), sel("to", as),
//    									sel[AirplaneType]("airplaneType", as), sel[Time]("time", as))}
//  //AirlineCompany
//  val airlineAtts =
//    parseStr("name") |
//    parseStr("short")
//  val airline =
//    parseAtts(airlineAtts) ^^ {as => Airline(selOpt[String]("name", as), selOpt[String]("short", as))}
//  val airlineData =
//    parseAtts(airlineAtts) ^^ {as => Airline_data(sel[String]("name", as), sel[String]("short",as))}
//  
//  //Template
//  val templateAtts =
//    parseStr("fln") |
//    parseType("airplaneType", airplaneType) |
//    parseType("from", airport) |
//    parseType("to", airport)
//  val templateSelectAtts =
//    parseType("airline", airline)
//  val template = parseAtts(templateSelectAtts) ^^
//  	{as => Template(selOpt[Airline]("airline", as), selOpt[String]("fln", as), selOpt[Airport]("from", as), selOpt[Airport]("to", as), selOpt[AirplaneType]("airplaneType", as))}
//  val templateChange = parseAtts(templateAtts) ^^
//	{as => Template_change(selOpt[String]("fln", as), selOpt[Airport]("from", as), selOpt[Airport]("to", as), selOpt[AirplaneType]("airplaneType", as))}
//  val templateData = parseAtts(templateAtts) ^^
//    {as => Template_data(sel[String]("fln", as), sel[Airport]("from", as), sel[Airport]("to", as), sel[AirplaneType]("airplaneType", as))}
//  
//  //Seat Instances
//  val seatInstance1Atts = parseInt("number") | parseInt("amt")
//  val seatInstance2Atts = parseStr("type")
//  val seatInstances1DataAtts = seatInstance1Atts | parseType("price", price)
//  val seatInstances2DataAtts = seatInstance2Atts | parseType("price", price)
//  val seatInstanceData:Parser[SeatInstance_data] =
//   	parseAtts(seatInstances1DataAtts) ^? {case as => SeatNumberInstances_data(sel[Int]("number", as), selOpt[Int]("amt", as), sel[Price]("price", as))} |
//   	parseAtts(seatInstances2DataAtts) ^^ {as => SeatTypeInstances_data(sel[String]("type", as), sel[Price]("price", as))}
//  val seatInstance:Parser[SeatInstance] =
//   	parseAtts(seatInstance1Atts) ^? {case as => SeatNumberInstances(sel[Int]("number", as), selOpt[Int]("amt", as))} |
//   	parseAtts(seatInstance2Atts) ^^ {as => SeatTypeInstances(sel[String]("type", as))}
//    
//   //Periods
//   val fromToAtts = parseType("from", date) | parseType("to", date)
//   val contPeriodAtts = fromToAtts | parseType("day", date)
//   val containedPeriod = parseAtts(contPeriodAtts) ^^
//     {as => ContainedPeriod(selOpt[Date]("from", as), selOpt[Date]("to", as), selOpt[Date]("day", as))}
//   
//   val periodDataAtts = fromToAtts | parseStr("weekday") | parseType("departure", time)
//   val periodAtts = parseType("contained", containedPeriod) | periodDataAtts
//   val period = parseAtts(periodAtts) ^^
//     {as => Period(selOpt[ContainedPeriod]("contained", as), selOpt[Date]("from", as), selOpt[Date]("to", as), selOpt[String]("weekday", as), selOpt[Time]("departure", as))}
//   val periodData = parseAtts(periodAtts) ^^
//     {as => Period_data(selOpt[Date]("from", as), selOpt[Date]("to", as), selOpt[String]("weekday", as), sel[Time]("departure", as))}
//  
//   //Flights
//   val flightDataAtts = 
//     parseType("template", template) |
//     parseType("departure", dateTime) |
//     parseType("arrival", dateTime) |
//     parseType("airplaneType", airplaneType)
//   val flightAtts =
//     flightDataAtts |
//     parseType("duringInterval", timePeriod)
//   val flight: Parser[Flight] =
//     parseAtts(flightAtts) ^? {case as => Flight1(selOpt[Template]("template", as), sel[DateTime]("departure", as),
//         selOpt[DateTime]("arrival", as), selOpt[AirplaneType]("airplaneType", as), selOpt[TimePeriod]("duringInterval", as))} | 
//     parseAtts(flightAtts) ^? {case as => Flight2(selOpt[Template]("template", as), selOpt[DateTime]("departure", as),
//         sel[DateTime]("arrival", as), selOpt[AirplaneType]("airplaneType", as), selOpt[TimePeriod]("duringInterval", as))} |
//     parseAtts(flightAtts) ^^ {as => Flight3(selOpt[Template]("template", as), selOpt[DateTime]("departure", as),
//         selOpt[DateTime]("arrival", as), selOpt[AirplaneType]("airplaneType", as), sel[TimePeriod]("duringInterval", as))}
//   val flightChange =
//     parseAtts(flightDataAtts) ^^ {as => Flight_change(selOpt[Template]("template", as), selOpt[DateTime]("departure", as),
//         selOpt[DateTime]("arrival", as), selOpt[AirplaneType]("airplaneType", as))}
//   val flightData =
//     parseAtts(flightDataAtts) ^^ {as => Flight_data(sel[Template]("template", as), sel[DateTime]("departure", as),
//         selOpt[DateTime]("arrival", as), selOpt[AirplaneType]("airplaneType", as))}
//     
//  
//  /**
//   * Used for tests
//   */
//  def parseType(s:String, typeName:String):Type ={
//    val tokens = new lexical.Scanner(s)
//    printTokens(tokens);
//    
//    val parseFunc:Parser[Type] = typeName match {
//      case "city" => city
//      case "cityData" => cityData
//      case "airport" => airport
//      case "airportData" => airportData
//    }
//    phrase(parseFunc)(tokens) match {
//      case Success(typ, _) => typ
//    }
//  } 
//  
//  //Quick fix for debugging purposes
//  var print:Boolean = false;
//  
//  def parse (s: String):ParseRes = parse(s, false)
//  def parse (s: String, print:Boolean):ParseRes = {
//    val tokens = new lexical.Scanner(s)
//    this.print = print;
//    if(print) printTokens(tokens);
//    try{
//	    phrase(parseOp)(tokens) match {
//	     	case Success(op, _) => PSucces(op)
//	     	case Failure(msg, n) => PFail(msg)
//	     	case Error(msg, _) => PFail(msg)
//	    }
//    }
//    catch {
//       case UnknownAttribute(n) => PFail("unknown attribute: " + n)
//       case SkippedAttribute(n) => PFail("skipped attribute: " + n)
//    }
//  }
//  
//  def printTokens(sc: lexical.Scanner):Unit = {
//    if(!sc.atEnd){
//      println(sc.first);
//      printTokens(sc.rest);
//    }
//  }
//
//  def main (args: Array[String]) = {
//    println("hello"); 
//  }   
//}
//  
////package parser
////
////import scala.util.parsing.combinator.syntactical.StandardTokenParsers
////import syntax.AddCity
////import syntax.Operation
////import syntax.Type
////import syntax.Opt
////import syntax.Empty
////import syntax.Filled
////import syntax.AddAirport
////import syntax.City
////import syntax.City
////import syntax.City1
////import syntax.City2
////import syntax.AddAirport
////import syntax.Airport_data
////import syntax.City_data
////
////sealed abstract class ParseRes;
////case class PSucces(op:Operation) extends ParseRes;
////case class PFail(msg:String) extends ParseRes;
////
////case class UnknownAttribute(name:String) extends Exception
////case class SkippedAttribute(name:String) extends Exception
////
////object Parser extends StandardTokenParsers {
////  type ParserType[T] = Parser[T];
////  
////  lexical.delimiters += ("{", "}", ",", ":")
////  lexical.reserved += ("ADD","CITY", "AIRPORT", "hoihoi")
////
////  def parseOp(): Parser[Operation]  =
////    "ADD" ~> "CITY" ~> parseCityData ^^ {c => new AddCity(c)} |
////    "ADD" ~> "AIRPORT" ~> parseAirportData ^^ {a => new AddAirport(a)}
////            
//// 
////  def parseAtts(parseAtt:Parser[(String,Any)]):Parser[Map[String,Any]] = {
////    parseAtt ~ "," ~ parseAtts(parseAtt) ^^ {case pair~","~map => map + pair} |
////    parseAtt ^^ {pair => Map(pair)}}
////  
////  def select[T](key:String, map:Map[String,Any]):T = {
////    println(" -- selects " + key + " from " + map);
////    if(map.contains(key))
////      map.get(key).get match {case t:T => t}
////    else
////      throw new MatchError("hoi")
////  }
////    
////  def selectOpt[T](key:String, map:Map[String,Any]):Opt[T] = {
////    println(" -- selectOpt " + key + " from " + map);
////    if(map.contains(key))
////      map.get(key).get match {case s:T => Filled(s)}
////    else
////      Empty[T]
////  }
////  
////  val parseCity: Parser[City] =
////    "{" ~> parseAtts(city) <~ "}" ^^ {atts =>
////      if(!atts.contains("name")) City2(select("short",atts))
////      else City1(select("name",atts), selectOpt("short",atts))}
////  val city:Parser[(String,Any)] = {
////    ident ~ ":" ~ ident ^? {
////     case "name"~":"~name => ("name", name)
////     case "short"~":"~short => ("short", short)}}
////  
////  val parseCityData: Parser[City_data] ={
////    "{" ~> parseAtts(cityData) <~ "}" ^^ {atts =>
////        println(atts);
////    	new City_data(select("name",atts), select("short",atts))}}
////  val cityData:Parser[(String,Any)] = {
////    ident ~ ":" ~ ident ^^ {
////     case "name"~":"~name => ("name", name)
////     case "short"~":"~short => ("short", short)
////     case name~":"~_ => throw new UnknownAttribute(name)}}
////
////  val parseAirportData: Parser[Airport_data] =
////    "{" ~> parseAtts(airportData) <~ "}" ^^ {atts =>
////        new Airport_data(select("city",atts), select("name",atts), select("short",atts))}
////  val airportData:Parser[(String,Any)] = 
////    ident ~ ":" ~ ident ^^ {
////     case "name"~":"~name => ("name", name)
////     case "short"~":"~short => ("short", short)
////     case name~":"~_ => throw new UnknownAttribute(name)} |
////    ident ~ ":" ~ parseCity ^^ {
////     case "city"~":"~city => ("city", city)
////     case name~":"~_ => throw new UnknownAttribute(name)} 
////  
////  /*def parse (s: String) = {
////    val tokens = new lexical.Scanner(s)
////    printTokens(tokens);
////    println(phrase(parseOp));
////    phrase(parseOp)(tokens)
////  }*/
////  
////  def parse (s: String):ParseRes = {
////    val tokens = new lexical.Scanner(s)
////    printTokens(tokens);
////    try{
////	    phrase(parseOp)(tokens) match {
////	     	case Success(op, _) => PSucces(op)
////	     	case Failure(msg, n) => PFail(msg)
////	     	case Error(msg, _) => PFail(msg)
////	    }
////    }
////    catch {
////       case UnknownAttribute(n) => PFail("unknown attribute: " + n)
////       case SkippedAttribute(n) => PFail("skipped attribute: " + n)
////    }
////  }
////  
////  def printTokens(sc: lexical.Scanner):Unit = {
////    if(!sc.atEnd){
////      println(sc.first);
////      printTokens(sc.rest);
////    }
////  }
////  
////  /*def test (exprString : String) = {
////     try{
////       parse (exprString) match {
////         case Success(op, _) => println("Operation: " + op)
////         case Failure(msg, n) =>
////           println("Failure: " + msg + " next: " + n)
////           println("Tokens left");
////           printTokens(n.asInstanceOf[lexical.Scanner])
////         case Error(msg, _) => println("Error: " + msg)
////       }
////     }
////     catch{
////       case UnknownAttribute(n) => println("Unkown attribute used:" + n);
////       case SkippedAttribute(n) => println("Attribute was not provided:" + n)
////     }
////  }*/
////
////  def main (args: Array[String]) = {
////    val x = 5
////    x match {
////      case 2 => 
////    }
////    println("hello"); 
////    //test ("ADD AIRPORT {name:BRUSSELS, city:{short:deef}, short:BRU}")
////  }   
////}
////  
////package parser
////
////import scala.util.parsing.combinator.syntactical.StandardTokenParsers
////import syntax.AddCity
////import syntax.Operation
////import syntax.Type
////import syntax.Opt
////import syntax.Empty
////import syntax.Filled
////import syntax.AddAirport
////import syntax.City
////import syntax.City
////import syntax.City1
////import syntax.City2
////import syntax.AddAirport
////import syntax.Airport_data
////import syntax.City_data
////
////sealed abstract class ParseRes;
////case class PSucces(op:Operation) extends ParseRes;
////case class PFail(msg:String) extends ParseRes;
////
////case class UnknownAttribute(name:String) extends Exception
////case class SkippedAttribute(name:String) extends Exception
////
////object Parser extends StandardTokenParsers {
////  type ParserType[T] = Parser[T];
////  
////  lexical.delimiters += ("{", "}", ",", ":")
////  lexical.reserved += ("ADD","CITY", "AIRPORT", "hoihoi")
////
////  def parseOp(): Parser[Operation]  =
////    "ADD" ~> "CITY" ~> parseCityData ^^ {c => new AddCity(c)} |
////    "ADD" ~> "AIRPORT" ~> parseAirportData ^^ {a => new AddAirport(a)}
////            
//// 
////  def parseAtts(parseAtt:Parser[(String,Any)]):Parser[Map[String,Any]] = {
////    parseAtt ~ "," ~ parseAtts(parseAtt) ^^ {case pair~","~map => map + pair} |
////    parseAtt ^^ {pair => Map(pair)}}
////  
////  def select[T](key:String, map:Map[String,Any]):T = {
////    println(" -- selects " + key + " from " + map);
////    if(map.contains(key))
////      map.get(key).get match {case t:T => t}
////    else
////      throw new MatchError("hoi")
////  }
////    
////  def selectOpt[T](key:String, map:Map[String,Any]):Opt[T] = {
////    println(" -- selectOpt " + key + " from " + map);
////    if(map.contains(key))
////      map.get(key).get match {case s:T => Filled(s)}
////    else
////      Empty[T]
////  }
////  
////  val parseCity: Parser[City] =
////    "{" ~> parseAtts(city) <~ "}" ^^ {atts =>
////      if(!atts.contains("name")) City2(select("short",atts))
////      else City1(select("name",atts), selectOpt("short",atts))}
////  val city:Parser[(String,Any)] = {
////    ident ~ ":" ~ ident ^? {
////     case "name"~":"~name => ("name", name)
////     case "short"~":"~short => ("short", short)}}
////  
////  val parseCityData: Parser[City_data] ={
////    "{" ~> parseAtts(cityData) <~ "}" ^^ {atts =>
////        println(atts);
////    	new City_data(select("name",atts), select("short",atts))}}
////  val cityData:Parser[(String,Any)] = {
////    ident ~ ":" ~ ident ^^ {
////     case "name"~":"~name => ("name", name)
////     case "short"~":"~short => ("short", short)
////     case name~":"~_ => throw new UnknownAttribute(name)}}
////
////  val parseAirportData: Parser[Airport_data] =
////    "{" ~> parseAtts(airportData) <~ "}" ^^ {atts =>
////        new Airport_data(select("city",atts), select("name",atts), select("short",atts))}
////  val airportData:Parser[(String,Any)] = 
////    ident ~ ":" ~ ident ^^ {
////     case "name"~":"~name => ("name", name)
////     case "short"~":"~short => ("short", short)
////     case name~":"~_ => throw new UnknownAttribute(name)} |
////    ident ~ ":" ~ parseCity ^^ {
////     case "city"~":"~city => ("city", city)
////     case name~":"~_ => throw new UnknownAttribute(name)} 
////  
////  /*def parse (s: String) = {
////    val tokens = new lexical.Scanner(s)
////    printTokens(tokens);
////    println(phrase(parseOp));
////    phrase(parseOp)(tokens)
////  }*/
////  
////  def parse (s: String):ParseRes = {
////    val tokens = new lexical.Scanner(s)
////    printTokens(tokens);
////    try{
////	    phrase(parseOp)(tokens) match {
////	     	case Success(op, _) => PSucces(op)
////	     	case Failure(msg, n) => PFail(msg)
////	     	case Error(msg, _) => PFail(msg)
////	    }
////    }
////    catch {
////       case UnknownAttribute(n) => PFail("unknown attribute: " + n)
////       case SkippedAttribute(n) => PFail("skipped attribute: " + n)
////    }
////  }
////  
////  def printTokens(sc: lexical.Scanner):Unit = {
////    if(!sc.atEnd){
////      println(sc.first);
////      printTokens(sc.rest);
////    }
////  }
////  
////  /*def test (exprString : String) = {
////     try{
////       parse (exprString) match {
////         case Success(op, _) => println("Operation: " + op)
////         case Failure(msg, n) =>
////           println("Failure: " + msg + " next: " + n)
////           println("Tokens left");
////           printTokens(n.asInstanceOf[lexical.Scanner])
////         case Error(msg, _) => println("Error: " + msg)
////       }
////     }
////     catch{
////       case UnknownAttribute(n) => println("Unkown attribute used:" + n);
////       case SkippedAttribute(n) => println("Attribute was not provided:" + n)
////     }
////  }*/
////
////  def main (args: Array[String]) = {
////    val x = 5
////    x match {
////      case 2 => 
////    }
////    println("hello"); 
////    //test ("ADD AIRPORT {name:BRUSSELS, city:{short:deef}, short:BRU}")
////  }   
////}
////  
