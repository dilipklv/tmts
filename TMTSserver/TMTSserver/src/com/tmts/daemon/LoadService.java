/**
 * 
 */
package com.tmts.daemon;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.tmts.utils.TMTSLogger;

/**
 * @author supporttmts
 *
 */
public class LoadService {

public static String CODE = "LOAD";
	
	ServiceParms m_sp;
	Connection m_conn ;
	
	public LoadService(ServiceParms sp) {
		m_sp = sp;
		m_conn = DaemonStaticResource.getConnection();
	}
	
	public boolean processLoadRequest(){
		
		long fNo = Long.parseLong(m_sp.getFrmNo()  );
    	long tNo = Long.parseLong(m_sp.getToNo());
    	int amt = Integer.parseInt(m_sp.getAmt());
    	CallableStatement cstm = null;
    	Statement stmt = null;
    	
    	try{
    		cstm = m_conn.prepareCall("{ call gotit_retailer_load( ?,?,?)  }");
    		cstm.setLong(1, tNo);
    		cstm.setLong(2, fNo);
    		cstm.setInt(3, amt);
    		cstm.execute();    		
    		
    		 		
    	}catch(Exception e){
    		TMTSLogger.logErr(e.getMessage() );
    		return false;
    	} finally {
    		try {
    			if(cstm != null)
    				cstm.close();
    			
    			stmt = m_conn.createStatement();
            	stmt.executeUpdate("update sms_service set status='COMPLETED' where sms_service_id = "+ m_sp.getSms_sid());  // + " and from_no= "+m_sp.getFrmNo()+" and service = '" + CODE + "'"
       
    			if(stmt != null)
    				stmt.close();
			} catch (SQLException e) {
				TMTSLogger.logErr(e.getMessage() );
				return false;
			}
    		
    	}
    	
		return true;
		
	}
		
}
