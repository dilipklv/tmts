package com.tmts.outgoing;

import java.sql.Connection;

import com.tmts.daemon.DaemonStaticResource;

import com.tmts.utils.TMTSLogger;

public class TMTSOUTGOINGDaemon implements Runnable {
	Thread m_daemonOutThread;
	Connection m_conn;
	TMTSOUTGOINGService tmtsOutgoingService = new TMTSOUTGOINGService();
	
	public TMTSOUTGOINGDaemon(){
		m_daemonOutThread = new Thread(this);
		m_conn = DaemonStaticResource.getConnection();
		m_daemonOutThread.start();
	}
	public void run() {
		try {
			while (true) {		

				tmtsOutgoingService.responseProcessor();
				
				Thread.sleep( DaemonStaticResource.getDaemonSleepTime()  );

			}
		} catch (Exception e) {
			TMTSLogger.logErr(e.getMessage());
		}
	}
}


