package syntax

/**
 * The base type for all types
 */
sealed abstract class Type;

sealed abstract class City extends Type;
case class City1(name: String) extends City;
case class City2(short: String) extends City;

case class Template(flightnr: String) extends Type;

case class Template_data(
    flightnr: String,
	from: City,
	to: City,
	periods: PricePeriod,
	reccPeriod: String) extends Type;

case class City_data(name: String, short: String) extends Type;

sealed abstract class Airport extends Type;
case class Airport1(name: String) extends Airport;
case class Airport2(city: City) extends Airport;
case class Airport3(short: String) extends Airport;

case class Airport_data(city:City, name:String, short:String) extends Type;

case class Dist_data(from:City, to:City, dist:Int) extends Type;

case class SeatType(name: String) extends Type;

case class SeatsWithType(nr: Int, typ:SeatType) extends Type;

case class AirplainType_data(name: String, arrangement:List[SeatsWithType]) extends Type;

case class AirplainType(name: String) extends Type;

case class Time_data(from:City, to:City, typ:AirplainType, time:Int) extends Type;

case class Period(from: String, to:String) extends Type;



sealed abstract class Prices extends Type;
case class Prices1(price:Int, seats:List[Int]) extends Prices;
case class Prices2(price:Int, typ:SeatType) extends Prices;

case class PricePeriod(period: Period, prices: Prices) extends Type;

sealed abstract class Flight extends Type;
case class Flight1(template:Template, period:Period);
case class Flight2(fln:String);


sealed abstract class BookingSlots extends Type;
case class BookingSlot1(price:Int, templates:List[Template], seat:List[Int], periods: List[Period]) extends BookingSlots;
case class BookingSlot2(price:Int, templates:List[Template], types:List[SeatType], periods: List[Period]) extends BookingSlots;
case class BookingSlot3(price:Int, flight:Flight, seat:Int);
case class BookingSlot4(price:Int, flight:Flight, typ:SeatType);

sealed abstract class BookingSlotsUpdate extends Type;
case class BookingSlotsUpdate1(price:Int) extends BookingSlotsUpdate;