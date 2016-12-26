package com.m3.tso.det

class TsoOtpMastr {

	String usrEmpCd;
	int otpGen;
	Date genDate;
	String fbtoken;
	String authkey;
	int updatestate;
	static constraints = {
	}

	static mapping = {
		table 'tso_otp_mastr'
		version false
		id column: 'otp_id'
		usrEmpCd column: 'usr_emp_cd'
		otpGen column: 'otp_gen'
		genDate column: 'gen_date'
		fbtoken column: 'fbtoken'
		authkey column: 'authkey'
		updatestate column: 'updatestate'
	}
}
