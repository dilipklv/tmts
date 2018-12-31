package com.tmts.daemon;

public class ServiceParms {
	
	
	private String m_frmNo; 
	private String m_toNo;
	private String m_amt;
	private String m_service;
	private String m_status;
	private long m_sms_sid;
	private String m_oprCode;
	private String m_oprCirCode;
	ServiceParms(){
		
	}
	ServiceParms( String frmNo, String toNo,String amt,String service,String status,long sms_sid,String oprCode,String oprCirCode){
		
		this.setFrmNo(frmNo); 
		this.setToNo(toNo);
		this.setAmt(amt);
		this.setService(service);
		this.setStatus(status);
		this.setSms_sid(sms_sid);
		this.setOprCode(oprCode);
		this.setOprCirCode(oprCirCode);
	}
	void setService(String service) {
		m_service = service;
	}
	String getService() {
		return m_service;
	}
	void setAmt(String amt) {
		m_amt = amt;
	}
	String getAmt() {
		return m_amt;
	}
	void setToNo(String toNo) {
		m_toNo = toNo;
	}
	String getToNo() {
		return m_toNo;
	}
	void setFrmNo(String frmNo) {
		m_frmNo = frmNo;
	}
	String getFrmNo() {
		return m_frmNo;
	}
	void setStatus(String status) {
		m_status = status;
	}
	String getStatus() {
		return m_status;
	}
	void setSms_sid(long sms_sid) {
		m_sms_sid = sms_sid;
	}
	long getSms_sid() {
		return m_sms_sid;
	}
	void setOprCode(String oprCode) {
		m_oprCode = oprCode;
	}
	String getOprCode() {
		return m_oprCode;
	}
	void setOprCirCode(String oprCirCode) {
		m_oprCirCode = oprCirCode;
	}
	String getOprCirCode() {
		return m_oprCirCode;
	}
}
