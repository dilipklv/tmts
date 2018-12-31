package com.tmts.daemon;

import java.sql.*;
import java.util.*; 

import com.tmts.utils.TMTSLogger;

public class TMTSService {	
	
	private ArrayList<ServiceParms> m_ServiceRequest = new ArrayList<ServiceParms>();
	
	Connection m_conn  = DaemonStaticResource.getConnection();
	
	public void requestProcessor() {
		loadServiceRequest(m_conn);	
		synchronized (m_ServiceRequest) {
			processRequest();
		}
		
	}
	
	public void processRequest(){    			
		
		try{
					
			Iterator<ServiceParms> requestIterator = m_ServiceRequest.iterator();		
			if( m_ServiceRequest.size() <= 0 ) {
				 TMTSLogger.log("No request found for processing.");
			}			
			int totalRequest = m_ServiceRequest.size();
			 while (requestIterator.hasNext()) {
				 ServiceParms sp = (ServiceParms)requestIterator.next();
				 TMTSLogger.log("SMS request service id  " + sp.getSms_sid() + " Processing request no. " + (totalRequest - m_ServiceRequest.size()) + " of " + totalRequest ) ;
				 
			     String FrmNo = sp.getFrmNo();
				 String ToNo = sp.getToNo();
				 String Amt = sp.getAmt();
				 String serviceType = sp.getService();
			     String requestStatus = sp.getStatus();
			     long sms_sid = sp.getSms_sid();		    		     
			     
			     //for BALANCE
			  
			    if( BalanceService.CODE.equalsIgnoreCase( serviceType ) && "PENDING".equalsIgnoreCase(requestStatus)){
			    	TMTSLogger.log("Mobile No. " + sp.getFrmNo() + " request type -- " + serviceType + "   Status -- " + requestStatus);
			    	BalanceService balService = new BalanceService( sp );
			    	
			    	boolean success = balService.processBalanceRequest( );
			    	String msg = "";
			    	if(success) {
			    		 msg = "Request type BAL suceeded for " + FrmNo + "    Status -- " + "COMPLETED";
			    	} else {
			    		msg = "Request type BAL failed for " + FrmNo + "    Status -- " + "COMPLETED";
			    	}
			    	TMTSLogger.log( msg );			    	
				  }
			    
			    //for RECHARGE
			    if((RechargeService.CODE.equalsIgnoreCase( serviceType )||RechargeService.CODE1.equalsIgnoreCase( serviceType ) || (RechargeService.CODE2.equalsIgnoreCase( serviceType )||RechargeService.CODE3.equalsIgnoreCase( serviceType ))) &&  "PENDING".equalsIgnoreCase(requestStatus)){
			    	TMTSLogger.log("Mobile No. " + sp.getFrmNo() + " request type -- " + serviceType + " to Mobile No.  " + sp.getToNo() + "   Status -- " + requestStatus);
			    	RechargeService rechargeService =	new RechargeService(sp);
			    	boolean success =  rechargeService.processRechargeRequest();
			    	String msg = "";
			    	if(success) {
			    		 msg = "Request type RECHARGE suceeded for " + sp.getFrmNo() + "  to mobile number  " + sp.getToNo()+ " generated_id " + RechargeService.gId;
			    	} else {
			    		msg = "Request type RECHARGE failed for " + sp.getFrmNo() + "  to mobile number  " + sp.getToNo() ;
			    	}
			    	TMTSLogger.log( msg );	
			    	
			     }
			    
			  //for LOAD
			    if(LoadService.CODE.equalsIgnoreCase( serviceType ) &&  "PENDING".equalsIgnoreCase(requestStatus)){
			    	TMTSLogger.log("Mobile No. " + sp.getFrmNo() + " request type -- " + serviceType + " to Mobile No.  " + sp.getToNo() + "   Status -- " + requestStatus);
			    	LoadService rechargeService =	new LoadService(sp);
			    	boolean success =  rechargeService.processLoadRequest();
			    	String msg = "";
			    	if(success) {
			    		 msg = "Request type LOAD suceeded for " + sp.getFrmNo() + "  to mobile number  " + sp.getToNo();
			    	} else {
			    		msg = "Request type LOAD failed for " + sp.getFrmNo() + "  to mobile number  " + sp.getToNo() ;
			    	}
			    	TMTSLogger.log( msg );	
			    	
			     }
			    
			    
				 requestIterator.remove();
			    }
			 
			 
			 m_ServiceRequest.clear();
			
		}catch(Exception e){
			TMTSLogger.log( e.getMessage() );
		}
		
		//return service;
	}
	/**
	 * @param conn
	 * @throws SQLException
	 */
	private void loadServiceRequest(Connection conn) {
		
		String service;
		String status;
		String frmNo;
		String toNo;
		String amt;		
		long  sms_sid;
		String oprCode;
		String oprCirCode;
		
		Statement stm = null;
		ResultSet rs = null;
		m_ServiceRequest.clear();
		TMTSLogger.log( "Finding request for processing ... " );
		try {
			stm = conn.createStatement();
			rs = stm.executeQuery("select  sms_service_id,from_no,dest_no,amount,service,status,operator_code,operator_circle_code from sms_service where status='PENDING' order by sms_service_id limit " + DaemonStaticResource.getNumberOfRequestToLoad()); // 
			while (rs.next()) {
				frmNo = rs.getString("from_no");
				toNo = rs.getString("dest_no");
				amt = rs.getString("amount");
				service = rs.getString("service");
				status = rs.getString("status");
				sms_sid = rs.getLong("sms_service_id");
				oprCode  = rs.getString("operator_code");
				oprCirCode  = rs.getString("operator_circle_code");
				m_ServiceRequest.add(new ServiceParms(frmNo, toNo, amt, service,status, sms_sid,oprCode,oprCirCode));
			}
			TMTSLogger.log( "Total no. of request loaded for processing " +  m_ServiceRequest.size()  );
		} catch (SQLException e) {			
			TMTSLogger.log( e.getMessage() );
		} finally {
			try {
				stm.close();
				rs.close();
			} catch (SQLException e) {
				TMTSLogger.log( e.getMessage() );
			}
			
		}
	}
	

}

