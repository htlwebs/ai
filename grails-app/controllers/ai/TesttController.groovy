package ai
import com.m3.tso.det.*

class TesttController {

    def index() {
		
	}
	def gmapcircle()
	{
	def jsonData=[]
		def targetList=[]
		String query="""select distinct stcstpnt_name,lat_val,lng_val from ujl_stcstpnt_mastr where cens_city_name = :state_name"""

		UjlStcstpntTypeMastr.withSession { session ->

			def qu4=session.createSQLQuery(query)
			qu4.setParameter("state_name",params.city)
			targetList=qu4.list();

		}
	
		if(targetList.size!=null)
		{for(int i=0;i<targetList.size;i++)
			jsonData[i]=[targetList[i][1],targetList[i][2]]
		}
		
def aa="{"
for(int i=0;i<jsonData.size;i++)
{
aa+="${i}:{center:{lat:"+jsonData[i][0]+",lng:"+jsonData[i][1]+"}},"
	}
	
aa+="}"

//		for(int i=0;i<.size;i++)
//		{println "a"}
		[oo:8888888,a:aa]
	}
	
	def listmarketcoverage()
	{//TODO
		def jsonData=[]
		def targetList=[]
		String query="""select distinct stcstpnt_name,lat_val,lng_val from ujl_stcstpnt_mastr where cens_city_name = :state_name"""

		UjlStcstpntTypeMastr.withSession { session ->

			def qu4=session.createSQLQuery(query)
			qu4.setParameter("state_name",params.city)
			targetList=qu4.list();

		}
	
		if(targetList.size!=null)
		{for(int i=0;i<targetList.size;i++)
			jsonData[i]=[targetList[i][1],targetList[i][2]]
		}
		
def aa="{"
for(int i=0;i<jsonData.size;i++)
{
aa+="${i}:{center:{lat:"+jsonData[i][0]+",lng:"+jsonData[i][1]+"}},"
	}
	
aa+="}"

println aa
	
}
}