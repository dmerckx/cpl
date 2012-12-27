package syntax

/**
 * The base type for all types
 */
sealed abstract class Type

sealed abstract class Opt[T] extends Type
case class Empty[T] extends Opt[T]
case class Filled[T](t:T) extends Opt[T]

/**
 * Implementation Kristof below
 */

/**
 * Types for selecting and adding cities.
 */
sealed abstract class City extends Type;
case class City1(name:String, short:Opt[String]) extends City;
case class City2(name:Opt[String], short:String) extends City;
case class City_data(name:String, short:String) extends Type;

/**
 * Types for selecting and adding airports.
 */
sealed abstract class Airport extends Type;
case class Airport1(name:String, city: Opt[City], short: Opt[String]) extends Airport;
case class Airport2(name: Opt[String], city:City, short: Opt[String]) extends Airport;
case class Airport3(name: Opt[String], city: Opt[City], short:String) extends Airport;
case class Airport_data(
    city:City, 
    name:String, 
    short:String) extends Type;

/**
 * Types for selecting and adding templates.
 */
case class Template(flightnr:String) extends Type;
case class Template_data(
    flightnr:String,
	from:City,
	to:City,
	periods:PricePeriod,
	airplaneType:AirplaneType,
	reccPeriod: String) extends Type;

/**
 * Types for selecting and adding distances.
 */
case class Dist_data(from:City, to:City, dist:Int) extends Type;
case class Dist(from:City, to:City) extends Type;
/**
 * Types for selecting and adding seattypes.
 */
case class SeatType(name:String) extends Type;
case class SeatsWithType(nr:Int, typ:SeatType) extends Type;
//for example SeatsWithType(50, SeatType("Business")) corresponds to 50 seats of business cards
case class SeatType_data(name:String) extends Type;

/**
 * Types for selecting and adding airplanetypes.
 */
case class AirplaneType(name:String) extends Type;
case class AirplaneType_data(name:String, arrangement:List[SeatsWithType]) extends Type;

/**
 * Types for selecting and adding flight times.
 */
case class FlightTime_data(
    from:City, 
    to:City, 
    airplaneType:AirplaneType, 
    time:Int) extends Type;
sealed abstract class FlightTime 
case class FlightTime1(from:City, to:City) extends FlightTime;
case class FlightTime2(from:City, to:City, airplaneType:AirplaneType) extends FlightTime;

/**
 * Types for selecting periods.
 */
case class Period(from:String, to:String) extends Type;

/**
 * Types for selecting and adding prices.
 */
sealed abstract class Prices extends Type;
case class Prices1(price:Int, seats:List[Int]) extends Prices;
case class Prices2(price:Int, typ:SeatType) extends Prices;
case class PricePeriod(period:Period, prices:Prices) extends Type;

/**
 * Types for selecting and adding airline companies.
 */
sealed abstract class AirlineCompany extends Type;
case class AirlineCompany1(name:String) extends AirlineCompany;
case class AirlineCompany2(short:String) extends AirlineCompany;
case class AirlineCompany_data(name:String, short:String) extends Type;



/**
 * Implementation Jonathan below
 */

/**
 * Types for selecting booking slots.
 */
sealed abstract class BookingSlots extends Type;
case class BookingSlot1(templates:List[Template], seat:List[Int], periods: List[Period]) extends BookingSlots;
case class BookingSlot2(templates:List[Template], types:List[SeatType], periods: List[Period]) extends BookingSlots;
case class BookingSlot3(flight:Flight, seat:Int);
case class BookingSlot4(flight:Flight, typ:SeatType);

/**
 * Type for updating booking slots.
 */
sealed abstract class BookingSlotsUpdate extends Type;
case class BookingSlotsUpdate1(price:Int) extends BookingSlotsUpdate;

sealed abstract class Flight extends Type;
case class Flight1(template:Template, period:Period);
case class Flight2(fln:String);

