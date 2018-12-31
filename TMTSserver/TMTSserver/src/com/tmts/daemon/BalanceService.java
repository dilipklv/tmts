/**
 * 
 */
package com.tmts.daemon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.CallableStatement;
import com.tmts.utils.TMTSLogger;

/**
 * @author supporttmts
 *
 */
public class BalanceService implements IService {

	public static String CODE = "BL";
	
	private ServiceParms m_sp;
	public BalanceService(ServiceParms sp) {
		m_sp = sp;
	}
		
	public boolean processBalanceRequest(){
		
		String balanceRequestMobileNo = m_sp.getFrmNo();
		try {
			long mobileNo = Long.parseLong(balanceRequestMobileNo);
		} catch (Exception e) {
			TMTSLogger.logErr("Not a valid mobile number - "
					+ balanceRequestMobileNo);
			TMTSLogger.logErr("Request Balance failed for mobile no.-- "
					+ balanceRequestMobileNo);
			return false;
		}

		java.sql.CallableStatement stmt = null;
		PreparedStatement pstmt = null;	
		Connection conn = DaemonStaticResource.getConnection();
		try {
			stmt = conn.prepareCall("{ call GOTIT_BALENQUIRY(?)  }");
			stmt.setString(1, balanceRequestMobileNo);
			stmt.execute();
			
		} catch (Exception e) {
			System.out.println("Inside Balance Exception" + e);
		} finally {
			try {
				if(stmt != null)
					stmt.close();	
				pstmt =conn.prepareCall("update sms_service set status='COMPLETED' where sms_service_id = ?  and service = '" + CODE + "'");
				pstmt.setLong(1, m_sp.getSms_sid());
				pstmt.execute();
				
				if(pstmt != null)
					pstmt.close();
			} catch (SQLException e) {				
				TMTSLogger.logErr("Balance request failed for mobile no. -- " + balanceRequestMobileNo + " Reason --"  +e.getMessage());
				return false;
			}
			
		}
		return true;
	}
	
	
}
