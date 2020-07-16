package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * this class contains the methods associated with ParkingService. 
 *
 */
public class ParkingService {

	private static final Logger logger = LogManager.getLogger("ParkingService");

	private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

	private InputReaderUtil inputReaderUtil;
	private ParkingSpotDAO parkingSpotDAO;
	private TicketDAO ticketDAO;

	/**
	 * service parking builder
	 * 
	 * @param inputReaderUtil
	 * @param parkingSpotDAO
	 * @param ticketDAO
	 */
	public ParkingService(InputReaderUtil inputReaderUtil,
			ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
		this.inputReaderUtil = inputReaderUtil;
		this.parkingSpotDAO = parkingSpotDAO;
		this.ticketDAO = ticketDAO;
	}

	/**
	 * vehicle entry procedure. Check if the user has already come. load the
	 * appropriate messages
	 */
	public void processIncomingVehicle() {
		try {
			ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
			if (parkingSpot != null && parkingSpot.getId() > 0) {
				String vehicleRegNumber = getVehichleRegNumber();
				parkingSpot.setAvailable(false);
				parkingSpotDAO.updateParking(parkingSpot);

				LocalDateTime inTime = LocalDateTime
						.now(ZoneId.systemDefault());
				Ticket ticket = new Ticket();
				if (ticketDAO.cyclicUser(vehicleRegNumber)) {
					System.out.println(
							"Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.");
					ticket.setDiscountPrice(true);
				} else {
					ticket.setDiscountPrice(false);
				}
				ticket.setParkingSpot(parkingSpot);
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(0);
				ticket.setInTime(inTime);
				ticketDAO.saveTicket(ticket);
				System.out.println("Generated Ticket and saved in DB");
				System.out.println("Please park your vehicle in spot number:"
						+ parkingSpot.getId());
				System.out.println("Recorded in-time for vehicle number:"
						+ vehicleRegNumber + " is:" + ticket.getInTime());
			}
		} catch (Exception e) {
			logger.error("Unable to process incoming vehicle", e);
		}
	}

	/**
	 * tells the user to enter keypad vehicle registration number
	 * 
	 * @return Message for user
	 * @throws Exception
	 */
	private String getVehichleRegNumber() throws Exception {
		System.out.println(
				"Please type the vehicle registration number and press enter key");
		return inputReaderUtil.readVehicleRegistrationNumber();
	}

	/**
	 * gives the user their parking slots number
	 * 
	 * @return parkingspot
	 */
	public ParkingSpot getNextParkingNumberIfAvailable() {
		int parkingNumber = 0;
		ParkingSpot parkingSpot = null;
		try {
			ParkingType parkingType = getVehichleType();
			parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
			if (parkingNumber > 0) {
				parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
			} else {
				throw new Exception(
						"Error fetching parking number from DB. Parking slots might be full");
			}
		} catch (IllegalArgumentException ie) {
			logger.error("Error parsing user input for type of vehicle", ie);
		} catch (Exception e) {
			logger.error("Error fetching next available parking slot", e);
		}
		return parkingSpot;
	}

	/**
	 * choice of vehicle type
	 * 
	 * @return parking type
	 */
	private ParkingType getVehichleType() {
		System.out.println("Please select vehicle type from menu");
		System.out.println("1 CAR");
		System.out.println("2 BIKE");
		int input = inputReaderUtil.readSelection();
		switch (input) {
			case 1 : {
				return ParkingType.CAR;
			}
			case 2 : {
				return ParkingType.BIKE;
			}
			default : {
				System.out.println("Incorrect input provided");
				throw new IllegalArgumentException("Entered input is invalid");
			}
		}
	}

	/**
	 * vehicle exit procedure: free the parking space, calculate the price and
	 * change the format of the price in €
	 * 
	 */
	public void processExitingVehicle() {
		try {
			String vehicleRegNumber = getVehichleRegNumber();
			Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
			LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
			ticket.setOutTime(outTime);
			fareCalculatorService.calculateFare(ticket);
			if (ticketDAO.updateTicket(ticket)) {
				ParkingSpot parkingSpot = ticket.getParkingSpot();
				parkingSpot.setAvailable(true);
				parkingSpotDAO.updateParking(parkingSpot);
				// formatting double type result in string type with two digits
				// after the decimal point
				NumberFormat format = NumberFormat.getInstance();
				format.setMaximumFractionDigits(2);
				String fare = format.format(ticket.getPrice());
				System.out
						.println("Please pay the parking fare: " + fare + "€");
				System.out.println("Recorded out-time for vehicle number:"
						+ ticket.getVehicleRegNumber() + " is:" + outTime);
			} else {
				System.out.println(
						"Unable to update ticket information. Error occurred");
			}
		} catch (Exception e) {
			logger.error("Unable to process exiting vehicle", e);
		}
	}
}
