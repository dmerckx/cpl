package syntax


sealed abstract class Operation
sealed abstract class Type

sealed abstract class Opt[T] extends Type
case class Empty[T] extends Opt[T]
case class Filled[T](t:T) extends Opt[T]

// Helper types
case class Time(h: Opt[Int], m: Opt[Int], s: Opt[Int]) extends Type;
case class Date(d:Int, m:Int, y:Int) extends Type;
case class DateTime(date:Date, time:Opt[Time]) extends Type;
case class Price(dollar : Opt[Int], cent : Opt[Int]) extends Type;


////////////////////////////////////////////////////////////////////////////////
// City ///////////////
////////////////////////////////////////////////////////////////////////////////
//## Types
case class City(id:Opt[Integer], name:Opt[String]) extends Type;
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
case class Airport(name: Opt[String], city: Opt[City], short: Opt[String]) extends Type
case class Airport_data(city:City, name:String, short:String) extends Type
//## Basic Operations
case class AddAirport(data:Airport_data) extends Operation
case class ChangeAirport(airportSelector:Airport, airportChange:Airport)
case class RemoveAirport(airportSelector:Airport)
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////
// Distance ///////////
// For example queries: see City
//## Types
case class Dist(from:Opt[City], to:Opt[City], dist:Opt[Int]) extends Type;
case class Dist_data(from:City, to:City, dist:Int) extends Type;
//## Basic Operations
case class AddDist(data:Dist_data) extends Operation
case class ChangeDistTo(distSelector:Dist, distChange:Dist) extends Operation
case class RemoveDist(distSelector:Dist) extends Operation
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
// FlightTime /////////
////////////////////////////////////////////////////////////////////////////////
// For example queries: see City
//## Types
case class FlightTime(from:Opt[City], to:Opt[City], airplaneType: Opt[AirplaneType], duration: Opt[Time]) extends Type;
case class FlightTime_data(from:City, to:City, airplaneType:AirplaneType, time:Time) extends Type;
//## Basic Operations
case class AddFlightTime(data:FlightTime_data) extends Operation
case class ChangeFlightTime(flightTimeSelector:FlightTime, flightTimeChanges:FlightTime) extends Operation
case class RemoveFlightTime(flightTimeSelector:FlightTime) extends Operation
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////


//////////////////////////////////////////////////////////////////////////////
// AirplaneType ///////
//## Types
case class AirplaneType(name:Opt[String]) extends Type;
case class AirplaneType_data(name:String, arrangement:List[Seat_data]) extends Type;
//## Basic operations
case class AddAirplaneType(data:AirplaneType_data) extends Operation
/*
 * ADD AIRPLANE TYPE {
 * 		name: "Boeing 706"
 * 		arrangement: [
 * 			{ type: business, amt: 100 },
 * 			{ type: first, amt: 50 },
 * 			{ type: economy, amt: 200 },
 * 			{ number: 1000, type: business }
 * 		]
 }
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
case class Seat(number:Opt[Int], seatType:Opt[String]) extends Type;
// Number specifies the seat number.
//		The default value of the seat number is [highest seat number in airplane type so far] + 1
// Amt specifies the amount of seats created.
//		If number is 10 and amt 10, we create seats with numbers 10 to 19.
//		The default value of amt is 1.
// type specifies the seat type
case class Seat_data(number:Opt[Int], amt:Opt[Int], seatType:String) extends Type;
//## Basic operations
case class AddSeat(airplaneTypeSelector:AirplaneType, data:List[Seat_data]) extends Operation
/* 
 * CHANGE AIRPLANE TYPE	{
 * 		name: "Boeing 707"
 * } ADD SEAT {
 * 		type: business,
 * 		amt: 100
 * }
 */

case class ChangeSeats(airplaneTypeSelector:AirplaneType, seatSelector:List[Seat], seatChange:Seat) extends Operation
/* 
 * CHANGE AIRPLANE TYPE {
 * 		name: "Boeing 707"
 * } CHANGE SEATS [
 *		{ number: 101 },
 * 		{ number: 250 },
 * 		{ type: first }
 * ] TO {
 * 		type: business
 * }
 */

// Corresponds to removal of all seats followed by adding the list in data.
case class ChangeSeatsTo(airplaneTypeSelector:AirplaneType, data:List[Seat_data]) extends Operation
/* 
 * CHANGE AIRPLANE TYPE {
 * 		name: "Boeing 707"
 * } CHANGE SEATS TO [
 *		{ amt: 100, type: business }
 * 		{ amt: 150, type: economy }
 * 		{ amt: 50 , type: first}
 * ]
 */

case class RemoveSeat(seatSelector:Seat) extends Operation
/* 
 * CHANGE AIRPLANE TYPE	{ name: "Boeing 707"
 * } REMOVE SEAT {
 * 		type: business
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
    fln: Opt[String],
    airline: Opt[String],
	from: Opt[City],
	to: Opt[City],
	airplaneType: Opt[AirplaneType]) extends Type;
case class Template_data(
    fln: String,
	from: City,
	to: City,
	airplaneType: AirplaneType,
	prices: List[SeatInstance_data],
	periods: List[Period_data]) extends Type;
//## Basic operations
case class AddTemplate(data:Template_data) extends Operation
/* 
 * ADD TEMPLATE {
 * 		fln: ABC300,
 *		from: {name: Brussels},
 *		to: {name: "New York"},
 *		airplaneType: {name: "Boeing 007"},
 *		prices: [
 *			{ type: business, price: { dollar: 100 } },
 *			{ type: economy, price: { dollar: 50 } },
 *			{ number: 465, price: { dollar: 0 } },
 *			{ number: 50, amt: 50, price: { dollar: 500 } }
 *		]
 *		periods: [{
 * 			day: monday,
 *			departure: {h: 12, m: 0}
 *		},{
 * 			day: tuesday,
 *			from: {d: 28, m: 12, y: 1990},
 *			to: {d: 28, m: 12, y: 1991}
 *			departure: {h: 12}
 *		}]
 */

case class ChangeTemplate(selectorTemplate:Template, changeTemplate:Template) extends Operation

case class RemoveTemplate(selectorTemplate:Template) extends Operation
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////


// Flight /////////////
case class TimePeriod(from:DateTime, to:DateTime) extends Type;
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
case class Flight_data(
    template: Template,
    departure: DateTime,
    arrival: Opt[DateTime],
    prices: Opt[List[SeatInstance_data]],
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
 * 
 * # Add flight using nondefault values
 * ADD FLIGHT {
 * 		template: { fln : ABC007 },
 * 		departure: {
 * 			date: { d: 4, m: 1, y: 2013 },
 * 			time: { h: 12, m: 4 }
 * 		},
 * 		arrival: {
 * 			date: { d: 4, m: 1, y: 2013 },
 * 			time: { h: 14, m: 8 } }
 * 		},
 * 		prices: [
 * 			{ type: economy, price: { dollar: 100 } },
 * 			{ type: first, price { dollar: 500, cent: 10 } }
 * 		],
 * 		airplaneType: { name: "Boeing 007" }
 * }
 */

case class ChangeFlight(selectorFlight:Flight, changeFlight:Flight) extends Operation;
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
    contained:ContainedPeriod,
    from:Opt[Date],
    to:Opt[Date],
    day:Opt[String],
    startTime:Opt[Time]) extends Type;
case class Period_data(
    from:Opt[Date],
    to:Opt[Date],
    weekday:Opt[String],
    startTime:Time) extends Type;
//# Operations
case class AddTemplatePeriod(selectorTemplate: Template, period:List[Period_data]);
/*
 * CHANGE TEMPLATE {
 * 		fln: XAB300
 * } ADD PERIOD [{ 
 * 		from : {d: 1, m: 1, y:1990},
 * 		to: {d: 31, m: 12, y:1990},
 * 		weekday: monday,
 * 		startTime: {h: 13}
 * }]
 */
case class ChangeTemplatePeriod(selectorTemplate: Template, selectorPeriod:Period, changePeriod:Period);
/*
 * CHANGE TEMPLATE {
 * 		fln: XAB300
 * } CHANGE PERIOD {
 * 		contained: {day: {d: 28, m: 12, y:1990}}
 * } TO	{
 * 		from : {d: 1, m: 1, y: 1990},
 * 		to: {d: 31, m: 12, y: 1990},
 * 		weekday: monday,
 * 		startTime: {h: 13}
 * 	}
 */
case class ChangeTemplatePeriods(selectorTemplate: Template, periods:List[Period_data]);
/*
 * CHANGE TEMPLATE {
 * 		fln: XAB300
 * } CHANGE PERIODS TO [{
 * 		from : {d: 1, m: 1, y: 1990},
 * 		to: {d: 31, m: 12, y: 1990},
 * 		weekday: monday,
 * 		startTime: {h: 13}
 * 	}]
 */
case class RemoveTemplatePeriod(selectorTemplate: Template, selectorPeriod:Period);
/*
 * CHANGE TEMPLATE {
 * 		fln: XAB300
 * } REMOVE PERIOD {
 * 		contained: {day: {d: 28, m: 12, y: 1990} }
 * }
 */

// Bookable ///////////
// Bookable does not appear in de domain.

// SeatInstance ///////
sealed abstract class SeatInstance_data extends Type;
case class SeatNumberInstances_data(number: Int, amt: Opt[Int], price : Price) extends SeatInstance_data
// We want seatType to be parsed as type (type is not available because type is a keyword reserved by Scala)
case class SeatTypeInstances_data(seatType: String, price: Price) extends SeatInstance_data
sealed abstract class SeatInstance extends Type;
case class SeatNumberInstances(number: Int, amt: Opt[Int]) extends SeatInstance
case class SeatTypeInstances(seatType: String) extends SeatInstance

case class ChangeTemplateSeatInstances(templateSelector:Template, seatInstanceSelectors:List[SeatInstance], seatInstances:SeatInstance_data) extends Operation
/*
 * CHANGE TEMPLATE {
 * 		airline : "ABC"
 * } CHANGE SEAT INSTANCES [
 * 		{ seatType: business },
 * 		{ seatNumber: 404, amt: 100 }
 * ] TO	{
 * 		seatNumber: 1000, price: { dollar: 100 }
 * }
 */
case class ChangeTemplateSeatInstancesTo(templateSelector:Template, seatInstances:List[SeatInstance_data]) extends Operation
/*
 * CHANGE TEMPLATE {
 * 		airline : "ABC"
 * } CHANGE SEAT INSTANCES TO [ 
 * 		{ type: business, price: { dollar: 100 } },
 * 		{ type: economy, price: { dollar: 50 } },
 * 		{ type: first, price: { dollar: 75 } },
 * 		{ number: 100, amt: 2, price: { dollar: 0 } }
 * 	]
 */
case class ChangeFlightSeatInstances(flightSelector:Flight, seatInstanceSelectors:List[SeatInstance], seatInstances:SeatInstance_data) extends Operation
case class ChangeFlightSeatInstancesTo(flightSelector:Flight, seatInstances:List[SeatInstance_data]) extends Operation
/*
 * CHANGE FLIGHT {
 * 		template: { airline: "ABC" },
 * 		during : {
 * 			from : {d: 1, m: 1, y: 2013},
 * 			to: {d: 31, m: 21, y: 2013}
 * 		}
 * } CHANGE SEAT INSTANCES TO {
 * 		price : { dollar : 0 }
 * }
 */

