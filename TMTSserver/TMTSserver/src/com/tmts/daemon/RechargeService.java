/**
 * 
 */
package com.tmts.daemon;

import java.io.StringReader;
import java.sql.CallableStatement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.tmts.utils.TMTSLogger;

/**
 * @author supporttmts
 *
 */
public class RechargeService {
	public static String CODE = "RC";
	public static String CODE1 = "DTH";
	public static String CODE2 = "RV";
	public static String CODE3 = "DTHV";
	static String rsltcode = "";
	static String rsltdscp = "";
	static String rslttraid = "";
	static String gId = "";
	String errcode="";
	ServiceParms m_sp;
	String service ;
	
	public RechargeService(ServiceParms sp) {
		m_sp = sp;
	}
	
	public boolean processRechargeRequest(){
		Connection conn = DaemonStaticResource.getConnection();
		Statement stm1 = null;
		Statement stm = null;
		ResultSet rs1 = null;
		String tran_id = "-1";
		Calendar cal = Calendar.getInstance();
		final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";		
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        
    	try{
    		 stm1 = conn.createStatement();
    		 stm = conn.createStatement();
    		 rs1 = stm1.executeQuery(" SELECT r.retailer_id ,(date_format(sysdate(), '%H%i%s')) generated_id FROM retailer r, trateluser t WHERE t.username = substr("+ m_sp.getFrmNo() +",3,10) AND r.tmts_userid = t.tmts_userid ");
    		int retId = -1;
    		int timeStp = -1;
    		
    		while(rs1.next()){
				retId = Integer.parseInt(rs1.getString("retailer_id"));
				timeStp = Integer.parseInt(rs1.getString("generated_id"));
				
			}
    		gId = String.valueOf(retId)+String.valueOf(timeStp);
    		
    		long fNo = Long.parseLong(m_sp.getFrmNo());
	    	long tNo = Long.parseLong(m_sp.getToNo());
	    	int amt = Integer.parseInt(m_sp.getAmt());	
	    	service = m_sp.getService();
    		
	    	
		CallableStatement cstm = conn.prepareCall("{ call gotit_valid_check( ?,?,?,?,?,?,?,?,?)  }");
    	cstm.setLong(1, fNo);
    	cstm.setLong(2, tNo);
    	cstm.setInt(3, amt);
    	cstm.setString(4, service); 
    	cstm.setString(5, m_sp.getOprCode());
    	cstm.setString(6, m_sp.getOprCirCode());
    	cstm.registerOutParameter(7, java.sql.Types.VARCHAR);
    	cstm.registerOutParameter(8, java.sql.Types.VARCHAR);
    	cstm.registerOutParameter(9, java.sql.Types.VARCHAR);
    	cstm.execute();
    	String resVldCheck = cstm.getString(7);
    	String prodCode = cstm.getString(8);
    	String oprCode = cstm.getString(9);
    	if (resVldCheck.equalsIgnoreCase("VALID") && oprCode != null && prodCode != null){
    		String x = "<mbanking>  <header><requesttype>TOPUP</requesttype></header><request><agentcode>"+DaemonStaticResource.getAgentCode()+"</agentcode><pin>"+DaemonStaticResource.getPin()+"</pin><source>"+m_sp.getToNo()+"</source><agenttransid>"+gId+"</agenttransid><opragentcode>"+oprCode+"</opragentcode><vendorcode>OLR</vendorcode><productcode>"+prodCode+"</productcode><amount>"+amt+"</amount><requestcts>"+ sdf.format(cal.getTime()).toString()+"</requestcts><clienttype>HTTP</clienttype><pgtransname>TMTS</pgtransname><pgtransid>"+gId+"</pgtransid><agentname>TMTS</agentname><comments>Topup request</comments><udv1/><udv2/><udv3/><udv4/></request></mbanking>";
    		//String respXML = RechargeRequest.requestProcess(x,DaemonStaticResource.getIpAddress(),DaemonStaticResource.getPortNo());
			//if(respXML != null || respXML != ""){
				   try{
				//	parseXML(respXML);
					cstm = conn.prepareCall("{ call gotit_do_recharge( ?,?,?,?,?,?,?,?,?,?)  }");
					cstm.setString(1, m_sp.getFrmNo());
			    	cstm.setString(2, m_sp.getToNo());
			    	cstm.setInt(3, Integer.parseInt(m_sp.getAmt()));
			    	cstm.setString(4, service);
			    	cstm.setString(5, m_sp.getOprCode());
			    	cstm.setString(6,  m_sp.getOprCirCode());
			    	cstm.setString(7, rsltcode); 					//"0"
			    	cstm.setString(8, rsltdscp);                    //"Transaction Successful"
			    	cstm.setString(9, gId);
			    	cstm.setString(10,rslttraid);				    //"11111111"	
			    	cstm.execute();
			    	System.out.println("-1-"+m_sp.getFrmNo()+"-2-"+m_sp.getToNo()+"-3-"+Integer.parseInt(m_sp.getAmt())+"-4-"+service+"-5-"+rsltcode+"-6-"+rsltdscp+"-7-"+gId+"-8-"+rslttraid);    	
					}catch(Exception e){
						errcode = "-100";
						System.out.println("respXML-----"+errcode);
						//e.printStackTrace();
			    	}
			//}
			stm.executeUpdate("update sms_service set status='COMPLETED' where sms_service_id = "+m_sp.getSms_sid());  //from_no= "+ m_sp.getFrmNo()  +" and service = '" + CODE + "'"
		}
    	}catch(Exception e)	{	
    		TMTSLogger.logErr(e.getMessage() );
    		return false;
		} finally {
			try {
				if(stm1 != null)
					stm1.close();
				
				if(rs1 != null)
					rs1.close();				
				
				stm.executeUpdate("update sms_service set status='COMPLETED' where sms_service_id = "+m_sp.getSms_sid()); 
				if(stm1 != null)
					stm.close();
		   		 
			} catch (SQLException e) {
				TMTSLogger.logErr(e.getMessage() );
				return false;
			}
   		
		}
		return true;		
	}
	
	
	public static boolean parseXML(String ResponseStr) {
	boolean resultcode = false;
	try {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(ResponseStr));

		Document doc = db.parse(is);
		String logstring = "";
		NodeList result = doc.getElementsByTagName("resultcode");
		Element line = (Element) result.item(0);
		rsltcode = getCharacterDataFromElement(line); 
		System.out.println("result code: "+ rsltcode);

		NodeList result1 = doc.getElementsByTagName("resultdescription");
		Element line1 = (Element) result1.item(0);
        rsltdscp = getCharacterDataFromElement(line1);
		System.out.println("resultdescription: "+rsltdscp);

		NodeList result2 = doc.getElementsByTagName("transid");
		Element line2 = (Element) result2.item(0);
		rslttraid = getCharacterDataFromElement(line2);
		System.out.println("resultdescription: "+rslttraid);
		
		if (Integer.parseInt(getCharacterDataFromElement(line)) == 0)
			resultcode = true;

		result = doc.getElementsByTagName("transid");
		line = (Element) result.item(0);

		System.out.println("value: " + getCharacterDataFromElement(line));

	} catch (Exception e) {
		e.printStackTrace();
	}

	finally {
		return resultcode;
	}

}

public static String getCharacterDataFromElement(Element e) {
	Node child = e.getFirstChild();
	if (child instanceof CharacterData) {
		CharacterData cd = (CharacterData) child;
		return cd.getData();
	}
	return "?";
  }
	
	
}
