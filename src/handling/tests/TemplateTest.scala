package handling.tests

import syntax._
import handling._
import handling.exceptions._

object TemplateTest {

  def main(args: Array[String]) {
	  //succes();
    changeFlight();
  }
  
  def succes() {
    val airline1 = Airline(Filled("RYA"),Empty());
    val city1 = City(Filled("Brussels"));
    val airport1 = Airport(Filled(city1),Empty(),Empty());
    val airport2 = Airport(Empty(),Empty(),Filled("NY"));
    val airplaneType1 = AirplaneType(Filled("BOEING727"));
    val airplaneType2 = AirplaneType(Filled("BOEING747"));
    val price1 = Dollar(Filled(100), Filled(5));
    val price2 = Euro(Filled(50),Filled(50));
    val seatInstance1 = SeatNumberInstances_data(5,Filled(5),price1);
    val seatInstance2 = SeatTypeInstances_data("Business",price2);
    val seatInstances = List(seatInstance1,seatInstance2);
    val date1 = Date(1,1,2013);
    val date2 = Date(1,5,1990);
    val	date3 = Date(1,2,2013);
    val startTime1 = Time(Filled(0),Filled(0),Filled(0));
    val startTime2 = Time(Filled(16),Filled(5),Filled(12));
    val period1 = Period_data(Filled(date1),Filled(date3),Filled("5"),startTime2);
    val period2 = Period_data(Empty(),Filled(date3),Empty(),startTime1);
    val periods = List(period1,period2);
    val template = Template_data("RYA1234",airport1,airport2,airplaneType1);
    Handler.handle(AddSeats(airplaneType1,List(Seat_data(Empty(),Empty(),"Business"))));
    Handler.handle(AddTemplate(template,seatInstances, periods));
  }

  def changeFlight() {
    val airline1 = Airline(Filled("RYA"),Empty());
    val city1 = City(Filled("Valencia"));
    val airport1 = Airport(Filled(city1),Empty(),Empty());
    val airport2 = Airport(Empty(),Empty(),Filled("NY"));
    val airplaneType1 = AirplaneType(Filled("BOEING777"));
    val airplaneType2 = AirplaneType(Filled("BOEING727"));
    val price1 = Dollar(Filled(100), Filled(5));
    val price2 = Euro(Filled(50),Filled(50));
    val seatInstance1 = SeatNumberInstances_data(5,Filled(5),price1);
    val seatInstance2 = SeatTypeInstances_data("Business",price2);
    val seatInstances = List(seatInstance1,seatInstance2);
    val date1 = Date(1,1,2013);
    val date2 = Date(1,5,1990);
    val	date3 = Date(1,2,2013);
    val startTime1 = Time(Filled(0),Filled(0),Filled(0));
    val startTime2 = Time(Filled(16),Filled(5),Filled(12));
    val period1 = Period_data(Filled(date1),Filled(date3),Filled("5"),startTime2);
    val period2 = Period_data(Empty(),Filled(date3),Empty(),startTime1);
    val periods = List(period1,period2);
    val template1 = Template(Empty(),Filled("RYA4521"),Filled(airport1),Filled(airport2),Filled(airplaneType2));
    val template_change= Template_change(Filled("RYA4521"),Filled(airport1),Filled(airport2),Filled(airplaneType1));
    Handler.handle(ChangeTemplate(template1,template_change));
  }

}