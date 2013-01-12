package handling.tests

import scala.Array
import scala.Predef._
import syntax._
import handling._

/**
 * Created with IntelliJ IDEA.
 * User: Kristof
 * Date: 11/01/13
 * Time: 21:22
 * To change this template use File | Settings | File Templates.
 */
object AssignmentTest {

  def main(args: Array[String]) : Unit = {
    Handler.handle(
    AddCity(
      City_data(
        "Brusssels"
      )
    ));

    println("city 1 added");
    Handler.handle(
      AddCity(
        City_data(
          "Edinburgh"
        )
      ));

    Handler.handle(
      AddSeatType(
        "business"
      )
    );

    Handler.handle(
      AddSeatType(
        "economy"
      )
    );

    println("city 2 added");
    Handler.handle(
    AddAirplaneType(
      AirplaneType_data(
        "Boeing 727"
      ),
      List(
        Seat_data(Empty(),Filled(24),"business"),
        Seat_data(Empty(),Filled(123),"economy")
      )
    ));

    println("airplanetype added");
    Handler.handle(
    AddAirport(
      Airport_data(
        City(Filled("Brusssels")),
        "Brussels airport",
        "BRU"
      )
    ));

    println("airport 1 added")
    Handler.handle(
      AddAirport(
        Airport_data(
          City(Filled("Edinburgh")),
          "Edinburgh",
          "EDI"
        )
      ));

    println("airport 2 added");
    Handler.handle(
    AddDist(
      Dist_data(
      Airport(Empty(),Filled("Brussels airport"),Empty()),
      Airport(Empty(),Filled("Edinburgh"),Empty()),
      757
      )
    ));

    println("distance added");
    Handler.handle(
      AddFlightTime(
        FlightTime_data(
          Airport(Empty(),Filled("Brussels airport"),Empty()),
          Airport(Empty(),Filled("Edinburgh"),Empty()),
          AirplaneType(Filled("Boeing 727")),
          Time(Filled(1),Filled(22),Empty())
        )
    ));

    println("flighttime added");
    Handler.handle(
      AddAirline(
        Airline_data(
          "BM",
          "British Midlands Airways"
        )
    ));

    println("airline added");
    Handler.handle(
    AddTemplate(
      Template_data(
        "BM1628",
        Airport(Empty(), Filled("Brussels airport"), Empty()),
        Airport(Empty(), Filled("Edinburgh"), Empty()),
        AirplaneType(Filled("Boeing 727"))
      ),
      List(
        SeatTypeInstances_data("economy", Euro(Filled(210), Empty())),
        SeatTypeInstances_data("business", Euro(Filled(340), Empty()))
      ),
      List(
        Period_data(Empty(), Empty(), Filled("monday"), Time(Filled(9),Filled(55),Empty())),
        Period_data(Empty(), Empty(), Filled("wednesday"), Time(Filled(9),Filled(55),Empty())),
        Period_data(Empty(), Empty(), Filled("friday"), Time(Filled(9),Filled(55),Empty()))
      )
    )) ;

    println("template added");
  }


}
