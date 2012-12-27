package syntax


/**
 * The base type for all operations
 */
sealed abstract class Operation

case class AddCity(data:CityData) extends Operation

case class AddAirport(data:AirportData) extends Operation

case class AddDist(data:Dist) extends Operation

case class AddTime(data:Time) extends Operation

case class AddTemplate(data:Template_data) extends Operation

case class ChangeTemplatePricesTo(templ:Template, to: List[PricePeriod]) extends Operation

case class ChangeTemplatePricesDuringTo(templ:Template, during:Period, to:List[Prices]) extends Operation