package com.tmts.outgoing;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.sql.PreparedStatement;
import com.tmts.daemon.DaemonStaticResource;
import com.tmts.daemon.ServiceParms;
import com.tmts.utils.TMTSLogger;

public class TMTSOUTGOINGService {

private ArrayList<OUTGOINGParams> o_ServiceRequest = new ArrayList<OUTGOINGParams>();
Connection m_conn  = DaemonStaticResource.getConnection();
	
	public void responseProcessor() {
		loadOutgoingResponse(m_conn);
		synchronized (o_ServiceRequest) {
			processRequest();
			
		}
    }
	
public void processRequest(){    
	
		try{
			Iterator<OUTGOINGParams> requestIterator = o_ServiceRequest.iterator();		
			if( o_ServiceRequest.size() <= 0 ) {
				 TMTSLogger.log("No sms request found for processing.");
			}
			int totalRequest = o_ServiceRequest.size();
			while (requestIterator.hasNext()) {
				 OUTGOINGParams op = (OUTGOINGParams)requestIterator.next();
				 TMTSLogger.log("SMS request transaction sms id  " + op.getO_trans_sms_id() + " Processing request no. " + (totalRequest - o_ServiceRequest.size()) + " of " + totalRequest ) ;
				 String O_smsText = op.getO_smsText();
				 String O_sentTo = op.getO_sentTo();
				new WebRequest(op);
				TMTSLogger.log( "Request suceeded for " + O_sentTo +" SMS_STATUS-----"+" N ");
			    requestIterator.remove();
			 }
			o_ServiceRequest.clear();
		}catch(Exception e){
			TMTSLogger.log( e.getMessage() );
		}
 }
	private void loadOutgoingResponse(Connection conn) {
		String smsText=""; 
		String sentTo="";
		long trans_sms_id;
		Statement stm = null;
		ResultSet rs = null;
		o_ServiceRequest.clear();
		PreparedStatement pstmt = null;

		TMTSLogger.log( "Finding request for processing ... " );
		try{
			stm = conn.createStatement();
			String query ="select transaction_sms_id,sms_text,sent_to from transaction_sms where sms_status=? order by transaction_sms_id limit " + DaemonStaticResource.getNumberOfRequestToLoad();
		    pstmt = conn.prepareStatement(query); // create a statement
	        pstmt.setString(1,"Y"); // set input parameter
	        rs = pstmt.executeQuery();
             while (rs.next()) {
				smsText = rs.getString("sms_text");
				sentTo = rs.getString("sent_to");
				trans_sms_id = rs.getLong("transaction_sms_id");
				o_ServiceRequest.add(new OUTGOINGParams(smsText,sentTo,trans_sms_id));
			}
			TMTSLogger.log( "Total no. of request loaded for processing " +  o_ServiceRequest.size()  );
		 }
		catch(SQLException e){
			 TMTSLogger.log( e.getMessage() );
			 
		}
		finally {
			try {
				stm.close();
				rs.close();
				pstmt.close();
		        
			} catch (SQLException e) {
				TMTSLogger.log( e.getMessage() );
			}
			
		}
		
		
	}

}
