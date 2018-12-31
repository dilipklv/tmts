package com.tmts.daemon;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

//import org.apache.tools.ant.types.resources.comparators.Date;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.CharacterData;
import org.xml.sax.InputSource;
import java.net.*;
import java.io.*;

public class RechargeRequest {

	public static String requestProcess(String x1,String ipAdds,int servprt) {
		//requestProcess(Long MNumber,String prdt,String DenoID,String generated_id,String agentCode,String pin,String vendorcode,String dateFrmt,String ipAdds,int servprt) 
		// WIll be provided by Mamta
		int serverPort = servprt; //6105 make sure you give the port number on which
								// the server is listening.
		String address = ipAdds; //10.10.10.10 this is the IP address of the server
										// program's computer. // the address
										// given here means
										// "the same computer as the client".
		String subscriber_id = "";
		String gateway_id = "";

		String ResponseStr = "";

		try {
			java.util.Calendar cal = java.util.Calendar.getInstance();
			java.text.SimpleDateFormat sft1 = new java.text.SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");

			InetAddress ipAddress = InetAddress.getByName(address); // create an
																	// object
																	// that
																	// represents
																	// the above
																	// IP
																	// address.

			Socket socket = new Socket(ipAddress, serverPort); // create a
																// socket with
																// the server's
																// IP address
																// and server's
																// port.
			socket.setKeepAlive(true);
			System.out.println("start of the program");

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter sout = new PrintWriter(socket.getOutputStream(), true);
			String logstring1 = "";
//			String XMLRetString = "<mbanking>" + "<header>"
//					+ "<requesttype>TOPUP</requesttype>" + "</header>"
//					+ "<request>" + "<agentcode>"+agentCode+"</agentcode>" + "<pin>"+pin+"</pin>"
//					+ "<agentname>"+agentCode+"</agenttransname>" + "<source>"+MNumber+"</source>"
//					+ "<agenttransid>		</agenttransid>"
//					+ "<opragentcode>"+prdt+"<opragentcode>"
//					+ "<vendorcode>OLR</vendorcode>"
//					+ "<productcode>"+prdt+"</productcode>" + "<amount>"+DenoID+"</amount>"
//					+ "<requestcts>"+dateFrmt+"</requestcts>" + "<clienttype>TCPIP</clienttype>"
//					+ "<comments>TOPUP</comments>" + "</udv1>" + "</udv2>"
//					+ "</udv3>" + "</udv4>" + "</request>" + "</mbanking>";

			
		//	String XMLRetString = strRequestXML;
			sout.println(x1);
			//System.out.println(x1);
			String result = null;

			do {
				if ((result = in.readLine()) == null)
					break;
				ResponseStr = ResponseStr + result;
				System.out.println(result);
			} while (result.indexOf("</mbanking>") == -1);

			socket.close();

		} catch (Exception x) {
			x.printStackTrace();
		} finally {
//			if (parseXML(ResponseStr))
//				return "success";
//			else
//				return "failure";
			return ResponseStr;
		}
	//	return ResponseStr;
	}

//	public static boolean parseXML(String ResponseStr) {
//		boolean resultcode = false;
//		try {
//
//			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//			DocumentBuilder db = dbf.newDocumentBuilder();
//			InputSource is = new InputSource();
//			is.setCharacterStream(new StringReader(ResponseStr));
//
//			Document doc = db.parse(is);
//			String logstring = "";
//			NodeList result = doc.getElementsByTagName("resultcode");
//			Element line = (Element) result.item(0);
//
//			System.out.println("result code: "
//					+ getCharacterDataFromElement(line));
//
//			if (Integer.parseInt(getCharacterDataFromElement(line)) == 0)
//				resultcode = true;
//
//			result = doc.getElementsByTagName("transid");
//			line = (Element) result.item(0);
//
//			System.out.println("value: " + getCharacterDataFromElement(line));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		finally {
//			return resultcode;
//		}
//
//	}

//	public static String getCharacterDataFromElement(Element e) {
//		Node child = e.getFirstChild();
//		if (child instanceof CharacterData) {
//			CharacterData cd = (CharacterData) child;
//			return cd.getData();
//		}
//		return "?";
//	}

}
