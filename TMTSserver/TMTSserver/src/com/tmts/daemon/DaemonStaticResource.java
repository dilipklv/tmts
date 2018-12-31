/**
 * 
 */
package com.tmts.daemon;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class DaemonStaticResource {
	private static String m_userName;
	private static String m_password;
	String m_jdbcDriver;
	private static String m_sleepTime;
	private static String m_numberOfRequestToLoad;
	private static String m_jdbcURL;

	private static Connection m_conn;
	private static int m_portNo;
	private static String m_ipAddress;
	private static String m_agentCode;
	private static String m_pin;
	private static String m_server;
	
	private static String o_ipAddress;
	private static String o_UserId;
	private static String o_Password;

	public void initialize() {
		readPropertiesFile();
		loadEstelProperties();
		loadAclProperties();
	}

	public void readPropertiesFile() {
		try {
			Properties props = new Properties();
			props.load(new FileInputStream("./conf/tmtsdaemon.properties"));

			m_userName = props.getProperty("username");
			m_password = props.getProperty("password");
			m_jdbcDriver = props.getProperty("jdbcdriver");
			m_jdbcURL = props.getProperty("jdbc_url");
			
			m_sleepTime = props.getProperty("DAEMON_SLEEP_TIME");
			m_numberOfRequestToLoad = props.getProperty("NUMBER_OF_REQUEST_TO_LOAD");
			
			
			setServer(props.getProperty("serverName"));
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static Connection getConnection() {

		if (m_conn != null) {
			return m_conn;
		}

		try {
			m_conn = DriverManager.getConnection(m_jdbcURL, m_userName,	m_password);
			System.out.println(" Database connection established");

		} catch (Exception e) {
			System.out.println("Error--" + e);
		}
		return m_conn;
	}

	public void loadEstelProperties() {
		try {
			Statement stm = getConnection().createStatement();
			ResultSet rs = stm
					.executeQuery("SELECT PROPERTY_NAME,PROPERTY_DESCR FROM PROPERTIES	WHERE SERVICE_PROVIDER='ESTEL' AND   SERVICE_TYPE = 'MOBILE'");
			String property;
			while (rs.next()) {
				property = rs.getString("PROPERTY_NAME");
				if (property.equals("PORTNO")) {
					setPortNo(rs.getString("PROPERTY_DESCR"));
				}
				if (property.equals("IPADDRESS")) {
					setIpAddress(rs.getString("PROPERTY_DESCR"));
				}
				if (property.equals("AGENTCODE")) {
					setAgentCode(rs.getString("PROPERTY_DESCR"));
				}
				if (property.equals("PIN")) {
					setPin(rs.getString("PROPERTY_DESCR"));
				}
			}

		} catch (Exception e) {
			System.out.println("Error--" + e);
		}
	}

	public void setPortNo(String portNo) {
		m_portNo = Integer.parseInt(portNo);
	}

	public static int getPortNo() {
		return m_portNo;
	}

	public void setIpAddress(String ipAddress) {
		m_ipAddress = ipAddress;
	}

	public  static String getIpAddress() {
		return m_ipAddress;
	}

	public void setAgentCode(String agentCode) {
		m_agentCode = agentCode;
	}

	public static  String getAgentCode() {
		return m_agentCode;
	}

	public void setPin(String pin) {
		m_pin = pin;
	}

	public  static String getPin() {
		return m_pin;
	}

	public void setServer(String server) {
		m_server = server;
	}

	public  static String getServer() {
		return m_server;
	}

	/**
	 * @return the m_sleepTime
	 */
	public static long getDaemonSleepTime() {
		return Long.parseLong( m_sleepTime );
	}

	/**
	 * @param sleepTime the m_sleepTime to set
	 */
	public static void setDaemonSleepTime(String sleepTime) {
		m_sleepTime = sleepTime;
	}

	/**
	 * @return the m_numberOfRequestToLoad
	 */
	public static long getNumberOfRequestToLoad() {
		return Long.parseLong( m_numberOfRequestToLoad );
	}

	/**
	 * @param mNumberOfRequestToLoad the m_numberOfRequestToLoad to set
	 */
	public static void setNumberOfRequestToLoad(String numberOfRequestToLoad) {
		m_numberOfRequestToLoad = numberOfRequestToLoad;
	}
	
	public void loadAclProperties() {
		try{
			Statement stm = getConnection().createStatement();
			ResultSet rs = stm
					.executeQuery("SELECT PROPERTY_NAME,PROPERTY_DESCR FROM PROPERTIES	WHERE SERVICE_PROVIDER='ACL' AND   SERVICE_TYPE = 'MOBILE'");
			String property_og;
			while (rs.next()) {
				property_og = rs.getString("PROPERTY_NAME");
				
				if (property_og.equals("IPADDRESS")) {
					setO_ipAddress(rs.getString("PROPERTY_DESCR"));
				}
				if (property_og.equals("USERID")) {
					setO_UserId(rs.getString("PROPERTY_DESCR"));
				}
				if (property_og.equals("PASSWORD")) {
					setO_Password(rs.getString("PROPERTY_DESCR"));
				}
			}
			
		}catch(Exception e){System.out.println("Error--" + e);}
	}

	public static void setO_ipAddress(String o_ipAddress) {
		DaemonStaticResource.o_ipAddress = o_ipAddress;
	}

	public static String getO_ipAddress() {
		return o_ipAddress;
	}

	public static void setO_UserId(String o_UserId) {
		DaemonStaticResource.o_UserId = o_UserId;
	}

	public static String getO_UserId() {
		return o_UserId;
	}

	public static void setO_Password(String o_Password) {
		DaemonStaticResource.o_Password = o_Password;
	}

	public static String getO_Password() {
		return o_Password;
	}

	
}
