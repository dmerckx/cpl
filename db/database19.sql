SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `mydb` ;
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`AirplaneType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`AirplaneType` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`AirplaneType` (
  `idAirplaneType` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idAirplaneType`) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `name_UNIQUE` ON `mydb`.`AirplaneType` (`name` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`SeatType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`SeatType` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`SeatType` (
  `idSeatType` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idSeatType`) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `name_UNIQUE` ON `mydb`.`SeatType` (`name` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`Seat`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Seat` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`Seat` (
  `idSeatType` INT NOT NULL ,
  `idAirplaneType` INT NOT NULL ,
  `seatNumber` INT NOT NULL ,
  PRIMARY KEY (`idAirplaneType`, `seatNumber`) ,
  CONSTRAINT `seat_airplaneType_fk`
    FOREIGN KEY (`idAirplaneType` )
    REFERENCES `mydb`.`AirplaneType` (`idAirplaneType` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `seat_seatType_fk`
    FOREIGN KEY (`idSeatType` )
    REFERENCES `mydb`.`SeatType` (`idSeatType` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `type_idx` ON `mydb`.`Seat` (`idSeatType` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`Airline`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Airline` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`Airline` (
  `idAirline` VARCHAR(3) NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idAirline`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`City`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`City` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`City` (
  `idCity` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idCity`) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `name_UNIQUE` ON `mydb`.`City` (`name` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`Airport`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Airport` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`Airport` (
  `idAirport` VARCHAR(3) NOT NULL ,
  `city` INT NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idAirport`) ,
  CONSTRAINT `airport_city_fk`
    FOREIGN KEY (`city` )
    REFERENCES `mydb`.`City` (`idCity` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `city_idx` ON `mydb`.`Airport` (`city` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`Template`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Template` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`Template` (
  `idAirline` VARCHAR(3) NOT NULL ,
  `idTemplate` DECIMAL(4,0) NOT NULL ,
  `idAirplaneType` INT NOT NULL ,
  `idAirportFrom` VARCHAR(3) NOT NULL ,
  `idAirportTo` VARCHAR(3) NOT NULL ,
  PRIMARY KEY (`idAirline`, `idTemplate`) ,
  CONSTRAINT `template_airlineCompany_fk`
    FOREIGN KEY (`idAirline` )
    REFERENCES `mydb`.`Airline` (`idAirline` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `template_airportFrom_fk`
    FOREIGN KEY (`idAirportFrom` )
    REFERENCES `mydb`.`Airport` (`idAirport` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `template_airportTo_fk`
    FOREIGN KEY (`idAirportTo` )
    REFERENCES `mydb`.`Airport` (`idAirport` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `template_airplanetype_fk`
    FOREIGN KEY (`idAirplaneType` )
    REFERENCES `mydb`.`AirplaneType` (`idAirplaneType` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `AirlineCompany_idx` ON `mydb`.`Template` (`idAirline` ASC) ;

CREATE INDEX `airportFrom_idx` ON `mydb`.`Template` (`idAirportFrom` ASC) ;

CREATE INDEX `airportTo_idx` ON `mydb`.`Template` (`idAirportTo` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`Period`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Period` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`Period` (
  `idPeriod` INT NOT NULL AUTO_INCREMENT ,
  `idAirline` VARCHAR(3) NOT NULL ,
  `idTemplate` DECIMAL(4,0) NOT NULL ,
  `fromDate` DATE NOT NULL COMMENT 'If fromDate is null, the period is valid from the beginning of time until toDate.' ,
  `toDate` DATE NULL COMMENT 'If toDate is null, period is valid from fromDate until the end of times.' ,
  `day` TINYINT NULL COMMENT 'If day is null, period spans all days.' ,
  `flightStartTime` TIME NOT NULL ,
  PRIMARY KEY (`idPeriod`) ,
  CONSTRAINT `period_flightTemplate_fk`
    FOREIGN KEY (`idAirline` , `idTemplate` )
    REFERENCES `mydb`.`Template` (`idAirline` , `idTemplate` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `flightTemplate_idx` ON `mydb`.`Period` (`idAirline` ASC, `idTemplate` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`Flight`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Flight` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`Flight` (
  `idFlight` INT NOT NULL AUTO_INCREMENT ,
  `cancelled` BIT(1) NOT NULL DEFAULT 0 ,
  `idAirplaneType` INT NULL ,
  `idPeriod` INT NULL ,
  `idAirline` VARCHAR(3) NOT NULL ,
  `idTemplate` DECIMAL(4,0) NOT NULL ,
  `departure` DATETIME NOT NULL ,
  `arrival` DATETIME NULL ,
  PRIMARY KEY (`idFlight`) ,
  CONSTRAINT `flight_airplaneType_fk`
    FOREIGN KEY (`idAirplaneType` )
    REFERENCES `mydb`.`AirplaneType` (`idAirplaneType` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `flight_template_fk`
    FOREIGN KEY (`idAirline` , `idTemplate` )
    REFERENCES `mydb`.`Template` (`idAirline` , `idTemplate` )
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `flight_period_fk`
    FOREIGN KEY (`idPeriod` )
    REFERENCES `mydb`.`Period` (`idPeriod` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `AirplaneType_idx` ON `mydb`.`Flight` (`idAirplaneType` ASC) ;

CREATE INDEX `Template_idx` ON `mydb`.`Flight` (`idAirline` ASC, `idTemplate` ASC) ;

CREATE UNIQUE INDEX `idAirplaneType_UNIQUE` ON `mydb`.`Flight` (`idAirplaneType` ASC) ;

CREATE INDEX `flight_period_fk_idx` ON `mydb`.`Flight` (`idPeriod` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`SeatInstance`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`SeatInstance` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`SeatInstance` (
  `idFlight` INT NOT NULL AUTO_INCREMENT ,
  `seatNumber` INT NOT NULL ,
  `idAirplaneType` INT NOT NULL ,
  `price` DOUBLE NOT NULL ,
  PRIMARY KEY (`idFlight`, `seatNumber`) ,
  CONSTRAINT `seatinstance_seat_fk`
    FOREIGN KEY (`idAirplaneType` , `seatNumber` )
    REFERENCES `mydb`.`Seat` (`idAirplaneType` , `seatNumber` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `seatinstance_flight_fk`
    FOREIGN KEY (`idFlight` )
    REFERENCES `mydb`.`Flight` (`idFlight` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `bookingslot_seat_fk_idx` ON `mydb`.`SeatInstance` (`idAirplaneType` ASC, `seatNumber` ASC) ;

CREATE INDEX `seatinstance_flight_fk_idx` ON `mydb`.`SeatInstance` (`idFlight` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`Distance`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Distance` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`Distance` (
  `idFromAirport` VARCHAR(3) NOT NULL ,
  `idToAirport` VARCHAR(3) NOT NULL ,
  `distance` DOUBLE NOT NULL ,
  PRIMARY KEY (`idFromAirport`, `idToAirport`) ,
  CONSTRAINT `distance_fromAirport_fk`
    FOREIGN KEY (`idFromAirport` )
    REFERENCES `mydb`.`Airport` (`idAirport` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `distance_toAirport_fk`
    FOREIGN KEY (`idToAirport` )
    REFERENCES `mydb`.`Airport` (`idAirport` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `fromAirport_idx` ON `mydb`.`Distance` (`idFromAirport` ASC) ;

CREATE INDEX `toAirport_idx` ON `mydb`.`Distance` (`idToAirport` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`FlightTime`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`FlightTime` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`FlightTime` (
  `idFromAirport` VARCHAR(3) NOT NULL ,
  `idToAirport` VARCHAR(3) NOT NULL ,
  `idAirplaneType` INT NOT NULL ,
  `duration` TIME NOT NULL ,
  PRIMARY KEY (`idFromAirport`, `idToAirport`, `idAirplaneType`) ,
  CONSTRAINT `flightTime_fromAirport_fk`
    FOREIGN KEY (`idFromAirport` )
    REFERENCES `mydb`.`Airport` (`idAirport` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `flightTime_toAirport_fk`
    FOREIGN KEY (`idToAirport` )
    REFERENCES `mydb`.`Airport` (`idAirport` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `flightTime_airplaneType_fk`
    FOREIGN KEY (`idAirplaneType` )
    REFERENCES `mydb`.`AirplaneType` (`idAirplaneType` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `fromAirport_idx` ON `mydb`.`FlightTime` (`idFromAirport` ASC) ;

CREATE INDEX `toAirport_idx` ON `mydb`.`FlightTime` (`idToAirport` ASC) ;

CREATE INDEX `airplaneType_idx` ON `mydb`.`FlightTime` (`idAirplaneType` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`SeatInstanceTemplate`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`SeatInstanceTemplate` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`SeatInstanceTemplate` (
  `idAirline` VARCHAR(3) NOT NULL ,
  `idTemplate` DECIMAL(4,0) NOT NULL ,
  `seatNumber` INT NOT NULL ,
  `idAirplaneType` INT NOT NULL ,
  `price` DOUBLE NOT NULL ,
  PRIMARY KEY (`seatNumber`, `idAirline`, `idTemplate`) ,
  CONSTRAINT `seatinstancetemplate_seat_fk`
    FOREIGN KEY (`idAirplaneType` , `seatNumber` )
    REFERENCES `mydb`.`Seat` (`idAirplaneType` , `seatNumber` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `seatinstancetemplate_template_fk`
    FOREIGN KEY (`idAirline` , `idTemplate` )
    REFERENCES `mydb`.`Template` (`idAirline` , `idTemplate` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `seatinstancetemplate_seat_fk_idx` ON `mydb`.`SeatInstanceTemplate` (`idAirplaneType` ASC, `seatNumber` ASC) ;

CREATE INDEX `seatinstancetemplate_template_fk_idx` ON `mydb`.`SeatInstanceTemplate` (`idAirline` ASC, `idTemplate` ASC) ;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
