package com.m3.tso.det
import grails.converters.JSON

import org.apache.commons.codec.binary.Base64;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.sun.org.apache.bcel.internal.generic.RETURN;

class MobileOtpController {

	//Function to generate OTP for first Time
	def generateOTP() {
		def jsonData;
		def empcd=params.empcode
		def empmobile= params.mobile
		def empemail= params.email
		def empvalid=1

		try{
			TsoUsrMastr usrMastr = TsoUsrMastr.findByUsrEmpCd(empcd)
			empmobile="91"+(empmobile).toString()
			print "Email"+usrMastr.getUsrEmail()+" Sending OTP Message to ${empmobile}"
			int otps= generateOTPNumber()
			TsoUsrMastr.executeUpdate("update TsoUsrMastr set usrEmail = ?,usrMobleNo=? where usrEmpCd = ?",[params.email, params.mobile, params.empcode])
			//Storing Entry to Database
			if(TsoUsrMastr.findByUsrEmpCd(empcd).getStatsFlg()=='A')
			{

				//				println"Testing Saving err"+TsoOtpMastr.findByUsrEmpCd(empcd)
				if(TsoOtpMastr.findByUsrEmpCd(empcd)==null)
				{

					try {
						TsoOtpMastr oo= new TsoOtpMastr()
						oo.usrEmpCd="${empcd}";
						oo.otpGen=otps;
						oo.genDate=new java.sql.Timestamp(new Date().getTime());
						oo.fbtoken="a"
						oo.authkey=AuthenticationController.generateKey(15)
						oo.updatestate=1



						oo.save()
					} catch (Exception e) {
						println"Error Saving OTP DATA: "+e
					}



					def url = "https://www.txtguru.in"
					def path = "/imobile/api.php"
					def query = [ username: "jyothycom", password: "22298994", source: "OTP",dmobile: "${empmobile}",message: "Your OTP For Reg. ${otps}"]

					// Submit a request via GET
					def response = ApiConsumerController.getText(url, path, query)
					println "Resp:"+response;
				}
				//Case if multiple Time URL hit
				else
				{
					println "Error in saving OTP record "

					empvalid=2;
				}

				jsonData = ["validate_usr_emp_cd":empvalid,"user_status":'A']
				render jsonData as JSON;
			}
			else
			{
				jsonData = ["validate_usr_emp_cd":empvalid,"user_status":'I']
				render jsonData as JSON;
			}
		}

		catch(Exception e)
		{
			println "error in otp: ${e}"
			jsonData = ["validate_usr_emp_cd":0,  ,"user_status":'N']
			render jsonData as JSON;
		}


	}

	//Function to Generate OTP of 4Digit
	static int generateOTPNumber(){
		int length=4;
		String result = "";
		int random;
		while(true){
			random  = (int) ((Math.random() * (10 )));
			if(result.length() == 0 && random == 0){
				//when parsed this insures that the number doesn't start with 0
				random+=1;
				result+=random;
			}
			else if(!result.contains(Integer.toString(random))){
				//if my result doesn't contain the new generated digit then I add it to the result
				result+=Integer.toString(random);
			}
			if(result.length()>=length){
				//when i reach the number of digits desired i break out of the loop and return the final result
				break;
			}
		}

		return Integer.parseInt(result);
	}

	//Function to verify OTP of user

	def verifyOTP()
	{
		def jsonData
		try {

			TsoOtpMastr obj= TsoOtpMastr.findByUsrEmpCdAndOtpGen(params.empcode,params.otp)
			println "OTP DATA:"+obj.getOtpGen()+"AUTHKEY: "+obj.getAuthkey()
			//to add auth key in otp master table
			TsoOtpMastr.executeUpdate("update TsoOtpMastr set authkey = ? where usrEmpCd = ?",[obj.getAuthkey(), params.empcode])
			//jsonData=[]
			jsonData = ["verify_otp":1,"authkey":obj.getAuthkey(),"image":logoImage(),"cmpy_name":"Demo Company"]
			render jsonData as JSON;
		}
		catch (Exception e)
		{
			println "Error in verifyOTP : ${e}"
			jsonData = ["verify_otp":0,"authkey":""]
			render jsonData as JSON;
		}
	}


	//Function to resend OTP

	def resendOTP()
	{
		def jsonData
		def usrStatus;
		def empmobile= params.mobile
		TsoOtpMastr obj= TsoOtpMastr.findByUsrEmpCd(params.empcode)
		String oo=obj.getOtpGen().toString()

		empmobile="91"+(empmobile).toString()
		try{
			def url = "https://www.txtguru.in"
			def path = "/imobile/api.php"
			def query = [ username: "jyothycom", password: "22298994", source: "OTP",dmobile: "${empmobile}",message: "Your OTP For Reg. ${oo}"]

			// Submit a request via GET
			def response = ApiConsumerController.getText(url, path, query)
			println "Resp:"+response;


			jsonData = ["validate_usr_emp_cd":1,  "validate_usr_moble_no":1]
			render jsonData as JSON;
		}
		catch(Exception e)
		{
			println "Error in resendOTP() : ${e}"
			jsonData = ["validate_usr_emp_cd":0,  "validate_usr_moble_no":0]
			render jsonData as JSON;
		}
	}


	//Function to verify user exist or not
	def verifyUser()
	{
		def jsonData;
		try{
			TsoUsrMastr obj= TsoUsrMastr.findByUsrEmpCd(params.empcode)
			if(obj.getStatsFlg()=='A')
			{
				jsonData=["user_status":'A']
				render jsonData as JSON;
			}
			else if(obj.getStatsFlg()=='I')
			{
				jsonData=["user_status":'I']
				render jsonData as JSON;
			}
		}
		catch(Exception e)
		{
			println "Error in verifyUser ${e}"
			jsonData=["user_status":'N']
			render jsonData as JSON;
		}
	}

	//Function to encode image to base 64
	public static String encodeImage(byte[] imageByteArray) {
		return new BASE64Encoder().encode(imageByteArray);
		//return Base64.encodeBase64URLSafeString(imageByteArray);
	}

	//Function to decode base 64 to byte array
	public static byte[] decodeImage(String imageDataString) {
		return new BASE64Decoder().decodeBuffer(imageDataString)
		//		return Base64.decodeBase64(imageDataString);
	}

	//Function to return logo in string(base64)
	def logoImage()
	{
		String path=request.getSession().getServletContext().getRealPath("/") + "ailogo.png"
		File  file= new File(path)
		// Reading a Image file from file system
		FileInputStream imageInFile = new FileInputStream(file);
		byte[] imageData = new byte[(int)file.length()];
		imageInFile.read(imageData);


		// Converting Image byte array into Base64 String
		String imageDataString = encodeImage(imageData);
		imageInFile.close();
		return imageDataString
	}
}
