package com.m3.tso.det
import grails.converters.JSON

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat

import org.json.simple.JSONObject;

class FirebaseController {
//TODO change authCheck() 0
	def authCheck(authkey,empcd) {
		try {
			TsoOtpMastr oo= TsoOtpMastr.findByUsrEmpCd(empcd)
			if(oo.getAuthkey()==authkey) {
				return 1;
			}
			else {
				return 0;
			}
		}
		catch(Exception e) {
			println "Error in auth ${e}"
			return 0;
		}
	}

	public final static String AUTH_KEY_FCM = "AIzaSyDsM4I3uGVs_l2hluX0T2qHP7h3UWtpmhg";
	public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

	// userDeviceIdKey is the device id you will query from your database

	public static void pushFCMNotification(String from,String to,String message) throws Exception{

		String authKey = AUTH_KEY_FCM;   // You FCM AUTH key
		String FMCurl = API_URL_FCM;

		URL url = new URL(FMCurl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization","key="+authKey);
		conn.setRequestProperty("Content-Type","application/json");

		JSONObject json = new JSONObject();
		json.put("to",to.trim());
		JSONObject info = new JSONObject();
		info.put("title", from+" : Alert!");   // Notification title
		info.put("body", message); // Notification body
		json.put("notification", info);

		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(json.toString());
		wr.flush();
		conn.getInputStream();
	}


	//Function to send Firebase notification
	/*
	 def sendNotification()
	 {def jsonData=[]
	 try{
	 def fromempcode=params.fromempcode
	 def toempcode=params.toempcode
	 def message=params.message
	 TsoUsrMastr obj= TsoUsrMastr.findByUsrEmpCd(fromempcode)
	 String fromusr=obj.getUsrName()
	 TsoOtpMastr oo= TsoOtpMastr.findByUsrEmpCd(toempcode)
	 String tousr=oo.getAuthkey()
	 pushFCMNotification(fromusr,tousr,message)
	 jsonData=["notificationStatus":1]
	 }
	 catch(Exception e)
	 {jsonData=["notificationStatus":0]}
	 render jsonData as JSON
	 }
	 */

	//Function to recieve notification data

	def sendNotification()
	{  //def authkey=params.authkey
		def jsonData=["notificationStatus":0]
		
		if(authCheck(params.authkey,params.sender_emp_cd))
			try{
				TsoNotification ooo= new TsoNotification();
				ooo.usrEmpCd=params.sender_emp_cd;
				ooo.senderName=params.sender_name;
				ooo.revieveEmpCd=params.recivr_emp_cd;
				ooo.sraGrpId=params.sra_grp_id as Integer;
				ooo.sraGrpName=params.sra_grp_name;
				ooo.target=params.target as Integer;
				ooo.achievement=params.achievement as Integer;
				ooo.percentage=params.percentage as Integer;
				ooo.achvSraGrp=params.achvd_sra_grp as Integer;
				ooo.message=params.msg;

				ooo.reciveDate=new java.sql.Timestamp(new Date().getTime());
				ooo.status=1
				ooo.save();
				TsoUsrMastr obj= TsoUsrMastr.findByUsrEmpCd(params.sender_emp_cd)
				String fromusr=obj.getUsrName()
				TsoOtpMastr oo= TsoOtpMastr.findByUsrEmpCd(params.recivr_emp_cd)
				String tousr=oo.getFbtoken()
				println "Sending message from : "+fromusr+" TO : "+tousr+ " Message : "+params.msg
				pushFCMNotification(fromusr,tousr,params.msg)
				jsonData=["notificationStatus":1]


			}
			catch(Exception e)
		{
			println"Error in sendNotification method : ${e}"
			jsonData=["notificationStatus":0]
		}
		render jsonData as JSON
	}

	//Function to send notification Response
	def recieveNotification()
	{
		def sender_name=[];
		def sender_emp_cd=[]
		def achvd_sra_grp=[];
		def sra_grp_id=[];
		def sra_grp_name=[];
		def target=[];
		def achievement=[];
		def msg=[];
		def date=[];
		def time=[];
		def jsonData=[["notificationStatus":0]];
		def targetList=[]
		if(authCheck(params.authkey,params.empcode))

			try{
				String query1="""select sender_name,usr_emp_cd,achieved_sra_grp,sra_grp_id,sra_grp_name,target,achievement,message,recive_date,percentage,notif_id from tso_notification where reciever_emp_cd=:reciever_emp_cd and status=1"""
				TsoOtpMastr.withSession { session ->
					def query = session.createSQLQuery(query1)
					query.setParameter("reciever_emp_cd",params.empcode)
					targetList = query.list();}
				SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
				SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm a");
				for(int i=0;i<targetList.size();i++)
				{
date[i]=formatDate.format(targetList[i][8])
time[i]=formatTime.format(targetList[i][8])
					jsonData[i]=["sender_name":targetList[i][0],
						"sender_emp_cd":targetList[i][1],
						"achvd_sra_grp":targetList[i][2],
						"sra_grp_id":targetList[i][3],
						"sra_grp_name":targetList[i][4],
						"target":targetList[i][5],
						"achievement":targetList[i][6],
						"msg":targetList[i][7],
						"date":date[i],
						"time":time[i],
						"percentage":targetList[i][9],
						"notificationStatus":1
					]
//TODO Uncomment final
					
					TsoNotification.executeUpdate("update TsoNotification set status=? where id=?",[0, targetList[i][10] as Long])
				
			
					}
			}
			catch(Exception e){

				println "Error in recieveNotification : ${e}"
				jsonData=[["notificationStatus":0]]

			}
		render jsonData as JSON

	}

def testt()
{
//	def targetList=[]
//	String query1="""select sender_name,usr_emp_cd,achieved_sra_grp,sra_grp_id,sra_grp_name,target,achievement,message,recive_date,notif_id from tso_notification where reciever_emp_cd=:reciever_emp_cd and status=1"""
//				TsoOtpMastr.withSession { session ->
//					def query = session.createSQLQuery(query1)
//					query.setParameter("reciever_emp_cd",params.empcode)
//					targetList = query.list();}
//	TsoNotification.executeUpdate("update TsoNotification set status=? where id=?",[0, targetList[0][9] as Long])

	pushFCMNotification("Haresh","eHmXFezNgJI:APA91bEE3FSendjzDBPZYe_-PeeiULjqHju9zMECyDrF2curtaboVl7TUvMlaZQCN68cQZb1qsZBhKAKTmLHYG1quxwvSspvhxRhmugWq5hDp6hetpqJt-bWTgDk2fnngAFO5Uj4Nq7V","Test")
	}

}