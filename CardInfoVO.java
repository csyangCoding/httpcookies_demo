package com.csy.springboot.szfy;

import java.io.Serializable;

public class CardInfoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String url = "http://3030.ij120.zoenet.cn/api/cardManage/getDefaultCard";

	private String patientId = "268d9bf35c9021831c3c4ddcfdaa201a";

	private String cardId = "6df543f4d81443f19638105b64ac4fce";

	private String idCardNo = "430523199005291568";
	
	private String icCardNo = "1002486";

	private String patientName = "谢媚";

	private String telephone = "18318054513";
	
	private String userId = "57ffcec4e1e44581907caa79af884181";

	public String getIcCardNo() {
		return icCardNo;
	}

	public void setIcCardNo(String icCardNo) {
		this.icCardNo = icCardNo;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

}
