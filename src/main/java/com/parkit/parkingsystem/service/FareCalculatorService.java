package com.parkit.parkingsystem.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

/**
 * this class configure ticket price calculations according to the user's situation
 *
 */
public class FareCalculatorService {

	/**
	 * configure the different price calculations. Free if -30 min, calculate
	 * price per hour for bike and car. apply the reduction if the user is
	 * recurrent
	 * 
	 * @param ticket
	 */
	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null)) {
			throw new IllegalArgumentException("Out time provided is incorrect:"
					+ ticket.getOutTime().toString());
		}

		LocalDateTime dateArrival = ticket.getInTime();
		LocalDateTime dateDeparture = ticket.getOutTime();

		long difference = ChronoUnit.MINUTES.between(dateArrival,
				dateDeparture);
		double duration = ((double) difference / Fare.MINUTES_PER_HOUR);

		// Parking is free if user stay less than 30min
		if (duration < Fare.RATE_THIRTYMIN) {
			duration = Fare.FREE;
		}

		switch (ticket.getParkingSpot().getParkingType()) {
			case CAR : {
				ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
				// a discount is made if is cyclic user
				calculDiscount(ticket.getPrice(), ticket);
				break;
			}
			case BIKE : {
				ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
				calculDiscount(ticket.getPrice(), ticket);
				break;
			}
			default :
				throw new IllegalArgumentException("Unkown Parking Type");
		}
	}

	/**
	 * check if the discount is applied
	 * 
	 * @param price
	 * @param ticket
	 */
	public void calculDiscount(double price, Ticket ticket) {
		if (ticket.isDiscountPrice()) {
			double discount = (price * 5) / 100;
			price = price - discount;
		}
		ticket.setPrice(price);
	}

}