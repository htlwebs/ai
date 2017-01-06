package com.m3.tso.det

import java.util.regex.Pattern;

import com.sun.xml.internal.rngom.parse.xml.SchemaParser.ParamState;

import grails.converters.JSON
import ai.api.model.AIResponse


class ApiaiController {

	def index() { }
	def getAidata() {

		String username;
		def jsonData=["type":0,
			"repeat":0,
			"text":"I'm not totally sure about that",
			"response":["No data"]]
		AIResponse response=TextClientApplication.getData(params.inputData)
		if(response!=null) {
			if(response.getStatus().getCode()==200) {
				println "Speech:"+response.getResult().getFulfillment().getSpeech()
				println "Action:"+ response.getResult().getAction()
				switch(response.getResult().getAction()) {


					//TODO CASE FOR USER ASSOCIATES
					case "user.associates":
						username=response.getResult().getParameters().get("username").toString()
						username=parsedString(username)
						println	"Showing associates for "+username
						if(username=="me"||username=="my"||username=="mine"||username=="I") {
							listAllAssociatebyCode(params.empcode)
						}
						else {
							if(checkAuthority(params.empcode, username)) {
								listAllAssociatebyName(username)
							}
							else {
								jsonData=["type":0,
									"repeat":0,
									"text":"Hey! You are not authorized to access!",
									"response":["No data"]]
								render jsonData as JSON
							}
						}
						break;


					//TODO CASE FOR USER PERFOMANCE
					case "user.performance":
						username=response.getResult().getParameters().get("username").toString()
						username=parsedString(username)
						println	"Showing perfomance for "+username
						if(username=="me"||username=="my"||username=="mine"||username=="me my"||username=="I")
						 {
							TsoUsrMastr usr=TsoUsrMastr.findByUsrCd(params.empcode)
							listPerformance(usr.getUsrName());
						}
						else {
							if(checkAuthority(params.empcode, username))
								//if(true)
							{
								listPerformance(username)
							}
							else {
								jsonData=["type":0,
									"repeat":0,
									"text":"Hey! You are not authorized to access!",
									"response":["No data"]]
								render jsonData as JSON
							}
						}
						break;

					//TODO CASE FOR USER TARGET/Achievement
					case "user.target":
						username=response.getResult().getParameters().get("username").toString()
						username=parsedString(username)
						println	"Showing perfomance for "+username
						if(username=="me"||username=="my"||username=="mine"||username=="I") {
							TsoUsrMastr usr=TsoUsrMastr.findByUsrCd(params.empcode)
							listTarget(usr.getUsrName());
						}
						else {
							if(checkAuthority(params.empcode, username))
								//if(true)
							{
								listTarget(username)
							}
							else {
								jsonData=["type":0,
									"repeat":0,
									"text":"Hey! You are not authorized to access!",
									"response":["No data"]]
								render jsonData as JSON
							}
						}
						break;

					//TODO CASE FOR USER Incentive/SKILL POINT
					case "user.skillpoint":
						username=response.getResult().getParameters().get("username").toString()
						username=parsedString(username)
						println	"Showing perfomance for "+username
						if(username=="me"||username=="my"||username=="mine"||username=="I") {
							TsoUsrMastr usr=TsoUsrMastr.findByUsrCd(params.empcode)
							listIncentive(usr.getUsrName());
						}
						else {
							if(checkAuthority(params.empcode, username))
								//if(true)
							{
								listIncentive(username)
							}
							else {
								jsonData=["type":0,
									"repeat":0,
									"text":"Hey! You are not authorized to access!",
									"response":["No data"]]
								render jsonData as JSON
							}
						}
						break;


					//TODO SALES POTENTIAL
					case"sales.potential":
						println "Showing sales potential of "+	response.getResult().getParameters().get("district").toString()
						try{
							def district=response.getResult().getParameters().get("district").toString()
							district=parsedString(district)
							listPotential(district)
						}
						catch(Exception e)
						{println "Error in sales.potential : ${e}"
							jsonData=["type":0,
								"repeat":0,
								"text":"Hey! Data Not available!",
								"response":["No data"]]
							render jsonData as JSON
						}
						break;


					//TODO SALES POTENTIAL
					case "population.list":
						println "Showing population list of "+	response.getResult().getParameters().get("state").toString()
						try{
							def state=response.getResult().getParameters().get("state").toString()
							String number=parsedString(response.getResult().getParameters().get("number").toString())
							int num =Integer.parseInt(number)
							state=parsedString(state)
							listgreaterPopulation(state,num)
						}

						catch(Exception e)
						{println "Error in sales.potential : ${e}"
							jsonData=["type":0,
								"repeat":0,
								"text":"Hey! Data Not available!",
								"response":["No data"]]
							render jsonData as JSON
						}
						break;


					//TODO market.coverage
					case "market.coverage" :
						println "Showing market coverage for "+	response.getResult().getParameters().get("state").toString()
						try{
							def state=response.getResult().getParameters().get("state").toString()

							state=parsedString(state)
							listmarketcoverage(state)
						}

						catch(Exception e)
						{println "Error in sales.potential : ${e}"
							jsonData=["type":0,
								"repeat":0,
								"text":"Hey! Data Not available!",
								"response":["No data"]]
							render jsonData as JSON
						}
						break;

					//TODO DEFAULT CASE
					default:
						jsonData=["type":0,
							"repeat":0,
							"text":response.getResult().getFulfillment().getSpeech(),
							"response":["No Data"]]
						render jsonData as JSON
						break;


				}
			}
			else {
				println "Hey,sorry dear system is under maintenance!"
				jsonData=["type":0,
					"repeat":0,
					"text":"Hey,sorry dear system is under maintenance!",
					"response":["No Data"]]
				render jsonData as JSON
			}
		}
	}


	//Function to check Authorization
	def checkAuthority(empcode,empname)
	{
		def emp_list=[]
		Boolean flag=false;
		String query="""SELECT usr_name FROM tso_usr_mastr tso_usr_mastr
WHERE tso_usr_mastr.reprtng_usr_id= (SELECT tso_usr_mastr.usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_emp_cd = :usr_emp_cd )"""
		TsoOtpMastr.withSession { session ->

			def qu4=session.createSQLQuery(query)
			qu4.setParameter("usr_emp_cd",empcode)
			emp_list=qu4.list();

		}

		for(int i=0;i<emp_list.size;i++)
		{
			//if(emp_list[i].toString().matches(Pattern.compile("(.*)"+empname.toString().toLowerCase()+"(.*)")))
			//if(emp_list[i].toString().trim()==empname.toString().toLowerCase().trim())
			if(emp_list[i].toString().contains(empname))
			{
				flag=true;
				break;
			}
		}
		return flag
	}

	//Function to eliminate "" form string
	String parsedString(String str)
	{return str.substring(1, str.length()-1)}

	//Function to get list of all associate by employee name
	def listAllAssociatebyName(empname)
	{
		def jsonData;
		def emp_list=[]
		def quer="""SELECT tso_usr_mastr.usr_name, tso_usr_type_mastr.usr_type_name
FROM tso_usr_type_mastr    tso_usr_type_mastr
     INNER JOIN tso_usr_mastr tso_usr_mastr
        ON (tso_usr_type_mastr.usr_type_id = tso_usr_mastr.usr_type_id)
WHERE tso_usr_mastr.reprtng_usr_id= (SELECT tso_usr_mastr.usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_name like :usr_name ) order by tso_usr_mastr.usr_name"""
		TsoOtpMastr.withSession { session ->

			def qu4=session.createSQLQuery(quer)
			qu4.setParameter("usr_name",empname+"%")
			emp_list=qu4.list();

		}
		//println"Showing data for: "+inputData.split("of ")[1]

		if(emp_list!=[])
		{jsonData=[
				"type":3,
				"repeat":0,
				"text":empname+" has "+ emp_list.size+" associates.\nFollowing is the list:",
				"response":[]];
			for(int i=0;i<emp_list.size;i++)
			{
				jsonData.response[i]=["name":emp_list[i][0],"asso_type":emp_list[i][1]]
			}
		}
		else
		{
			jsonData=[
				"type":0,
				"repeat":0,
				"text":empname+" have no associates.",
				"response":[]];
		}
		render jsonData as JSON
	}

	//Function to get list of all associate by employee code
	def listAllAssociatebyCode(empcode)
	{   def jsonData=[];
		def emp_list=[]
		def quer="""SELECT tso_usr_mastr.usr_name, tso_usr_type_mastr.usr_type_name
FROM tso_usr_type_mastr    tso_usr_type_mastr
     INNER JOIN tso_usr_mastr tso_usr_mastr
        ON (tso_usr_type_mastr.usr_type_id = tso_usr_mastr.usr_type_id)
        WHERE tso_usr_mastr.reprtng_usr_id= (SELECT tso_usr_mastr.usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_emp_cd = :usr_emp_cd ) order by tso_usr_mastr.usr_name"""

		TsoOtpMastr.withSession { session ->

			def qu4=session.createSQLQuery(quer)
			qu4.setParameter("usr_emp_cd",empcode)
			emp_list=qu4.list();

		}
		if(emp_list!=[])
		{
			jsonData=[
				"type":3,
				"repeat":0,
				"text":"There are "+ emp_list.size+" associates.\nFollowing is the list:",
				"response":[]];
			for(int i=0;i<emp_list.size;i++)
			{
				jsonData.response[i]=["name":emp_list[i][0],"asso_type":emp_list[i][1]]
			}

		}
		else
		{jsonData=[
				"type":0,
				"repeat":0,
				"text":"Sorry you don't have associates!",
				"response":[]];

		}
		render jsonData as JSON
	}

	//Function for 	Performance
	def listPerformance(empname)
	{def jsonData=[]
		def targetList=[]
		String query="""SELECT SUM(tso_achvmnt_dtls.targt_vol) AS targt_vol,
       SUM(tso_achvmnt_dtls.achvmnt_vol) AS achvmnt_vol,
       tso_incntv_dtls.tot_sra_no,
       tso_achvmnt_dtls.usr_id
		    FROM tso_incntv_dtls    tso_incntv_dtls
        INNER JOIN tso_achvmnt_dtls tso_achvmnt_dtls
        ON (tso_incntv_dtls.usr_id = tso_achvmnt_dtls.usr_id)
        INNER JOIN tso_usr_mastr tso_usr_mastr
        ON (tso_usr_mastr.usr_id = tso_achvmnt_dtls.usr_id)
        where tso_incntv_dtls.usr_id = (select tso_usr_mastr.usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_name LIKE :usr_name) 
        Group By tso_achvmnt_dtls.usr_id"""
		TsoOtpMastr.withSession { session ->

			def qu4=session.createSQLQuery(query)
			qu4.setParameter("usr_name",empname+"%")
			targetList=qu4.list();

		}
		if(targetList!=[])
		{
			jsonData=[
				"type":5,
				"repeat":0,
				"text":"Perfomance is listed bellow:",
				"response":[["targt_vol":targetList[0][0],
					"achvmnt_vol":targetList[0][1],
					"tot_sra_no":targetList[0][2]]]
			];
		}
		else
		{
			jsonData=[
				"type":0,
				"repeat":0,
				"text":"Sorry you don't have associates!",
				"response":[]];
		}
		render jsonData as JSON;
	}


	//Function for listing target
	def listTarget(empname)
	{	def temp=[]
		def jsonData=[]
		def targetList=[]
		def query="""SELECT
       tso_targt_dtls.sra_grp_name,
       SUM(tso_targt_dtls.targt_vol) as total_targt_vol,
       SUM(tso_targt_dtls.achvmnt_vol) as total_achvmnt_vol,
       tso_usr_mastr.usr_name
FROM tso_targt_dtls    tso_targt_dtls
     INNER JOIN tso_usr_mastr tso_usr_mastr
        ON (tso_targt_dtls.usr_id = tso_usr_mastr.usr_id)
WHERE (tso_usr_mastr.usr_name like :usr_name) 
group by tso_targt_dtls.sra_grp_name"""
		TsoOtpMastr.withSession { session ->

			def qu4=session.createSQLQuery(query)
			qu4.setParameter("usr_name",empname+"%")
			targetList=qu4.list();

		}

		
		for(int i=0;i<targetList.size;i++)
		{
			temp[i]=[
				"sra_grp_name":targetList[i][0],
				"targt_vol":targetList[i][1],
				"achvmnt_vol":targetList[i][2]
			]
		}
		
		jsonData=[
			"type":4,
			"repeat":0,
			"text":"Targets and achievement are listed below:",
			"response":temp];
		render jsonData as JSON;
	}


	//Function for listincentive
	def listIncentive (empname)
	{
		def jsonData1=[]
		def jsonData=[]
		def targetList=[]
		String query="""SELECT tso_achvmnt_dtls.skill_point,
       tso_achvmnt_dtls.inctv_amt,
       tso_sragrp_mastr.sra_grp_name,
       tso_achvmnt_dtls.usr_id
      FROM tso_sragrp_mastr    tso_sragrp_mastr
     INNER JOIN tso_achvmnt_dtls tso_achvmnt_dtls
        ON (tso_sragrp_mastr.sra_grp_id = tso_achvmnt_dtls.sra_grp_id)
         INNER JOIN tso_usr_mastr tso_usr_mastr
        ON (tso_usr_mastr.usr_id = tso_achvmnt_dtls.usr_id)
         WHERE tso_achvmnt_dtls.usr_id= (SELECT tso_usr_mastr.usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_name like  :usr_name) """
		TsoOtpMastr.withSession { session ->

			def qu4=session.createSQLQuery(query)
			qu4.setParameter("usr_name",empname+"%")

			targetList=qu4.list();

		}

		int temp1=0,temp2=0;


		if(targetList.size!=0)
		{
			for(int i=0;i<targetList.size;i++)
			{
				jsonData1[i]=["skill_point":targetList[i][0],"inctv_amt":targetList[i][1],"sra_grp_name":targetList[i][2]]
				temp1=temp1+targetList[i][0]
				temp2=temp2+targetList[i][1]
			}

		}

		jsonData=[
			"type":6,
			"repeat":0,
			"text":"Hey total incentives  ${temp2} , Skill Points ${temp1}.Details are as listed below: ",
			"response":jsonData1
		];

		render jsonData as JSON;
	}

	//Function for Potential value
	def listPotential(disname)
	{
		def targetList=[]
		def jsonData=[]

		String query="""select distrct_name,lat_val,lng_val,rural_mpv_val,urban_mpv_val,rural_mpv_grade,urban_mpv_grade from ujlpocdb.ujl_distrct_mastr where distrct_name like :distrct_name"""
		UjlStcstpntTypeMastr.withSession { session ->

			def qu4=session.createSQLQuery(query)
			qu4.setParameter("distrct_name",disname+"%")
			targetList=qu4.list();

		}
		if(targetList!=[])
		{jsonData=[
				"type":7,
				"repeat":0,
				"text":"Showing Sales potential for " +disname,
				"response":[["distrct_name":targetList[0][0],"lat_val":targetList[0][1],"lng_val":targetList[0][2],"rural_mpv_val":targetList[0][3],"urban_mpv_val":targetList[0][4],"rural_mpv_grade":targetList[0][5],"urban_mpv_grade":targetList[0][6]]]
			]
		}
		else
		{
			jsonData=[
				"type":0,
				"repeat":0,
				"text":"Sorry Data not avilable!",
				"response":["Do Data"]]
		}
		render jsonData as JSON
	}


	//Function for population list
	def listgreaterPopulation(statename,town_popltn)
	{
		def jsonData=[]
		def targetList=[]
		String query="""SELECT ujl_town_mastr.town_name,
       ujl_town_mastr.town_popltn,
       ujl_town_mastr.lat_val,
       ujl_town_mastr.lng_val,
       ujl_state_mastr.state_name
FROM ujlpocdb.ujl_town_mastr    ujl_town_mastr
     INNER JOIN ujlpocdb.ujl_state_mastr ujl_state_mastr
        ON (ujl_town_mastr.state_id = ujl_state_mastr.state_id)
WHERE     (ujl_town_mastr.town_popltn > :town_popltn)
      AND (ujl_state_mastr.state_name like :state_name)"""

		UjlStateMastr.withSession { session ->

			def qu4=session.createSQLQuery(query)
			qu4.setParameter("state_name",statename+"%")
			qu4.setParameter("town_popltn",town_popltn)
			targetList=qu4.list();

		}

		if(targetList.size!=null)
		{jsonData=[
				"type":9,
				"repeat":0,
				"text":"Showing Population  for "+statename+ " with greater than "+town_popltn,
				"response":[]];
			for(int i=0;i<targetList.size;i++)
			{
				jsonData.response[i]=["town_name":targetList[i][0],"town_popltn":targetList[i][1],"lat_val":targetList[i][2],"lng_val":targetList[i][3]]
			}

		}
		render jsonData as JSON
	}

	//Function for market coverage
	def listmarketcoverage(city)
	{
		def jsonData=[]
		def targetList=[]
		String query="""select lat_val,lng_val from ujlpocdb.ujl_state_mastr where state_name like :state_name"""

		UjlStcstpntTypeMastr.withSession { session ->

			def qu4=session.createSQLQuery(query)
			qu4.setParameter("state_name",city+"%")
			targetList=qu4.list();

		}
		if(targetList.size!=null)
		{jsonData=[
				"type":10,
				"repeat":0,
				"text":"Market Coverage of "+city,
				"response":[["lat_val":targetList[0][0],"lng_val":targetList[0][1]]]];

		}
		else
		{
			jsonData=[
				"type":0,
				"repeat":0,
				"text":"Sorry data for state "+city+" not available",
				"response":["No Data"]];
		}
		render jsonData as JSON

	}

}
