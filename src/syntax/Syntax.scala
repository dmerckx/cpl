package syntax


sealed abstract class Operation
sealed abstract class Type

sealed abstract class Opt[T] extends Type
case class Empty[T] extends Opt[T]
case class Filled[T](t:T) extends Opt[T]

// Helper types
case class Time(h: Opt[Int], m: Opt[Int], s: Opt[Int]) extends Type {
  def compare(that:Time) : Int = {
    if(getTotalTime(Time(h,m,s)) > getTotalTime(that))
        return 1;
    if(getTotalTime(Time(h,m,s)) < getTotalTime(that))
        return -1;
    else
       return 0;
  }
  
  def getTotalTime(time:Time) : Int = {
    return getHour(time)*3600+getMinutes(time)*60+getSeconds(time);
  }
  
  def getHour(time:Time) : Int = {
    time match {
      case Time(Filled(hour),_,_) => return hour;
      case _ => return 0;
    }
  }
  
  def getMinutes(time:Time) : Int = {
    time match {
      case Time(_,Filled(min),_) => return min;
      case _ => return 0;
    }
  }
  
   def getSeconds(time:Time) : Int = {
    time match {
      case Time(_,_,Filled(sec)) => return sec;
      case _ => return 0;
    }
  }
  
}
case class Date(d:Int, m:Int, y:Int) extends Type {
  
  def compare(that:Date) : Int =  {
    if(y > that.y)
        return 1;
     if(y < that.y)
        return -1;
    if(m > that.m)
       return 1;
    if(m < that.m)
       return -1;
    if(d < that.d)
        return -1;
     if(d > that.d)
        return 1;
    else
      return 0;
  }
  
}
case class DateTime(date:Date, time:Opt[Time]) extends Type {
  def compare(that: DateTime): Int = {
    var dateComp = date.compare(that.date);
    if(dateComp != 0)
      return dateComp;
   that.time match {
     case Filled(t1) =>
       time match {
         case Filled(t2) => return t2.compare(t1);
         case Empty() => return t1.compare(Time(Empty(),Empty(),Empty()));
       }
     case Empty() =>
       time match {
         case Filled(t2) => return t2.compare(Time(Empty(),Empty(),Empty()));
         case Empty() => return 0;
       }
   }
  }
  
}

sealed abstract class Price extends Type
case class Dollar(dollar : Opt[Int], cent : Opt[Int]) extends Price;
case class Euro(euro : Opt[Int], cent : Opt[Int]) extends Price;
case class TimePeriod(from:DateTime, to:DateTime) extends Type;

////////////////////////////////////////////////////////////////////////////////
// City ///////////////
////////////////////////////////////////////////////////////////////////////////
//## Types
case class City(name:Opt[String]) extends Type;
case class City_change(name:Opt[String]) extends Type;
case class City_data(name:String) extends Type;
//## Basic Operations
case class AddCity(data:City_data) extends Operation
/*
 * ADD CITY { name: "New York" }
 */

case class ChangeCity(citySelector:City, cityChange:City) extends Operation;
/*
 * CHANGE CITY { name: "New York" } TO { name: "New Orleans" }
 */

case class RemoveCity(citySelector:City) extends Operation
/*
 * REMOVE CITY { name: "New York" } 
 */
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////
// Airport ////////////
////////////////////////////////////////////////////////////////////////////////
// For example queries: see City
//## Types
case class Airport(city: Opt[City], name: Opt[String], short: Opt[String]) extends Type
case class Airport_data(city:City, name:String, short:String) extends Type
//## Basic Operations
case class AddAirport(data:Airport_data) extends Operation
case class ChangeAirport(airportSelector:Airport, airportChange:Airport) extends Operation
case class RemoveAirport(airportSelector:Airport) extends Operation
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////
// Distance ///////////
// For example queries: see City
//## Types
case class Dist(from:Opt[Airport], to:Opt[Airport], dist:Opt[Int]) extends Type;
case class Dist_data(from:Airport, to:Airport, dist:Int) extends Type;
//## Basic Operations
case class AddDist(data:Dist_data) extends Operation
case class ChangeDistTo(distSelector:Dist, changedDist:Dist) extends Operation 
case class RemoveDist(distSelector:Dist) extends Operation
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
// FlightTime /////////
////////////////////////////////////////////////////////////////////////////////
// For example queries: see City
//## Types
case class FlightTime(from:Opt[Airport], to:Opt[Airport], airplaneType: Opt[AirplaneType], time: Opt[Time]) extends Type;
case class FlightTime_data(from:Airport, to:Airport, airplaneType:AirplaneType, time:Time) extends Type;
//## Basic Operations
case class AddFlightTime(data:FlightTime_data) extends Operation
case class ChangeFlightTime(flightTimeSelector:FlightTime, changedFlightTime:FlightTime) extends Operation
case class RemoveFlightTime(flightTimeSelector:FlightTime) extends Operation
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////


//////////////////////////////////////////////////////////////////////////////
// AirplaneType ///////
//## Types
case class AirplaneType(name:Opt[String]) extends Type;
case class AirplaneType_data(name:String) extends Type;
abstract sealed class Seat_change extends Type;
	// default value for amt is 1
	// seatTypeChange is typeChange when parsing
case class Seat_change1(number:Int, amt:Opt[Int], seatTypeChange:String) extends Seat_change;
	// seatTypeSelect is typeSelect when parsing
	// seatTypeChange is typeChange when parsing
case class Seat_change2(seatTypeSelect:Opt[String], seatTypeChange:String) extends Seat_change;
	// Number specifies the seat number.
	//		The default value of the seat number is [highest seat number in airplane type so far] + 1
	// Amt specifies the amount of seats created.
	//		If number is 10 and amt 10, we create seats with numbers 10 to 19.
	//		The default value of amt is 1.
	// type specifies the seat type
abstract sealed class Seat_remove extends Type;
case class Seat_remove1(number:Int, amt:Opt[Int]) extends Seat_remove;
	// seatType is type when parsing
case class Seat_remove2(seatType:Opt[String]) extends Seat_remove;
case class Seat_data(number:Opt[Int], amt:Opt[Int], seatType:String) extends Type;


//## Basic operations

case class AddAirplaneType(data:AirplaneType_data, seats:List[Seat_data]) extends Operation
/*
 * ADD AIRPLANE TYPE {
 * 		name: "Boeing 706"
 * } WITH SEATS [
 * 		{ type: business, amt: 100 },
 * 		{ type: first, amt: 50 },
 * 		{ type: economy, amt: 200 },
 * 		{ number: 1000, type: business }
 * ]
 */ 
case class ChangeAirplaneType(airplaneTypeSelector:AirplaneType, airplaneType:AirplaneType) extends Operation
/*
 * CHANGE AIRPLANE TYPE { name: "Boeing 706" } TO { name: "Boeing 007" }  
 */

case class RemoveAirplaneType(airplaneTypeSelector:AirplaneType) extends Operation
/*
 * REMOVE AIRPLANE TYPE { name: "Boeing 007" }
 */
////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////


//////////////////////////////////////////////////////////////////////////////
// Seat ///////////////
//## Types
// We want seatType to correspond to "type" in our language syntax.
//## Basic operations
//ADD SEAT ALSO PROVIDED
case class AddSeats(airplaneTypeSelector:AirplaneType, data:List[Seat_data]) extends Operation
/* 
 * ADD SEATS [{
 * 		type: business,
 * 		amt: 100
 * }] TO AIRPLANE TYPE {
 * 		name: "Boeing 707"
 * }
 */
// Corresponds to removal of all seats followed by adding the list in data.
//CHANGE SEAT ALSO PROVIDED
case class ChangeSeatsTo(airplaneTypeSelector:AirplaneType, data:List[Seat_change]) extends Operation
/* 
 * CHANGE SEATS OF AIRPLANE TYPE {
 * 		name: "Boeing 707"
 * } TO [
 *		{ numer: 1, amt: 100, type: business }
 * 		{ numer: 101, amt: 150, type: economy }
 * 		{ number: 251, amt: 50 , type: first}
 * ]
 */
//REMOVE SEAT ALSO PROVIDED
case class RemoveSeats(airplaneTypeSelector:AirplaneType, seatSelectors:List[Seat_remove]) extends Operation
/*
 * REMOVE SEAT {
 * 		type: business
 * } FROM AIRPLANE TYPE {
 * 		name: "Boeing 707"
 * } 
 */
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////
// SeatType ///////////
// We use String instead of SeatType to make the syntax more legible. 
case class AddSeatType(name: String) extends Operation
/*
 * ADD SEAT TYPE { business }
 */

case class ChangeSeatType(seatTypeSelector:String, seatTypeChange:String) extends Operation
/*
 * CHANGE SEAT TYPE { business } TO { first }
 */

case class RemoveSeatType(seatTypeSelector:String) extends Operation
/*
 * REMOVE SEAT TYPE { first }
 */


// AirlineCompany /////
//## Types
case class Airline(short: Opt[String], name:Opt[String]) extends Type;
case class Airline_data(short:String, name:String) extends Type;

//## Basic operations
case class AddAirline(data:Airline_data) extends Operation

case class ChangeAirline(airlineCompanySelector:Airline, airlineCompany:Airline) extends Operation

case class RemoveAirline(airlineCompanySelector:Airline) extends Operation

// Template ///////////
//## Types
case class Template(
    airline: Opt[Airline],
    fln: Opt[String],
	from: Opt[Airport],
	to: Opt[Airport],
	airplaneType: Opt[AirplaneType]) extends Type;
case class Template_change(
    fln: Opt[String],
	from: Opt[Airport],
	to: Opt[Airport],
	airplaneType: Opt[AirplaneType]) extends Type;
case class Template_data(
    fln: String,
	from: Airport,
	to: Airport,
	airplaneType: AirplaneType) extends Type;
//## Basic operations
case class AddTemplate(data:Template_data, 	prices: List[SeatInstance_data], periods: List[Period_data]) extends Operation
/* 
 * ADD TEMPLATE {
 * 		fln: ABC300,
 *		from: {name: Brussels},
 *		to: {name: "New York"},
 *		airplaneType: {name: "Boeing 007"}
 * } WITH SEAT INSTANCES [
 *		{ type: business, price: { dollar: 100 } },
 *		{ type: economy, price: { dollar: 50 } },
 *		{ number: 465, price: { dollar: 0 } },
 *		{ number: 50, amt: 50, price: { dollar: 500 } }
 * ] AND WITH PERIODS [{
 * 		day: monday,
 *		departure: {h: 12, m: 0}
 *	},{
 * 		day: tuesday,
 *		from: {d: 28, m: 12, y: 1990},
 *		to: {d: 28, m: 12, y: 1991}
 *		departure: {h: 12}
 *	}]
 */

case class ChangeTemplate(selectorTemplate:Template, changeTemplate:Template_change) extends Operation

case class RemoveTemplate(selectorTemplate:Template) extends Operation
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////


// Flight /////////////
sealed abstract class Flight extends Type;
// We have to subtype Flight because either departure time, arrival time or during interval needs to be specified.
// Otherwise, a flight selector can potentially match an infinite number of flights.
case class Flight1(
    template: Opt[Template],
    departure: DateTime,
    arrival: Opt[DateTime],
    airplaneType: Opt[AirplaneType],
    duringInterval : Opt[TimePeriod]) extends Flight;
case class Flight2(
    template: Opt[Template],
    departure: Opt[DateTime],
    arrival: DateTime,
    airplaneType: Opt[AirplaneType],
    duringInterval: Opt[TimePeriod]) extends Flight;
case class Flight3(
    template: Opt[Template],
    departure: Opt[DateTime],
    arrival: Opt[DateTime],
    airplaneType: Opt[AirplaneType],
    duringInterval: TimePeriod) extends Flight;
case class Flight_change( // Flight_change is like Flight but doesn't have duringInterval 
    departure: Opt[DateTime],
    arrival: Opt[DateTime],
    airplaneType: Opt[AirplaneType]) extends Flight;
case class Flight_data(
    template: Template,
    departure: DateTime,
    arrival: Opt[DateTime],
    airplaneType: Opt[AirplaneType]) extends Type;

case class AddFlight(data:Flight_data) extends Operation;
/*
 * # Add flight using default values
 * ADD FLIGHT {
 * 		template: { fln : ABC007 },
 * 		departure: {
 * 			date: { d: 4, m: 1, y: 2013 },
 * 			time: { h: 12, m: 4 }
 * 		}
 * }
 */
case class AddFlight2(data:Flight_data, prices:List[SeatInstance_data]) extends Operation;
/* 
 * # Add flight using nondefault values
 * ADD FLIGHT {
 * 		template: { fln : ABC007 },
 * 		departure: {
 * 			date: { d: 4, m: 1, y: 2013 },
 * 			time: { h: 12, m: 4 }
 * 		},
 * 		arrival: {
 * 			date: { d: 4, m: 1, y: 2013 },
 * 			time: { h: 14, m: 8 }
 * 		},
 * 		airplaneType: { name: "Boeing 007" }
 * } WITH SEAT INSTANCES [
 * 		{ type: economy, price: { dollar: 100 } },
 * 		{ type: first, price { dollar: 500, cent: 10 } }
 * 	]
 * }
 */

case class ChangeFlight(selectorFlight:Flight, changeFlight:Flight_change) extends Operation;
/*
 * CHANGE FLIGHT {
 * 		template: { fln : ABC007 },
 * 		departure: {
 * 			date: { d: 4, m: 1, y: 2013 },
 * 			time: { h: 12, m: 4 }
 * 		}
 * } TO {
 * 		arrival: {
 * 			date: { d: 4, m: 1, y: 2013 }
 * 			time: { h: 14, m: 8 }
 * 		}
 * }
 */

case class RemoveFlight(selectorFlight:Flight) extends Operation;


// Period /////////////
//## Types
case class ContainedPeriod(
    from:Opt[Date],
    to:Opt[Date],
    day:Opt[Date]) extends Type
case class Period(
    contained:Opt[ContainedPeriod],
    from:Opt[Date],
    to:Opt[Date],
    weekday:Opt[String],
    startTime:Opt[Time]) extends Type;
case class Period_data(
    from:Opt[Date],
    to:Opt[Date],
    weekday:Opt[String],
    startTime:Time) extends Type;
//# Operations
case class AddTemplatePeriods(selectorTemplate: Template, periods:List[Period_data]) extends Operation;
/*
 * ADD PERIODS [{ 
 * 		from : {d: 1, m: 1, y:1990},
 * 		to: {d: 31, m: 12, y:1990},
 * 		weekday: monday,
 * 		startTime: {h: 13}
 * }] TO TEMPLATE {
 * 		fln: XAB300
 * }
 */
case class ChangeTemplatePeriods(selectorTemplate: Template, selectorPeriods:List[Period], changePeriod:Period) extends Operation;
/*
 * CHANGE PERIOD {
 * 		contained: {day: {d: 28, m: 12, y:1990}}
 * } OF TEMPLATE {
 * 		fln: XAB300
 * } TO	{
 * 		from : {d: 1, m: 1, y: 1990},
 * 		to: {d: 31, m: 12, y: 1990},
 * 		weekday: monday,
 * 		startTime: {h: 13}
 * 	}
 */
case class ChangeTemplatePeriodsTo(selectorTemplate: Template, periods:List[Period_data]) extends Operation;
/*
 * CHANGE PERIODS OF TEMPLATE {
 * 		fln: XAB300
 * } TO [{
 * 		from : {d: 1, m: 1, y: 1990},
 * 		to: {d: 31, m: 12, y: 1990},
 * 		weekday: monday,
 * 		startTime: {h: 13}
 * 	}]
 */
case class RemoveTemplatePeriods(selectorTemplate: Template, selectorPeriods:List[Period]) extends Operation;
/*
 * REMOVE PERIODS [{
 * 		contained: {day: {d: 28, m: 12, y: 1990} }
 * }] FROM TEMPLATE {
 * 		fln: XAB300
 * }
 */

// Bookable ///////////
// Bookable does not appear in de domain.

// SeatInstance ///////
sealed abstract class SeatInstance_data extends Type;
case class SeatNumberInstances_data(number: Int, amt: Opt[Int], price : Price) extends SeatInstance_data
// We want seatType to be parsed as type (type is not available because type is a keyword reserved by Scala)
case class SeatTypeInstances_data(seatType: String, price: Price) extends SeatInstance_data
//sealed abstract class SeatInstance extends Type;
//case class SeatNumberInstances(number: Int, amt: Opt[Int]) extends SeatInstance
//case class SeatTypeInstances(seatType: String) extends SeatInstance


//CHANGE SEAT INSTANCE syntax is er nog
case class ChangeTemplateSeatInstancesTo(
    templateSelector:Template,
    seatInstances:List[SeatInstance_data]) extends Operation
/*
 * CHANGE SEAT INSTANCES OF TEMPLATE {
 * 		airline : "ABC"
 * } TO [ 
 * 		{ type: business, price: { dollar: 100 } },
 * 		{ type: economy, price: { dollar: 50 } },
 * 		{ type: first, price: { dollar: 75 } },
 * 		{ number: 100, amt: 2, price: { dollar: 0 } }
 * 	]
 */
case class ChangeFlightSeatInstancesTo(
    flightSelector:Flight,
    seatInstances:List[SeatInstance_data]) extends Operation
/*
 * CHANGE SEAT INSTANCES OF FLIGHT {
 * 		template: { airline: "ABC" },
 * 		during : {
 * 			from : {d: 1, m: 1, y: 2013},
 * 			to: {d: 31, m: 21, y: 2013}
 * 		}
 * } TO {
 * 		price : { dollar : 0 }
 * }
 */
