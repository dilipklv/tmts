/**
 * 
 */
package com.tmts.utils;

import org.apache.log4j.Logger;

/**
 * @author supporttmts
 *
 */
public class TMTSLogger {
	
	private static Logger logger = Logger.getLogger(TMTSLogger.class);
	
	public static void log(String msg) {
		//System.out.println(msg);
		logger.info( msg );
	}

	public static void logErr(String errmsg ) {
	//	System.out.println( errmsg );	
		logger.error( errmsg );
	}	
	
	public static void warn (String warnmsg ) {
		//	System.out.println( errmsg );	
			logger.warn( warnmsg );
		}
	
	public static void debug (String dbgmsg ) {
		//	System.out.println( errmsg );	
			logger.debug( dbgmsg );
		}
	
}
