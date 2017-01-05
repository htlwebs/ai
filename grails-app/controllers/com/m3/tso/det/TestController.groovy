package com.m3.tso.det
import grails.converters.JSON
import groovy.json.*

import java.util.regex.Pattern
class TestController {

	def index() { }
	//Codes for Testing AI

	int temp1=0,temp2=0
	//Function to get input data to AI
	def getAidata()
	{	def type=0;
		def repeat=0;
		def jsonData=["type":type,
			"repeat":repeat,
			"response":["No data"]];

		String inputData=params.inputdata.toString().toLowerCase();
		println inputData;

		def employees=[]
		try{
			// Show my/mine associate
			if(inputData.matches(Pattern.compile("(.*)(my|mine)(.*)(associate)(.*)")))
			{
				println "Showing MY assoicates"
				listAllAssociatebyCode(params.empcode)
			}
			//associate of manish kumar sing
			else if(inputData.matches(Pattern.compile("(.*)(associate)(.*)(of)(.*)")))
			{
				println "Showing assoicates for "+inputData.split("of ")[1].trim()
				listAllAssociatebyName(inputData.split("of ")[1].trim());
			}
			//my/ mine target or achievement
			else if(inputData.matches(Pattern.compile("(.*)(my|mine)(.*)(target|achievement)(.*)")))
			{
				println "Showing MY target"
				TsoUsrMastr usr=TsoUsrMastr.findByUsrCd(params.empcode)
				employees=listTarget(usr.getUsrName());
				type=4;
				repeat=0;
				jsonData=[
					"type":type,
					"repeat":repeat,
					"text":"Your targets and achievement are listed below:",
					"response":[]];
				for(int i=0;i<employees.size;i++)
				{
					jsonData.response[i]=employees[i]
				}
			}

			//target of manish kumar sing
			else if(inputData.matches(Pattern.compile("(.*)(target|achievement)(.*)(of)(.*)")))
			{
				employees=listTarget(inputData.split("of ")[1].trim());
				type=4;
				repeat=0;
				jsonData=[
					"type":type,
					"repeat":repeat,
					"text":"Targets and achievements of " +inputData.split("of ")[1].trim()+" are listed below:",
					"response":[]];
				for(int i=0;i<employees.size;i++)
				{
					jsonData.response[i]=employees[i]
				}
			}


			//my/mine performance
			else if(inputData.matches(Pattern.compile("(.*)(my|mine)(.*)(performance)(.*)")))
			{
				TsoUsrMastr usr=TsoUsrMastr.findByUsrCd(params.empcode)
				employees=listPerformance(usr.getUsrName());
				type=5;
				repeat=0;
				jsonData=[
					"type":type,
					"repeat":repeat,
					"text":"Your perfomance is :",
					"response":[employees]];

			}

			else if(inputData.matches(Pattern.compile("(.*)(performance of)(.*)")))
			{//Sales performance of agra for chek
				if(inputData.matches(Pattern.compile("(.*)(sales performance of)(.*)")))
				{
					println "testing" + inputData.split("for ")[1].trim() +"    , "+inputData.substring(inputData.indexOf("of ") + 2, inputData.indexOf("for ")).trim()
					employees=listsalesperformance(inputData.split("for ")[1].trim(),inputData.substring(inputData.indexOf("of ") + 2, inputData.indexOf("for ")).trim());
					type=8;
					repeat=0;
					jsonData=[
						"type":type,
						"repeat":repeat,
						"text":"Showing Sales performance for " +inputData.split("for ")[1].trim(),
						"response":[employees]]
				}
				else
				{
					println"Showing data for: "+inputData.split("of ")[1].trim()
					employees=listPerformance(inputData.split("of ")[1].trim());
					type=5;
					repeat=0;
					jsonData=[
						"type":type,
						"repeat":repeat,
						"text":"Performance of " +inputData.split("of ")[1].trim()+" is:",
						"response":[employees]];

				}
			}
			//Show skill point/ incentive of manish kumar singh
			else if(inputData.matches(Pattern.compile("(.*)(incentive|skill point)(.*)(of)(.*)")))
			{
				println"Showing data for: "+inputData.split("of ")[1]
				employees=listIncentive(inputData.split("of ")[1].trim());
				type=6;
				repeat=0;
				jsonData=[
					"type":type,
					"repeat":repeat,
					"text":"Incentives of " +inputData.split("of ")[1].trim()+" is:",
					"response":employees];

			}


			//my skill point/incentive
			else if(inputData.matches(Pattern.compile("(.*)(my|mine)(.*)(incentive|skill point)(.*)")))
			{
				employees=listIncentive("SHARAFAT ALI");
				type=6;
				repeat=0;
				jsonData=[
					"type":type,
					"repeat":repeat,
					"text":"Hey your Incentives total ${temp2} , Skill Points ${temp1}.Details are as listed below: ",
					"response":employees];

			}


			//Sales potential of Bagalkot
			else if(inputData.matches(Pattern.compile("(.*)(potential of)(.*)")))
			{
				employees=listPotential(inputData.split("of ")[1].trim());
				type=7;
				repeat=0;
				jsonData=[
					"type":type,
					"repeat":repeat,
					"text":"Showing Sales potential for " +inputData.split("of ")[1].trim(),
					"response":[employees]];
			}

			//show village of goa whose population is greater than 5000
			else if(inputData.matches(Pattern.compile("(.*)(village|town|city)(.*)(population)(.*)(greater than)(.*)")))
			{
				//println"Showing villages  of "+inputData.substring(inputData.indexOf("of")+2 , inputData.indexOf("whose")).trim()+ " with population greater than "+inputData.split("greater than")[1].trim()
				employees=listgreaterPopulation(inputData.substring(inputData.indexOf("of ") + 2, inputData.indexOf("whose")).trim(),Integer.parseInt(inputData.split("greater than")[1].trim()));
				//employees=listgreaterPopulation("Kerala",5000)
				type=9;
				repeat=0;
				jsonData=[
					"type":type,
					"repeat":repeat,
					"text":"Showing Population  for "+inputData.substring(inputData.indexOf("of")+2 , inputData.indexOf("whose")).trim()+ " with greater than "+inputData.split("greater than")[1].trim(),
					"response":employees];
			}


			//Market Coverage of Goa
			else if(inputData.matches(Pattern.compile("(.*)(market coverage of)(.*)")))
			{
				println"Market Coverage of "+inputData.split("of")[1].trim()
				employees=listmarketcoverage(inputData.split("of")[1].trim());
				type=10;
				repeat=0;
				jsonData=[
					"type":type,
					"repeat":repeat,
					"text":"Market Coverage of "+inputData.split("of")[1].trim(),
					"response":[employees]];
			}

			else
			{
				jsonData=[
					"type":0,
					"text":"Sorry i didn't get you! Please try again!"]
			}

		}
		catch(Exception e)
		{
			jsonData=[
				"type":0,
				"text":"Sorry i didn't get you! Please try again!"]
			println "Error : ${e}"
		}
		render jsonData as JSON
	}

	//Function to get list of all associate by employee code
	def listAllAssociatebyCode(empcode)
	{   def jsonData;
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
			"text":"You have "+ emp_list.size+" associates.\nFollowing is the list:",
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
	


	//Function for listing target
	def listTarget(empname)
	{
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
WHERE (tso_usr_mastr.usr_name like :usr_name) AND (tso_usr_mastr.reprtng_usr_id=(select usr_id from tso_usr_mastr where usr_cd=:usr_cd) OR tso_usr_mastr.usr_id=(select usr_id from tso_usr_mastr where usr_name like :usr_name)) group by tso_targt_dtls.sra_grp_name"""
		TsoOtpMastr.withSession { session ->

			def qu4=session.createSQLQuery(query)
			qu4.setParameter("usr_name",empname+"%")
			qu4.setParameter("usr_cd",params.empcode)
			targetList=qu4.list();

		}
		for(int i=0;i<targetList.size;i++)
		{
			jsonData[i]=[
				"sra_grp_name":targetList[i][0],
				"targt_vol":targetList[i][1],
				"achvmnt_vol":targetList[i][2],
			]
		}
		println "listTarget : " + jsonData
		return jsonData;
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
        where tso_incntv_dtls.usr_id = (select tso_usr_mastr.usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_name LIKE :usr_name) AND (tso_usr_mastr.reprtng_usr_id=(select usr_id from tso_usr_mastr where usr_cd=:usr_cd) OR tso_usr_mastr.usr_id=(select usr_id from tso_usr_mastr where usr_name like :usr_name))
        Group By tso_achvmnt_dtls.usr_id"""
		TsoOtpMastr.withSession { session ->

			def qu4=session.createSQLQuery(query)
			qu4.setParameter("usr_name",empname+"%")
			qu4.setParameter("usr_cd",params.empcode)
			targetList=qu4.list();

		}


		jsonData=[
			"targt_vol":targetList[0][0],
			"achvmnt_vol":targetList[0][1],
			"tot_sra_no":targetList[0][2],
		]


		return jsonData;
	}

	//Function for listincentive
	def listIncentive (empname)
	{
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
         WHERE tso_achvmnt_dtls.usr_id= (SELECT tso_usr_mastr.usr_id FROM tso_usr_mastr WHERE tso_usr_mastr.usr_name like  :usr_name) AND (tso_usr_mastr.reprtng_usr_id=(select usr_id from tso_usr_mastr where usr_cd=:usr_cd) OR tso_usr_mastr.usr_id=(select usr_id from tso_usr_mastr where usr_name like :usr_name))  """
		TsoOtpMastr.withSession { session ->

			def qu4=session.createSQLQuery(query)
			qu4.setParameter("usr_name",empname+"%")
			qu4.setParameter("usr_cd",params.empcode)
			targetList=qu4.list();

		}



		if(targetList.size!=0)
		{
			for(int i=0;i<targetList.size;i++)
			{
				jsonData[i]=["skill_point":targetList[i][0],"inctv_amt":targetList[i][1],"sra_grp_name":targetList[i][2]]
				temp1=temp1+targetList[i][0]
				temp2=temp2+targetList[i][1]
			}

		}


		return jsonData;
	}

	//Function for listBrand
	def listBrand()
	{
		render listsalesperformance("chek","agra") as JSON
	}
	//Function for greeting
	def showGreeting()
	{}


	//Function for Population list
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
		{
			for(int i=0;i<targetList.size;i++)
			{
				jsonData[i]=["town_name":targetList[i][0],"town_popltn":targetList[i][1],"lat_val":targetList[i][2],"lng_val":targetList[i][3]]
			}

		}
		return jsonData
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
		if(targetList.size!=null)
		{
			jsonData=["distrct_name":targetList[0][0],"lat_val":targetList[0][1],"lng_val":targetList[0][2],"rural_mpv_val":targetList[0][3],"urban_mpv_val":targetList[0][4],"rural_mpv_grade":targetList[0][5],"urban_mpv_grade":targetList[0][6]	]
		}
		
		return jsonData
	}

	//Function for sales performance
	def listsalesperformance(brandname,cityname)
	{
		def jsonData=[]
		def targetList=[]
		String query="""SELECT SUM(ujl_sales_dtls.sales_val)/SUM(ujl_sales_dtls.sales_volmn),
       ujl_stcstpnt_mastr.cens_city_name,
       ujl_stcstpnt_mastr.lat_val,
       ujl_stcstpnt_mastr.lng_val,
       ujl_brand_mastr.brand_name,
       ujl_sales_dtls.month,
       ujl_sales_dtls.year
FROM (ujlpocdb.ujl_sales_dtls    ujl_sales_dtls
      INNER JOIN ujlpocdb.ujl_brand_mastr ujl_brand_mastr
         ON (ujl_sales_dtls.brand_id = ujl_brand_mastr.brand_id))
     INNER JOIN ujlpocdb.ujl_stcstpnt_mastr ujl_stcstpnt_mastr
        ON (ujl_sales_dtls.stcstpnt_id = ujl_stcstpnt_mastr.stcstpnt_id)
        WHERE ujl_brand_mastr.brand_name like :brand_name AND ujl_stcstpnt_mastr.cens_city_name like :cens_city_name
        GROUP BY ujl_brand_mastr.brand_name,ujl_stcstpnt_mastr.cens_city_name"""

		UjlStcstpntTypeMastr.withSession { session ->

			def qu4=session.createSQLQuery(query)
			qu4.setParameter("brand_name",brandname+"%")
			qu4.setParameter("cens_city_name",cityname+"%")
			targetList=qu4.list();

		}
		if(targetList.size!=null)
		{
			jsonData=["per_capital_sales":targetList[0][0],"city_name":targetList[0][1],"lat_val":targetList[0][2],"lng_val":targetList[0][3],"brnd_name":targetList[0][4],"month":targetList[0][5],"year":targetList[0][6]]
		}
		return jsonData
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
		{
			jsonData=["lat_val":targetList[0][0],"lng_val":targetList[0][1]]
		}
		return jsonData

	}

	//Function for testing regex expression
	def tests()
	{
		//		println "testing"
		//		listTarget("SHARAFAT ALI")
		Pattern match= Pattern.compile("(.*)(abc|xyz)(.*)")
		String ss= params.a.toString() ;
		if(ss.matches(match))
		{println "Found"}
		else
		{
			println "No Found"
		}
	}

}
