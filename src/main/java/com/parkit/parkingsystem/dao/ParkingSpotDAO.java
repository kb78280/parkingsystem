package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * this class checks the configuration of the available parking slot
 *
 */
public class ParkingSpotDAO {
	private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	/**
	 * check next available slot
	 * 
	 * @param parkingType
	 * @return int
	 */
	public int getNextAvailableSlot(ParkingType parkingType) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int result = -1;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
			ps.setString(1, parkingType.toString());
			rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);;
			}
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);

		}
		return result;
	}

	/**
	 * update the availability for that parking slot
	 * 
	 * @param parkingSpot
	 * @return boolean
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	public boolean updateParking(ParkingSpot parkingSpot)
			throws ClassNotFoundException, SQLException, IOException {

		Connection con = null;

		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con
					.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
			try {
				ps.setBoolean(1, parkingSpot.isAvailable());
				ps.setInt(2, parkingSpot.getId());
				int updateRowCount = ps.executeUpdate();
				return (updateRowCount == 1);
			} finally {
				if (ps != null) {
					ps.close();
				}
				dataBaseConfig.closeConnection(con);
			}
		} catch (SQLException e) {
			logger.error("Error updating parking info", e);
			return false;

		}
	}

}
