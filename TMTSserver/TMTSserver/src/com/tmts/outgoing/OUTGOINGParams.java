package com.tmts.outgoing;

public class OUTGOINGParams {
	

	private  String o_smsText; 
	private  String o_sentTo;
	private  long   o_trans_sms_id;
	
	OUTGOINGParams(){
		
	}
   OUTGOINGParams(String smsText,String sentTo,long trans_sms_id){
		setO_smsText(smsText);
		setO_sentTo(sentTo);
		setO_trans_sms_id(trans_sms_id);
	
	}
	
   public void setO_sentTo(String sentTo) {
		o_sentTo = sentTo;
	}
   public  String getO_sentTo() {
		return o_sentTo;
	}
	
   public  void setO_smsText(String smsText) {
		o_smsText = smsText;
	}
	 public  String getO_smsText() {
		return o_smsText;
	}
	 public  void setO_trans_sms_id(long trans_sms_id) {
		o_trans_sms_id = trans_sms_id;
	}
	 public  long getO_trans_sms_id() {
		return o_trans_sms_id;
	}

}
