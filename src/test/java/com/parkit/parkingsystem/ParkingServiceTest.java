package com.parkit.parkingsystem;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

/**
 * this class contains the tests : spot and ticket parking test together car and
 * bike tests vehicle entry and exit procedures
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) 
public class ParkingServiceTest {

	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	/**
	 * 
	 */
	@BeforeEach
	private void setUpPerTestCar() {
		try {
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(inputReaderUtil.readVehicleRegistrationNumber())
					.thenReturn("ABCDEF");
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,
					false);
			Ticket ticket = new Ticket();
			ticket.setInTime(
					LocalDateTime.now(ZoneId.systemDefault()).minusHours(1));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");
			when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
			when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class)))
					.thenReturn(1);

			when(parkingSpotDAO.updateParking(any(ParkingSpot.class)))
					.thenReturn(true);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO,
					ticketDAO);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	@Test
	public void processExitingVehicleTest() throws Exception {
		parkingService.processExitingVehicle();
		verify(ticketDAO, Mockito.times(1)).getTicket(any());																											
		verify(parkingSpotDAO, Mockito.times(1))
				.updateParking(any(ParkingSpot.class));
	}

	@Test
	public void processIncomingVehicle() throws Exception {
		parkingService.processIncomingVehicle();
		verify(inputReaderUtil, Mockito.times(1))
				.readVehicleRegistrationNumber();
		verify(parkingSpotDAO, Mockito.times(1))
				.updateParking(any(ParkingSpot.class));
		verify(ticketDAO, Mockito.times(1)).cyclicUser(anyString());
		verify(ticketDAO, Mockito.times(1)).saveTicket(any());
	}

}
