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
  `airplaneType` INT NOT NULL ,
  `seatNumber` INT NOT NULL ,
  PRIMARY KEY (`airplaneType`, `seatNumber`) ,
  CONSTRAINT `seat_airplaneType_fk`
    FOREIGN KEY (`airplaneType` )
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
  `idAirlineCompany` VARCHAR(3) NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idAirlineCompany`) )
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
  `code` VARCHAR(3) NOT NULL ,
  `city` INT NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`code`) ,
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
  `idAirlineCompany` VARCHAR(3) NOT NULL ,
  `idTemplate` DECIMAL(4,0) NOT NULL ,
  `idAirplaneType` INT NOT NULL ,
  `idBookable` INT NOT NULL COMMENT 'Candidate key (arbitrarily not the primary key)' ,
  `idAirportFrom` VARCHAR(3) NOT NULL ,
  `idAirportTo` VARCHAR(3) NOT NULL ,
  PRIMARY KEY (`idAirlineCompany`, `idTemplate`) ,
  CONSTRAINT `template_airlineCompany_fk`
    FOREIGN KEY (`idAirlineCompany` )
    REFERENCES `mydb`.`Airline` (`idAirlineCompany` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `template_airportFrom_fk`
    FOREIGN KEY (`idAirportFrom` )
    REFERENCES `mydb`.`Airport` (`code` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `template_airportTo_fk`
    FOREIGN KEY (`idAirportTo` )
    REFERENCES `mydb`.`Airport` (`code` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `template_airplanetype_fk`
    FOREIGN KEY (`idAirplaneType` )
    REFERENCES `mydb`.`AirplaneType` (`idAirplaneType` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `AirlineCompany_idx` ON `mydb`.`Template` (`idAirlineCompany` ASC) ;

CREATE INDEX `airportFrom_idx` ON `mydb`.`Template` (`idAirportFrom` ASC) ;

CREATE INDEX `airportTo_idx` ON `mydb`.`Template` (`idAirportTo` ASC) ;

CREATE UNIQUE INDEX `idBookable_UNIQUE` ON `mydb`.`Template` (`idBookable` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`Period`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Period` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`Period` (
  `idPeriod` INT NOT NULL AUTO_INCREMENT ,
  `idAirlineCompany` VARCHAR(3) NOT NULL ,
  `idTemplate` DECIMAL(4,0) NOT NULL ,
  `fromDate` DATE NOT NULL COMMENT 'If fromDate is null, the period is valid from the beginning of time until toDate.' ,
  `toDate` DATE NULL COMMENT 'If toDate is null, period is valid from fromDate until the end of times.' ,
  `day` TINYINT NULL COMMENT 'If day is null, period spans all days.' ,
  `flightStartTime` TIME NOT NULL ,
  PRIMARY KEY (`idPeriod`) ,
  CONSTRAINT `period_flightTemplate_fk`
    FOREIGN KEY (`idAirlineCompany` , `idTemplate` )
    REFERENCES `mydb`.`Template` (`idAirlineCompany` , `idTemplate` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `flightTemplate_idx` ON `mydb`.`Period` (`idAirlineCompany` ASC, `idTemplate` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`Flight`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Flight` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`Flight` (
  `idFlight` INT NOT NULL AUTO_INCREMENT ,
  `cancelled` BIT(1) NOT NULL DEFAULT 0 ,
  `idBookable` INT NULL ,
  `idAirplaneType` INT NULL ,
  `idPeriod` INT NULL ,
  `idAirlineCompany` VARCHAR(3) NOT NULL ,
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
    FOREIGN KEY (`idAirlineCompany` , `idTemplate` )
    REFERENCES `mydb`.`Template` (`idAirlineCompany` , `idTemplate` )
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `flight_period_fk`
    FOREIGN KEY (`idPeriod` )
    REFERENCES `mydb`.`Period` (`idPeriod` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `AirplaneType_idx` ON `mydb`.`Flight` (`idAirplaneType` ASC) ;

CREATE INDEX `Template_idx` ON `mydb`.`Flight` (`idAirlineCompany` ASC, `idTemplate` ASC) ;

CREATE UNIQUE INDEX `idBookable_UNIQUE` ON `mydb`.`Flight` (`idBookable` ASC) ;

CREATE UNIQUE INDEX `idAirplaneType_UNIQUE` ON `mydb`.`Flight` (`idAirplaneType` ASC) ;

CREATE INDEX `flight_period_fk_idx` ON `mydb`.`Flight` (`idPeriod` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`SeatInstance`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`SeatInstance` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`SeatInstance` (
  `idFlight` INT NOT NULL AUTO_INCREMENT ,
  `airplaneType` INT NOT NULL ,
  `seatNumber` INT NOT NULL ,
  `price` DOUBLE NOT NULL ,
  PRIMARY KEY (`idFlight`, `airplaneType`, `seatNumber`) ,
  CONSTRAINT `seatinstance_seat_fk`
    FOREIGN KEY (`airplaneType` , `seatNumber` )
    REFERENCES `mydb`.`Seat` (`airplaneType` , `seatNumber` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `seatinstance_flight_fk`
    FOREIGN KEY (`idFlight` )
    REFERENCES `mydb`.`Flight` (`idFlight` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `bookingslot_seat_fk_idx` ON `mydb`.`SeatInstance` (`airplaneType` ASC, `seatNumber` ASC) ;

CREATE INDEX `seatinstance_flight_fk_idx` ON `mydb`.`SeatInstance` (`idFlight` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`Distance`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Distance` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`Distance` (
  `idFromCity` VARCHAR(3) NOT NULL ,
  `idToCity` VARCHAR(3) NOT NULL ,
  `distance` DOUBLE NOT NULL ,
  PRIMARY KEY (`idFromCity`, `idToCity`) ,
  CONSTRAINT `distance_fromAirport_fk`
    FOREIGN KEY (`idFromCity` )
    REFERENCES `mydb`.`Airport` (`code` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `distance_toAirport_fk`
    FOREIGN KEY (`idToCity` )
    REFERENCES `mydb`.`Airport` (`code` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `fromAirport_idx` ON `mydb`.`Distance` (`idFromCity` ASC) ;

CREATE INDEX `toAirport_idx` ON `mydb`.`Distance` (`idToCity` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`FlightTime`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`FlightTime` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`FlightTime` (
  `idFromCity` VARCHAR(3) NOT NULL ,
  `idToCity` VARCHAR(3) NOT NULL ,
  `idAirplaneType` INT NOT NULL ,
  `duration` TIME NOT NULL ,
  PRIMARY KEY (`idFromCity`, `idToCity`, `idAirplaneType`) ,
  CONSTRAINT `flightTime_fromAirport_fk`
    FOREIGN KEY (`idFromCity` )
    REFERENCES `mydb`.`Airport` (`code` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `flightTime_toAirport_fk`
    FOREIGN KEY (`idToCity` )
    REFERENCES `mydb`.`Airport` (`code` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `flightTime_airplaneType_fk`
    FOREIGN KEY (`idAirplaneType` )
    REFERENCES `mydb`.`AirplaneType` (`idAirplaneType` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `fromAirport_idx` ON `mydb`.`FlightTime` (`idFromCity` ASC) ;

CREATE INDEX `toAirport_idx` ON `mydb`.`FlightTime` (`idToCity` ASC) ;

CREATE INDEX `airplaneType_idx` ON `mydb`.`FlightTime` (`idAirplaneType` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`SeatInstanceTemplate`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`SeatInstanceTemplate` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`SeatInstanceTemplate` (
  `idAirlineCompany` VARCHAR(45) NOT NULL ,
  `idTemplate` DECIMAL(4,0) NOT NULL ,
  `airplaneType` INT NOT NULL ,
  `seatNumber` INT NOT NULL ,
  `price` DOUBLE NOT NULL ,
  PRIMARY KEY (`idTemplate`, `idAirlineCompany`, `airplaneType`, `seatNumber`) ,
  CONSTRAINT `seatinstancetemplate_seat_fk`
    FOREIGN KEY (`airplaneType` , `seatNumber` )
    REFERENCES `mydb`.`Seat` (`airplaneType` , `seatNumber` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `seatinstancetemplate_template`
    FOREIGN KEY (`idTemplate` , `idAirlineCompany` )
    REFERENCES `mydb`.`Template` (`idAirplaneType` , `idAirlineCompany` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `seatinstancetemplate_seat_fk_idx` ON `mydb`.`SeatInstanceTemplate` (`airplaneType` ASC, `seatNumber` ASC) ;

CREATE INDEX `seatinstancetemplate_template_idx` ON `mydb`.`SeatInstanceTemplate` (`idTemplate` ASC, `idAirlineCompany` ASC) ;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
