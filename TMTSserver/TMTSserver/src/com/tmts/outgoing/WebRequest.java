package com.tmts.outgoing;

import java.io.IOException;
import java.io.InputStreamReader; 
import java.net.URI;
import java.sql.Connection;
import java.io.BufferedReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import com.tmts.daemon.DaemonStaticResource;
import com.tmts.utils.TMTSLogger;

public class WebRequest {
	private java.util.Map<String,java.util.List<String>> responseHeader = null;
    private java.net.URL responseURL = null;
    private int responseCode = -1;
    private String MIMEtype  = null;
    private String charset   = null;
    private Object content   = null;
    OUTGOINGParams outgoingParams= new OUTGOINGParams();
    Connection m_conn  = DaemonStaticResource.getConnection();
   
    WebRequest(OUTGOINGParams op) throws Exception {
        BufferedReader in = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            String oldStr = op.getO_smsText();
            long trns_smsid = op.getO_trans_sms_id();
            String newStr = oldStr.replaceAll(" ", "+");
            request.setURI(new URI("http://push1.maccesssmspush.com/servlet/com.aclwireless.pushconnectivity.listeners.TextListener?userId="+DaemonStaticResource.getO_UserId()+"&pass="+DaemonStaticResource.getO_Password()+"&contenttype=1&to="+op.getO_sentTo()+"&from=56767&text="+newStr+"&selfid=true&alert=1&dlrreq=true"));
            HttpResponse response = client.execute(request);
            in = new BufferedReader
            (new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            String page = sb.toString();
            TMTSLogger.log("ACL RESPONSE ID--------------"+page);
            java.sql.PreparedStatement ps = null;
            ps = m_conn.prepareStatement("update transaction_sms set sms_status='N',response_id=? where  transaction_sms_id=? ");
            ps.setString(1, page);
            ps.setLong(2, trns_smsid);
            ps.executeUpdate();
            
            } finally {
            if (in != null) {
                try {
                    in.close();
                    } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
