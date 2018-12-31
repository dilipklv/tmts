package com.tmts.daemon;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.tmts.outgoing.TMTSOUTGOINGDaemon;
import com.tmts.utils.TMTSLogger;

class TMTSDaemonProcessor implements Runnable {
	
	Thread m_daemonThread;
	Connection m_conn;
	TMTSService tmtsService = new TMTSService();
	

	TMTSDaemonProcessor() {
		m_daemonThread = new Thread(this);
		m_conn = DaemonStaticResource.getConnection();
		m_daemonThread.start();
	}

	public void run() {
		try {
			while (true) {		

				tmtsService.requestProcessor();
				
				updateServerStatus();		

				Thread.sleep( DaemonStaticResource.getDaemonSleepTime()  );

			}
		} catch (Exception e) {
			TMTSLogger.logErr(e.getMessage());
		}
	}


	/**
	 * @throws SQLException
	 */
	private void updateServerStatus() throws SQLException {
		Calendar cal = Calendar.getInstance();
		final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";		

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		CallableStatement cstm = null;
		Connection conn = DaemonStaticResource.getConnection();
		try {
			cstm = conn.prepareCall("{? =  call Up_Server_status(?,?,?)  }");
			cstm.registerOutParameter(1, java.sql.Types.INTEGER);
			cstm.setString(2, DaemonStaticResource.getServer());
			cstm.setString(3, sdf.format(cal.getTime()).toString());
			cstm.setString(4, "LIVE");
			cstm.execute();
			int stat = cstm.getInt(1);
		} catch (Exception e) {
			TMTSLogger.logErr(e.getMessage());
		} finally {
			cstm.close();
		}
	}
}

public class TMTSDaemon {
	public static void main(String args[]) throws Exception {
		new DaemonStaticResource().initialize();
		new TMTSDaemonProcessor();	
	    new TMTSOUTGOINGDaemon(); 
	}
}
