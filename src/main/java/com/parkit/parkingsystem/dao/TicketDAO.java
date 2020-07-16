package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

/**
 * this class configures the save, update and get ticket methods
 *
 */
public class TicketDAO {

	private static final Logger logger = LogManager.getLogger("TicketDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	/**
	 * Save ticket to database
	 * 
	 * @param ticket
	 * @return boolean
	 */
	public boolean saveTicket(Ticket ticket) {
		Connection con = null;
		PreparedStatement ps = null;
		if (ticket == null) {
			throw new NullPointerException("invalid request");
		}
		try {

			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.SAVE_TICKET);
			ps.setInt(1, ticket.getParkingSpot().getId());
			ps.setString(2, ticket.getVehicleRegNumber());
			ps.setDouble(3, ticket.getPrice());
			ps.setTimestamp(4, Timestamp.valueOf(ticket.getInTime()));
			ps.setTimestamp(5,
					(ticket.getOutTime() == null)
							? null
							: Timestamp.valueOf(ticket.getOutTime()));
			ps.setBoolean(6, ticket.isDiscountPrice());
			return ps.execute();
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);

		}
		return false;
	}

	/**
	 * configuration Get Ticket
	 * 
	 * @param vehicleRegNumber
	 * @return ticket
	 * @throws Exception
	 */
	public Ticket getTicket(String vehicleRegNumber) throws Exception {
		Connection con = null;
		Ticket ticket = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.GET_TICKET);
			ps.setString(1, vehicleRegNumber);
			rs = ps.executeQuery();

			if (rs.next()) {
				ticket = new Ticket();
				ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1),
						ParkingType.valueOf(rs.getString(7)), false);
				ticket.setParkingSpot(parkingSpot);
				ticket.setId(rs.getInt(2));
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(rs.getDouble(3));
				ticket.setInTime(rs.getTimestamp(4).toLocalDateTime());
				ticket.setOutTime((rs.getTimestamp(5) == null)
						? null
						: rs.getTimestamp(5).toLocalDateTime());
				ticket.setDiscountPrice(rs.getBoolean(6));
			} else {
				throw new Exception();
			}
		} catch (Exception ex) {
			throw new Exception("invalid request");
		} finally {
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
		return ticket;
	}

	/**
	 * For Update Ticket
	 * 
	 * @param ticket
	 * @return boolean
	 */
	public boolean updateTicket(Ticket ticket) {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
			ps.setDouble(1, ticket.getPrice());
			ps.setTimestamp(2, Timestamp.valueOf(ticket.getOutTime()));
			ps.setInt(3, ticket.getId());
			ps.execute();
			return true;
		} catch (Exception ex) {
			logger.error("Error saving ticket info", ex);
		} finally {
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);

		}
		return false;
	}

	/**
	 * Check if is Cyclic User, check VehicleRegNumber
	 * 
	 * @param vehicleRegNumber
	 * @return boolean
	 */
	public boolean cyclicUser(String vehicleRegNumber) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.CYCLIC_USER);
			ps.setString(1, vehicleRegNumber);
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getBoolean(1);
			}
		} catch (Exception ex) {
			logger.error("Error identification User", ex);
		} finally {
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);

		}
		return false;
	}

	/**
	 * Query used for integration test
	 * 
	 * @param ticket
	 * @return boolean
	 */
	public boolean upTicketITTest(Ticket ticket) {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(
					DBConstants.TestRequestIT); //update ticket set IN_TIME=? where ID=?
			ps.setTimestamp(1, Timestamp.valueOf(ticket.getInTime()));
			ps.setInt(2, ticket.getId());
			ps.execute();
			return true;
		} catch (Exception ex) {
			logger.error("Error saving ticket info", ex);
		} finally {
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);

		}
		return false;
	}

}
