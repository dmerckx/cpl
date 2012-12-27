package syntax


/**
 * The base type for all operations
 */
sealed abstract class Operation

case class AddCity(data:City_data) extends Operation
case class ChangeCityName(city:City, to:String) extends Operation
case class ChangeCityCode(city:City, to:String) extends Operation
case class RemoveCity(city:City) extends Operation

case class AddAirport(data:Airport_data) extends Operation
case class ChangeAirportName(airport:Airport, toName: String) extends Operation
case class ChangeAirportCity(airport:Airport, toCity:City) extends Operation
case class ChangeAirportShort(airport:Airport, toShort:String) extends Operation
case class ChangeAirport(airport:Airport, toName: String, toCity:City, toShort:String) extends Operation
case class RemoveAirport(airport:Airport) extends Operation

case class AddDist(data:Dist_data) extends Operation
case class ChangeDistTo(dist:Dist, toDist:Int) extends Operation
case class RemoveDist(dist:Dist) extends Operation

case class AddFlightTime(data:FlightTime_data) extends Operation
case class ChangeFlightTime(flightTime:FlightTime, time:Int) extends Operation
case class ChangeFlightTimeAirplaneType(flightTime:FlightTime, airplaneType:AirplaneType) extends Operation
case class RemoveFlightTime(flightTime:FlightTime) extends Operation

case class AddTemplate(data:Template_data) extends Operation
case class ChangeTemplatePricesTo(templ:Template, to: List[PricePeriod]) extends Operation
case class ChangeTemplatePricesDuringTo(templ:Template, during:Period, to:List[Prices]) extends Operation
case class ChangeTemplateFlightPeriodTo(templ:Template, reccPeriod: String) extends Operation
case class ChangeTemplateAirplaneTypeTo(templ:Template, AirplaneType: AirplaneType) extends Operation
case class RemoveTemplate(templ:Template) extends Operation

case class AddAirplaneType(data:AirplaneType_data) extends Operation
case class ChangeAirplaneTypeName(airplaneType:AirplaneType, name:String) extends Operation
case class ChangeAirplaneTypeArrangement(airplaneType:AirplaneType, arrangement:List[SeatsWithType]) extends Operation
case class RemoveAirplaneType(airplaneType:AirplaneType) extends Operation

case class AddAirlineCompany(data:AirlineCompany_data) extends Operation
case class ChangeAirlineCompanyName(airlineCompany:AirlineCompany, toName:String) extends Operation
case class ChangeAirlineCompanyShort(airlineCompany:AirlineCompany, toShort:String) extends Operation
case class RemoveAirlineCompany(airlineCompany:AirlineCompany) extends Operation

case class AddSeatType(data:SeatType_data) extends Operation
case class ChangeSeatTypeName(seatType:SeatType) extends Operation
case class RemoveSeatType(seatType:SeatType) extends Operation

//case class ChangePrices(slot:BookingSlots, update:BookingSlotsUpdate) extends Operation

