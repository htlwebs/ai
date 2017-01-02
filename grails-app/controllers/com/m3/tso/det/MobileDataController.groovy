package com.m3.tso.det
import grails.converters.JSON
class MobileDataController {



	//Code Test for Update in Grooy
	//	def updateCheckTest() {
	//		TsoUsrMastr.executeUpdate("update TsoUsrMastr set usrEmail = ?,usrMobleNo=? where usrEmpCd = ?",[params.email, params.mobile, params.empcode])
	//	}

	
	
	//Function for authentication

	def authCheck(authkey,empcd)
	{try
		{
			TsoOtpMastr oo= TsoOtpMastr.findByUsrEmpCd(empcd)
			if(oo.getAuthkey()==authkey)
			{
				return 1;
			}
			else
			{
				return 1;
			}
		}
		catch(Exception e)
		{
			println "Error in auth ${e}"
			return 1;
		}
	}

	//Function to fetch data for emp Data

	def fetchEmpData() {

		if(authCheck(params.authkey,params.empcode))
		{
			if(params.fbtoken!=null)
			{
				TsoOtpMastr.executeUpdate("update TsoOtpMastr set fbtoken=? where usrEmpCd=?",[params.fbtoken, params.empcode])
			}
			def jsonData=getAllAssociate(params.empcode)
			render jsonData as JSON
		}
		else
		{
			def jsonData=["error":"unauthorized"]
			render jsonData as JSON
		}
	}

	//Function to fetch associate data

	def fetchAssociateData(){
		def jsonData=[];
		def targetList;
		String quer="""SELECT tso_usr_mastr.usr_id, tso_usr_mastr.usr_emp_cd
FROM tso_usr_mastr tso_usr_mastr
WHERE tso_usr_mastr.reprtng_usr_id= (SELECT tso_usr_mastr.usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_emp_cd = :usr_emp_cd )"""

		TsoOtpMastr.withSession { session ->

			def qu4=session.createSQLQuery(quer)
			qu4.setParameter("usr_emp_cd",params.empcode)
			targetList=qu4.list();
		}
	
		for(int i=0;i<targetList.size();i++) {
			jsonData[i]=getAllAssociate(targetList[i][1])
		}
		render jsonData as JSON
		}

	//Function to getDetails

	def getAllAssociate(empcd) {
	
		def targetList,targetList2,targetList3,targetList4,targetList5;
		def jsonData
		def usr_type_name,usr_name,usr_emp_cd,usr_email,usr_moble_no,achvd_sra_grp,extra_incentive,senior;
		def sra_grp_id=[],sra_grp_name=[],sra_grp_year=[],sra_grp_qutr=[],sra_grp_target=[],sra_grp_achvment=[],sra_grp_skill_points=[],sra_grp_skill_amount=[]
		def brandList=[]
		def total_skill_points,total_skill_amount
		def reporting_person_emp_code,reporting_person_name;

		//usr_emp_cd  usr_name  usr_email  usr_moble_no
		//test for E000000021,E000007505
		String query1="""SELECT tso_usr_mastr.usr_emp_cd,
       tso_usr_mastr.usr_name,
       tso_usr_mastr.usr_email,
       tso_usr_mastr.usr_moble_no,
       tso_usr_type_mastr.usr_type_name
	   FROM tso_usr_type_mastr    tso_usr_type_mastr
       INNER JOIN tso_usr_mastr tso_usr_mastr
       ON (tso_usr_mastr.usr_type_id = tso_usr_type_mastr.usr_type_id)
       WHERE tso_usr_mastr.usr_emp_cd=:usr_emp_cd"""



		//	reporting_person_emp_code, reporting_person_name

		String query2="""SELECT tso_usr_mastr.usr_emp_cd, tso_usr_mastr.usr_name
		FROM tso_usr_mastr tso_usr_mastr
		WHERE tso_usr_mastr.usr_id= (select tso_usr_mastr.reprtng_usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_cd = :usr_emp_cd )"""


		//usr_id,targt_year,targt_qutr,targt_vol,achvmnt_vol,sra_grp_id,sra_grp_name,sra_achived,skill_amount,skill_point

		String query3="""SELECT tso_usr_mastr.usr_id,
		tso_targt_dtls.targt_year,
		tso_targt_dtls.targt_qutr,
		tso_achvmnt_dtls.targt_vol,
		tso_achvmnt_dtls.achvmnt_vol,
		tso_targt_dtls.sra_grp_id,
		tso_sragrp_mastr.sra_grp_name,
		tso_usr_mastr.usr_emp_cd,
		IF(SUM(tso_targt_dtls.achvmnt_vol)>=SUM(tso_targt_dtls.targt_vol) AND SUM(tso_targt_dtls.achvmnt_vol)!=0,1,0) AS achived_sra,
		tso_achvmnt_dtls.inctv_amt,
		tso_achvmnt_dtls.skill_point
		FROM (tso_targt_dtls    tso_targt_dtls
		INNER JOIN tso_sragrp_mastr tso_sragrp_mastr
		ON (tso_targt_dtls.sra_grp_id = tso_sragrp_mastr.sra_grp_id))
		INNER JOIN tso_usr_mastr tso_usr_mastr
		ON (tso_usr_mastr.usr_id = tso_targt_dtls.usr_id)
		INNER JOIN tso_achvmnt_dtls tso_achvmnt_dtls
		ON(tso_achvmnt_dtls.usr_id = tso_targt_dtls.usr_id AND tso_achvmnt_dtls.sra_grp_id=tso_targt_dtls.sra_grp_id)
		WHERE (tso_usr_mastr.usr_emp_cd = :usr_emp_cd )
		Group BY tso_targt_dtls.sra_grp_id""";



		// For Brand targt_year,targt_qutr,targt_vol,achvmnt_vol,brnd_name,brnd_cd,catgry_cd,catgry_name,sra_grp_id,usr_code,ratio

		String query4="""
	 SELECT tso_targt_dtls.targt_year,
       tso_targt_dtls.targt_qutr,
       tso_targt_dtls.targt_vol,
       tso_targt_dtls.achvmnt_vol,
       tso_brand_mastr.brnd_name,
       tso_brand_mastr.brnd_cd,
       tso_catgry_mastr.catgry_cd,
       tso_catgry_mastr.catgry_name,
       tso_targt_dtls.sra_grp_id,
       tso_targt_dtls.usr_code,
	   tso_sragrp_ratio_dtls.ratio
	   FROM (tso_targt_dtls    tso_targt_dtls
       INNER JOIN tso_catgry_mastr tso_catgry_mastr
       ON (tso_targt_dtls.catgry_id = tso_catgry_mastr.catgry_id))
       INNER JOIN tso_brand_mastr tso_brand_mastr
       ON (tso_targt_dtls.brnd_id = tso_brand_mastr.brnd_id)
	   INNER JOIN tso_sragrp_ratio_dtls tso_sragrp_ratio_dtls
       ON (tso_targt_dtls.brnd_id=tso_sragrp_ratio_dtls.brnd_id  AND tso_targt_dtls.catgry_id=tso_sragrp_ratio_dtls.catgry_id   AND  tso_targt_dtls.sra_grp_id=tso_sragrp_ratio_dtls.sra_grp_id)
	   WHERE (tso_targt_dtls.sra_grp_id = :sra_grp_id)
       AND (tso_targt_dtls.usr_code = :usr_emp_cd )""";




		//Query for finding is senior or not
		String query5="""SELECT tso_usr_mastr.usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_emp_cd = :usr_emp_cd AND tso_usr_mastr.usr_type_id in (1,2,3)"""
	
		//Query for Skill Amount and Skill Points
		String query6="""SELECT tot_sra_no,tot_incntv_amt,adtntl_achmnt_amt,skill_point,skill_amt FROM tso_incntv_dtls where usr_id= (select usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_emp_cd = :usr_emp_cd )"""




		TsoOtpMastr.withSession { session ->
			def query = session.createSQLQuery(query1)
			query.setParameter("usr_emp_cd",empcd)
			targetList = query.list();
			def qu1 = session.createSQLQuery(query2)
			qu1.setParameter("usr_emp_cd",empcd)
			targetList2=qu1.list();
			def qu2 = session.createSQLQuery(query3)
			qu2.setParameter("usr_emp_cd",empcd)
			targetList3=qu2.list();
			def qu4=session.createSQLQuery(query5)
			qu4.setParameter("usr_emp_cd",empcd)
			targetList4=qu4.list();
			def qu5=session.createSQLQuery(query6)
			qu5.setParameter("usr_emp_cd",empcd)
			targetList5=qu5.list();
		}

		if(targetList4[0]==null)
		{
			senior=0;
		}
		else
		{
			senior=1;
		}
		usr_emp_cd=targetList[0][0]
		usr_name=targetList[0][1]
		usr_email=targetList[0][2]
		usr_moble_no=targetList[0][3]
		usr_type_name=targetList[0][4]


		if(targetList5[0]!=null)
		{
			achvd_sra_grp=targetList5[0][0]
			extra_incentive=targetList5[0][2]
			total_skill_points=targetList5[0][3]
			total_skill_amount=targetList5[0][4]
		}
		else
		{
			achvd_sra_grp=0
			extra_incentive=0
			total_skill_points=0
			total_skill_amount=0
		}


		if(targetList2[0]!=null)
		{
			reporting_person_emp_code=targetList2[0][0]
			reporting_person_name=targetList2[0][1]
		}
		else
		{
			reporting_person_emp_code=""
			reporting_person_name=""
		}

		if(targetList3.size()==0)
		{
			sra_grp_id=[]
			sra_grp_name=[]
			sra_grp_year=[]
			sra_grp_qutr=[]
			sra_grp_target=[]
			sra_grp_achvment=[]
			sra_grp_skill_points=[]
			sra_grp_skill_amount=[]
		}
		else
			for(int i=0;i<targetList3.size;i++)
		{
			//println"TargetList  "+	targetList3[i]
			sra_grp_id[i]=targetList3[i][5]
			sra_grp_name[i]=targetList3[i][6]
			sra_grp_year[i]=targetList3[i][1]
			sra_grp_qutr[i]=targetList3[i][2]
			sra_grp_target[i]=targetList3[i][3]
			sra_grp_achvment[i]=targetList3[i][4]
			sra_grp_skill_points[i]=targetList3[i][10]
			sra_grp_skill_amount[i]=targetList3[i][9]
			TsoOtpMastr.withSession { session ->
				def query = session.createSQLQuery(query4)
				query.setParameter("usr_emp_cd",empcd)
				query.setParameter("sra_grp_id",sra_grp_id[i])
				brandList[i] = query.list();
			}
		}
		
		def aaa=[
			"usr_type_name": usr_type_name,
			"usr_name": usr_name,
			"usr_emp_cd": usr_emp_cd,
			"usr_email": usr_email,
			"usr_moble_no": usr_moble_no,
			"reporting_person_emp_code": reporting_person_emp_code,
			"reporting_person_name": reporting_person_name,
			"achvd_sra_grp":achvd_sra_grp ,
			"extra_incentive": extra_incentive,
			"senior": senior,
			"total_skill_points":total_skill_points,
			"total_skill_amount":total_skill_amount,
			"sra_group": []]

		for(int i=0;i<sra_grp_id.size();i++)
		{
			aaa.sra_group[i]=["sra_grp_id": sra_grp_id[i],
				"sra_grp_name": "${sra_grp_name[i]}",
				"data":[],
				"sra_grp_year": sra_grp_year[i],
				"sra_grp_qutr": sra_grp_qutr[i],
				"sra_grp_target": sra_grp_target[i],
				"sra_grp_achvment": sra_grp_achvment[i],
				"sra_grp_skill_points": sra_grp_skill_points[i],
				"sra_grp_skill_amount": sra_grp_skill_amount[i]]
		}

		for(int i=0;i<sra_grp_id.size();i++)
		{
			for(int j=0;j<brandList[i].size();j++)

			{
				aaa.sra_group[i].data[j]=[
					"brnd_cd": brandList[i][j][5],
					"brnd_name": brandList[i][j][4],
					"catgry_cd": brandList[i][j][6],
					"catgry_name": brandList[i][j][7],
					"year": brandList[i][j][0],
					"qutr": brandList[i][j][1],
					"target": brandList[i][j][2],
					"achievement": brandList[i][j][3],
					"ratio":brandList[i][j][10]
				]
			}
		}

		return aaa
	}



	//Function to get update associate user data

	def updateAssociateUsrData()
	{def jsonData=[];
	def targetList;
		String query5="""SELECT tso_usr_mastr.usr_id, tso_usr_mastr.usr_cd
FROM tso_usr_mastr tso_usr_mastr
WHERE tso_usr_mastr.reprtng_usr_id= (SELECT tso_usr_mastr.usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_emp_cd = :usr_emp_cd )"""

		TsoOtpMastr.withSession { session ->

			def qu4=session.createSQLQuery(query5)
			qu4.setParameter("usr_emp_cd",params.empcode)
			targetList=qu4.list();
		}
	
		for(int i=0;i<targetList.size();i++) {
			jsonData[i]=getUsrData(targetList[i][1])
		}
		render jsonData as JSON

	}


	//Function for getting update user data

	def updateUsrData()
	{def jsonData
		if(authCheck(params.authkey,params.empcode))
		{jsonData=getUsrData(params.empcode)
			render jsonData as JSON
		}
		else
		{jsonData=["error":"unauthorized"]
			render jsonData as JSON}
	}

	def getUsrData(empcd)
	{
		def targetList,targetList2,targetList3,targetList4,targetList5;
		def jsonData
		def usr_emp_cd,achvd_sra_grp,extra_incentive,senior;
		def sra_grp_id=[],sra_grp_name=[],sra_grp_year=[],sra_grp_qutr=[],sra_grp_target=[],sra_grp_achvment=[],sra_grp_skill_points=[],sra_grp_skill_amount=[]
		def brandList=[]
		def reporting_person_emp_code,reporting_person_name


		//usr_emp_cd  usr_name  usr_email  usr_moble_no
		//test for E000000021,E000007505




		//usr_id,targt_year,targt_qutr,targt_vol,achvmnt_vol,sra_grp_id,sra_grp_name,sra_achived,skill_amount,skill_point
		//Changes done tso_achvmnt_dtls.targt_vol,		tso_achvmnt_dtls.achvmnt_vol from sum(tso_targt_dtls.targt_vol)
		String query3="""SELECT tso_usr_mastr.usr_id,
		tso_targt_dtls.targt_year,
		tso_targt_dtls.targt_qutr,
		tso_achvmnt_dtls.targt_vol,
		tso_achvmnt_dtls.achvmnt_vol,
		tso_targt_dtls.sra_grp_id,
		tso_sragrp_mastr.sra_grp_name,
		tso_usr_mastr.usr_emp_cd,
		IF(SUM(tso_targt_dtls.achvmnt_vol)>=SUM(tso_targt_dtls.targt_vol) AND SUM(tso_targt_dtls.achvmnt_vol)!=0,1,0) AS achived_sra,
		tso_achvmnt_dtls.inctv_amt,
		tso_achvmnt_dtls.skill_point
		FROM (tso_targt_dtls    tso_targt_dtls
		INNER JOIN tso_sragrp_mastr tso_sragrp_mastr
		ON (tso_targt_dtls.sra_grp_id = tso_sragrp_mastr.sra_grp_id))
		INNER JOIN tso_usr_mastr tso_usr_mastr
		ON (tso_usr_mastr.usr_id = tso_targt_dtls.usr_id)
		INNER JOIN tso_achvmnt_dtls tso_achvmnt_dtls
		ON(tso_achvmnt_dtls.usr_id = tso_targt_dtls.usr_id AND tso_achvmnt_dtls.sra_grp_id=tso_targt_dtls.sra_grp_id)
		WHERE (tso_usr_mastr.usr_emp_cd = :usr_emp_cd )
		Group BY tso_targt_dtls.sra_grp_id""";

		// For Brand targt_year,targt_qutr,targt_vol,achvmnt_vol,brnd_name,brnd_cd,catgry_cd,catgry_name,sra_grp_id,usr_code

		String query4="""
SELECT tso_targt_dtls.targt_year,
       tso_targt_dtls.targt_qutr,
       tso_targt_dtls.targt_vol,
       tso_targt_dtls.achvmnt_vol,
       tso_brand_mastr.brnd_name,
       tso_brand_mastr.brnd_cd,
       tso_catgry_mastr.catgry_cd,
       tso_catgry_mastr.catgry_name,
       tso_targt_dtls.sra_grp_id,
       tso_targt_dtls.usr_code,
	   tso_sragrp_ratio_dtls.ratio
	   FROM (tso_targt_dtls    tso_targt_dtls
       INNER JOIN tso_catgry_mastr tso_catgry_mastr
       ON (tso_targt_dtls.catgry_id = tso_catgry_mastr.catgry_id))
       INNER JOIN tso_brand_mastr tso_brand_mastr
       ON (tso_targt_dtls.brnd_id = tso_brand_mastr.brnd_id)
	   INNER JOIN tso_sragrp_ratio_dtls tso_sragrp_ratio_dtls
       ON (tso_sragrp_ratio_dtls.brnd_id = tso_targt_dtls.brnd_id AND tso_sragrp_ratio_dtls.catgry_id = tso_targt_dtls.catgry_id AND tso_sragrp_ratio_dtls.sra_grp_id = tso_targt_dtls.sra_grp_id)
	   WHERE (tso_targt_dtls.sra_grp_id = :sra_grp_id)
       AND (tso_targt_dtls.usr_code = :usr_emp_cd )""";




		//Query for finding is senior or not
		String query5="""SELECT tso_usr_mastr.usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_emp_cd = :usr_emp_cd AND tso_usr_mastr.usr_type_id in (1,2,3)"""



		//Query for finding total sra achived, total incntv , addtional incntv, total skill point and total skill amount
		String query6="""SELECT tot_sra_no,tot_incntv_amt,adtntl_achmnt_amt,skill_point,skill_amt FROM tso_incntv_dtls where usr_id= (select usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_emp_cd = :usr_emp_cd )"""
		TsoOtpMastr.withSession { session ->

			def qu3 = session.createSQLQuery(query3)
			qu3.setParameter("usr_emp_cd",empcd)
			targetList3=qu3.list();
			def qu4=session.createSQLQuery(query5)
			qu4.setParameter("usr_emp_cd",empcd)
			targetList4=qu4.list();
			def qu5=session.createSQLQuery(query6)
			qu5.setParameter("usr_emp_cd",empcd)
			targetList5=qu5.list();
		}

		if(targetList5[0]!=null)
		{achvd_sra_grp=targetList5[0][0]
			extra_incentive=targetList5[0][2]
		}
		else
		{extra_incentive=0
		}
		if(targetList4[0]==null)
		{
			senior=0;
		}
		else
		{
			senior=1;
		}
		usr_emp_cd=params.empcode



		if(targetList3.size()==0)
		{
			sra_grp_id=[]
			sra_grp_name=[]
			sra_grp_year=[]
			sra_grp_qutr=[]
			sra_grp_target=[]
			sra_grp_achvment=[]
			sra_grp_skill_points=[]
			sra_grp_skill_amount=[]
		}
		else
			for(int i=0;i<targetList3.size;i++)
		{
			sra_grp_id[i]=targetList3[i][5]
			sra_grp_name[i]=targetList3[i][6]
			sra_grp_year[i]=targetList3[i][1]
			sra_grp_qutr[i]=targetList3[i][2]
			sra_grp_target[i]=targetList3[i][3]
			sra_grp_achvment[i]=targetList3[i][4]
			sra_grp_skill_points[i]=targetList3[i][10]
			sra_grp_skill_amount[i]=targetList3[i][9]
			TsoOtpMastr.withSession { session ->
				def query = session.createSQLQuery(query4)
				query.setParameter("usr_emp_cd",params.empcode)
				query.setParameter("sra_grp_id",sra_grp_id[i])
				brandList[i] = query.list();
			}
		}



		def aaa=[
			"usr_emp_cd": usr_emp_cd,
			"achvd_sra_grp": achvd_sra_grp,
			"extra_incentive": extra_incentive,
			"senior": senior,
			"sra_group": []]

		for(int i=0;i<sra_grp_id.size();i++)
		{
			aaa.sra_group[i]=["sra_grp_id": sra_grp_id[i],
				"sra_grp_name": "${sra_grp_name[i]}",
				"data":[],
				"sra_grp_year": sra_grp_year[i],
				"sra_grp_qutr": sra_grp_qutr[i],
				"sra_grp_target": sra_grp_target[i],
				"sra_grp_achvment": sra_grp_achvment[i],
				"sra_grp_skill_points": sra_grp_skill_points[i],
				"sra_grp_skill_amount": sra_grp_skill_amount[i]]
		}

		for(int i=0;i<sra_grp_id.size();i++)
		{
			for(int j=0;j<brandList[i].size();j++)

			{
				aaa.sra_group[i].data[j]=[
					"brnd_cd": brandList[i][j][5],
					"brnd_name": brandList[i][j][4],
					"catgry_cd": brandList[i][j][6],
					"catgry_name": brandList[i][j][7],
					"year": brandList[i][j][0],
					"qutr": brandList[i][j][1],
					"target": brandList[i][j][2],
					"achievement": brandList[i][j][3],
					"ratio":brandList[i][j][10]
				]
			}
		}




		return aaa
	}

	//Function to update firebase token in OTP master table

	def updateFbToken()
	{
		def jsonData
		if(authCheck(params.authkey,params.empcode))
		{
			if(params.fbtoken!=null && params.empcode!=null)
			{TsoOtpMastr.executeUpdate("update TsoOtpMastr set fbtoken=? where usrEmpCd=?",[params.fbtoken, params.empcode])

				jsonData=[updateFbToken: 'Y']
				render jsonData as JSON
			}

			else
			{
				jsonData=[updateFbToken: 'N']
				render jsonData as JSON
			}
		}
		else
		{
			jsonData=["error":"unauthorized"]
			render jsonData as JSON
		}

	}


	//Function for daily sync of user update
	def dailyUserUpdate()
	{
		def jsonData
		if(authCheck(params.authkey,params.empcode))
		{if(TsoOtpMastr.findByUsrEmpCd(params.empcode).getUpdatestate()==1)
			{jsonData=dailyUpdate(params.empcode)
				TsoOtpMastr.executeUpdate("update TsoOtpMastr set updatestate=? where usrEmpCd=?",[0, params.empcode])
			}
			else
			{
				jsonData=["updateStatus":0,
					"usr_emp_cd": 0,
					"achvd_sra_grp":0 ,
					"extra_incentive": 0,
					"senior": 0,
					"sra_group": []]
			}
		}
		else
		{
			jsonData=["error":"unauthorized"]

		}
		render jsonData as JSON
	}


	//Function for daily sync associate update
	def dailyAssociateUpdate()
	{
		
		def jsonData=[];
		def targetList;
		String quer="""SELECT tso_usr_mastr.usr_id, tso_usr_mastr.usr_emp_cd
FROM tso_usr_mastr tso_usr_mastr
WHERE tso_usr_mastr.reprtng_usr_id= (SELECT tso_usr_mastr.usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_emp_cd = :usr_emp_cd )"""
	
		TsoOtpMastr.withSession { session ->

			def qu4=session.createSQLQuery(quer)
			qu4.setParameter("usr_emp_cd",params.empcode)
			targetList=qu4.list();
		}
		//println "Testing fetchAssocite:"+targetList
		for(int i=0;i<targetList.size();i++) {
			jsonData[i]=dailyUpdate(targetList[i][1])
		}
		
		
		render jsonData as JSON
		
	}


	//Daily Update recursive Call
	def dailyUpdate(empcd)
	{
		def jsonData=[];

		def targetList3=[],targetList4=[],targetList5=[],brandList=[]
		TsoOtpMastr obj= TsoOtpMastr.findByUsrEmpCd(empcd)

		def achvd_sra_grp,extra_incentive,senior
		def sra_grp_id=[],sra_grp_year=[],sra_grp_qutr=[],sra_grp_achvment=[],sra_grp_skill_points=[],sra_grp_skill_amount=[]
		def brnd_cd=[],catgry_cd=[],year=[],qutr=[],achievement=[]

		//Query to find sra_grp_id,target_year,targt_qutr,achvmnt_vol,skill_point,inctv_amt

		String query3="""SELECT
tso_targt_dtls.sra_grp_id,
tso_targt_dtls.targt_year,
tso_targt_dtls.targt_qutr,
tso_achvmnt_dtls.achvmnt_vol,
tso_achvmnt_dtls.skill_point,
tso_achvmnt_dtls.inctv_amt
FROM tso_targt_dtls    tso_targt_dtls
INNER JOIN tso_usr_mastr tso_usr_mastr
ON (tso_usr_mastr.usr_id = tso_targt_dtls.usr_id)
INNER JOIN tso_achvmnt_dtls tso_achvmnt_dtls
ON(tso_achvmnt_dtls.usr_id = tso_targt_dtls.usr_id AND tso_achvmnt_dtls.sra_grp_id=tso_targt_dtls.sra_grp_id)
WHERE (tso_usr_mastr.usr_emp_cd = :usr_emp_cd )
Group BY tso_targt_dtls.sra_grp_id""";

		//For Brand targt_year,targt_qutr,targt_vol,achvmnt_vol,brnd_name,brnd_cd,catgry_cd,catgry_name,sra_grp_id,usr_code

		String query4="""SELECT
tso_brand_mastr.brnd_cd,
tso_catgry_mastr.catgry_cd,
tso_targt_dtls.targt_year,
tso_targt_dtls.targt_qutr,
tso_targt_dtls.achvmnt_vol
FROM (tso_targt_dtls    tso_targt_dtls
INNER JOIN tso_catgry_mastr tso_catgry_mastr
ON (tso_targt_dtls.catgry_id = tso_catgry_mastr.catgry_id))
INNER JOIN tso_brand_mastr tso_brand_mastr
ON (tso_targt_dtls.brnd_id = tso_brand_mastr.brnd_id)
WHERE (tso_targt_dtls.sra_grp_id = :sra_grp_id)
AND (tso_targt_dtls.usr_code = :usr_emp_cd)""";

		//Query for finding is senior or not
		String query5="""SELECT tso_usr_mastr.usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_emp_cd = :usr_emp_cd AND tso_usr_mastr.usr_type_id in (1,2,3)"""

		//Query to get achvd sra group, extra incentive
		String query6="""SELECT tot_sra_no,adtntl_achmnt_amt FROM tso_incntv_dtls where usr_id= (select usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_emp_cd = :usr_emp_cd )"""

		TsoOtpMastr.withSession { session ->
			def qu2 = session.createSQLQuery(query3)
			qu2.setParameter("usr_emp_cd",empcd)
			targetList3=qu2.list();
			def qu4=session.createSQLQuery(query5)
			qu4.setParameter("usr_emp_cd",empcd)
			targetList4=qu4.list();
			def qu5=session.createSQLQuery(query6)
			qu5.setParameter("usr_emp_cd",empcd)
			targetList5=qu5.list();
		}
		if(targetList3.size()==0)
		{
			sra_grp_id=[]
			sra_grp_year=[]
			sra_grp_qutr=[]
			sra_grp_achvment=[]
			sra_grp_skill_points=[]
			sra_grp_skill_amount=[]
		}
		else
			for(int i=0;i<targetList3.size;i++)
		{
			sra_grp_id[i]=targetList3[i][0]
			sra_grp_year[i]=targetList3[i][1]
			sra_grp_qutr[i]=targetList3[i][2]
			sra_grp_achvment[i]=targetList3[i][3]
			sra_grp_skill_points[i]=targetList3[i][4]
			sra_grp_skill_amount[i]=targetList3[i][5]
			TsoOtpMastr.withSession { session ->
				def query = session.createSQLQuery(query4)
				query.setParameter("usr_emp_cd",empcd)
				query.setParameter("sra_grp_id",sra_grp_id[i])
				brandList[i] = query.list();
			}
		}
		if(targetList4[0]==null)
		{
			senior=0;
		}
		else
		{
			senior=1;
		}
		if(targetList5[0]!=null)
		{
			achvd_sra_grp=targetList5[0][0]
			extra_incentive=targetList5[0][1]
		}
		else
		{
			achvd_sra_grp=0
			extra_incentive=0
		}
		jsonData=[
			"updateStatus":1,
			"usr_emp_cd": empcd,
			"achvd_sra_grp":targetList5[0][0] ,
			"extra_incentive": targetList5[0][1],
			"senior": 1,
			"sra_group": []]
		for(int i=0;i<sra_grp_id.size();i++)
		{ jsonData.sra_group[i]=[
				"sra_grp_id": sra_grp_id[i],

				"data":[],
				"sra_grp_year": sra_grp_year[i],
				"sra_grp_qutr": sra_grp_qutr[i],
				"sra_grp_achvment": sra_grp_achvment[i],
				]
		}

		for(int i=0;i<sra_grp_id.size();i++)
		{
			for(int j=0;j<brandList[i].size();j++)

			{
				jsonData.sra_group[i].data[j]=[
					"brnd_cd": brandList[i][j][0],
					"catgry_cd": brandList[i][j][1],
					"year": brandList[i][j][2],
					"qutr": brandList[i][j][3],
					"achievement": brandList[i][j][4]
				]
			}
		}
		return jsonData
	}

	//Function to get Hierarchy
	def getJuniors()
	{

		def jsonData=[]
		def listEmp=gethierarchyEmpCode(params.empcode)
		for(int i=0;i<listEmp.size();i++)
		{
			jsonData[i]=getAllAssociate(listEmp[i])
		}
		render jsonData as JSON

	}
	//Function to get list of empcode under associate
	def gethierarchyEmpCode(empcode)
	{
		def emplist=[]
		if(testSenior(empcode))
		{def list1=getAssist(empcode)
			emplist.addAll(list1)
			for(int i=0;i<list1.size();i++)
			{
				if(testSenior(list1[i])){
					emplist.addAll(getAssist(list1[i]))
				}
			}
		}
		return emplist
	}

	//Function to get all assistance
	def getAssist(empcd)
	{
		def targetList=[];
		String quer="""SELECT tso_usr_mastr.usr_emp_cd
FROM tso_usr_mastr tso_usr_mastr
WHERE tso_usr_mastr.reprtng_usr_id= (SELECT tso_usr_mastr.usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_emp_cd = :usr_emp_cd )"""

		TsoOtpMastr.withSession { session ->

			def qu4=session.createSQLQuery(quer)
			qu4.setParameter("usr_emp_cd",empcd)
			targetList=qu4.list();
		}
		return targetList
	}

//Function to check senior or not
	def testSenior(empcd)
	{
		def targetList
		String query5="""SELECT tso_usr_mastr.usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_emp_cd = :usr_emp_cd AND tso_usr_mastr.usr_type_id in (1,2,3)"""
		TsoOtpMastr.withSession { session ->
			def query = session.createSQLQuery(query5)
			query.setParameter("usr_emp_cd",empcd)
			targetList = query.list();}
		if(targetList[0]==null)
		{
			return 0
		}
		else
		{
			return 1
		}

	}


}

