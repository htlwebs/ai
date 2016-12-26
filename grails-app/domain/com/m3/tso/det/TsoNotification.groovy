package com.m3.tso.det

import java.util.Date;

class TsoNotification {

 String usrEmpCd;
	String senderName;
	String revieveEmpCd;
	int sraGrpId;
	String sraGrpName;
	int target;
	int achievement;
	int percentage;
	int achvSraGrp;
	String message;
	Date reciveDate;
	int status;
	
    static constraints = {
		
    }
	
	static mapping = {
	table 'tso_notification'
	version false
	id column: 'notif_id'
	usrEmpCd column: 'usr_emp_cd'
	senderName column: 'sender_name'
	revieveEmpCd column: 'reciever_emp_cd'
	sraGrpId column: 'sra_grp_id'
	sraGrpName column: 'sra_grp_name'
	target column: 'target'
	achievement column: 'achievement'
	percentage column:'percentage'
	achvSraGrp column:'achieved_sra_grp'
	message column: 'message'
	reciveDate column: 'recive_date'
	status column: 'status'
    }
}
