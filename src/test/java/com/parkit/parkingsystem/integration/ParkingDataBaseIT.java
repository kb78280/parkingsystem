package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

/**
 * integration test: car park, bike park, car exit, bike exit
 *
 */
@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static FareCalculatorService fareCalculatorService;
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber())
				.thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void testParkingCar() {
		ParkingService parkingService = new ParkingService(inputReaderUtil,
				parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		try {
			Ticket ticket = ticketDAO.getTicket("ABCDEF");
			// check that a ticket is actualy saved in DB and Parking table is
			// updated with availability
			assertNotNull(ticket);
			assertNotEquals(1,
					parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
			assertEquals(2,
					parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
			// check that a ticket is not available for discount fare
			assertFalse(ticket.isDiscountPrice());
			ticket.setInTime(
					LocalDateTime.now(ZoneId.systemDefault()).minusHours(1));
			ticketDAO.upTicketITTest(ticket);
			parkingService.processExitingVehicle();

			// same vehicle new coming
			parkingService.processIncomingVehicle();
			Ticket ticket2 = ticketDAO.getTicket("ABCDEF");
			// check that ticket is available for discount fare
			assertTrue(ticket2.isDiscountPrice());
			// exit 1 hour later
			ticket2.setOutTime(
					LocalDateTime.now(ZoneId.systemDefault()).plusHours(1));
			ticketDAO.updateTicket(ticket2);
			// calcul fare
			fareCalculatorService.calculateFare(ticket2);
			ticketDAO.updateTicket(ticket2);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testParkingBike() {
		ParkingService parkingService = new ParkingService(inputReaderUtil,
				parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		try {
			Ticket ticket = ticketDAO.getTicket("ABCDEF");
			// check that a ticket is actualy saved in DB and Parking table is
			// updated with availability
			assertNotNull(ticket);
			assertNotEquals(1,
					parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE));
			assertEquals(4,
					parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE));
			// check that a ticket is not available for discount fare
			assertFalse(ticket.isDiscountPrice());
			ticket.setInTime(
					LocalDateTime.now(ZoneId.systemDefault()).minusHours(1));
			ticketDAO.upTicketITTest(ticket);
			parkingService.processExitingVehicle();

			// same vehicle new coming
			parkingService.processIncomingVehicle();
			Ticket ticket2 = ticketDAO.getTicket("ABCDEF");
			// check that ticket is available for discount fare
			assertTrue(ticket2.isDiscountPrice());
			// exit 1 hour later
			ticket2.setOutTime(
					LocalDateTime.now(ZoneId.systemDefault()).plusHours(1));
			ticketDAO.updateTicket(ticket2);
			// calcul fare
			fareCalculatorService.calculateFare(ticket2);
			ticketDAO.updateTicket(ticket2);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testParkingCarLotExit() {
		testParkingCar();
		try {
			Ticket ticket = ticketDAO.getTicket("ABCDEF");

			// check that the fare generated and out time are populated
			// correctly in the database
			assertNotNull(ticket.getPrice()); // discount fare
			assertNotNull(ticket.getOutTime());

			ParkingSpot parkingSpot = ticket.getParkingSpot();
			parkingSpot.setAvailable(true);
			assertTrue(parkingSpotDAO.updateParking(parkingSpot));
			// check that the spot is now available
			assertEquals(1,
					parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testParkingBikeLotExit() {
		testParkingBike();
		try {
			Ticket ticket = ticketDAO.getTicket("ABCDEF");

			// check that the fare generated and out time are populated
			// correctly in the database
			assertNotNull(ticket.getPrice()); // discount fare
			assertNotNull(ticket.getOutTime());

			ParkingSpot parkingSpot = ticket.getParkingSpot();
			parkingSpot.setAvailable(true);
			assertTrue(parkingSpotDAO.updateParking(parkingSpot));
			// check that the spot is now available
			assertEquals(4,
					parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
