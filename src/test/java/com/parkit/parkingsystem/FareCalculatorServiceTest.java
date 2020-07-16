package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * this class contains the calculates test : car, bike, unknown type, car and
 * bike less than an hour (45 min), car and bike 1 day, car and bike 30 min, car
 * and bike less than 30 min (free parking), car and bike for recurrent users ,
 * car and bike for no recurrent users, car and bike for recurrent users and
 * less than 30 min (with discount false)
 */
public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;

	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();
	}

	@Test
	public void calculateFareCar() {
		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusHours(1);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	public void calculateFareBike() {
		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusHours(1);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
	}

	@Test
	public void calculateFareUnkownType() {
		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusHours(1);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(NullPointerException.class,
				() -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	public void calculateFareCarWithLessThanOneHour() {
		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusMinutes(45);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithLessHalfAnHour() {

		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusMinutes(45);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithMoreThanADay() { //
		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusDays(1);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithMoreThanADay() {

		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusDays(1);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((24 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithHalfAnHour() {
		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusMinutes(30);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0.5 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithHalfAnHour() {
		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusMinutes(30);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0.5 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithLessThanAnHalfHourIsFree() {
		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusMinutes(15);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(Fare.FREE, ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithLessThanAnHalfHourIsFree() {
		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusMinutes(15);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(Fare.FREE, ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithCyclicUser() {
		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusHours(1);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscountPrice(true);
		double discount = (Fare.CAR_RATE_PER_HOUR * 5) / 100;
		fareCalculatorService.calculateFare(ticket);
		assertEquals((1.5 - discount), ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithCyclicUser() {
		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusHours(1);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscountPrice(true);
		double discount = (Fare.BIKE_RATE_PER_HOUR * 5) / 100;
		fareCalculatorService.calculateFare(ticket);
		assertEquals((1 - discount), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithNoCyclictUser() {
		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusHours(1);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscountPrice(false);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((1 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithNoCyclicUser() {
		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusHours(1);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscountPrice(false);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((1 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithFreeAndDiscount() {
		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusMinutes(15);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscountPrice(true);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(Fare.FREE, ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithFreeAndDiscount() {
		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusMinutes(15);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscountPrice(true);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(Fare.FREE, ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithFree() {
		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusMinutes(15);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscountPrice(false);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(Fare.FREE, ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithFree() {
		LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault())
				.minusMinutes(15);
		LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscountPrice(false);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(Fare.FREE, ticket.getPrice());
	}

}
