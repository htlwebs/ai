package com.m3.tso.det
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7' )
class ApiConsumerController {

	static def postText(String baseUrl, String path, query, method = Method.POST) {
		try {
			def ret = null
			def http = new HTTPBuilder(baseUrl)

			// perform a POST request, expecting TEXT response
			http.request(method, ContentType.TEXT) {
				uri.path = path
				uri.query = query
				headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

				// response handler for a success response code
				response.success = { resp, reader ->
					println "response status: ${resp.statusLine}"
					println 'Headers: -----------'
					resp.headers.each { h ->
						println " ${h.name} : ${h.value}"
					}

					ret = reader.getText()

					println 'Response data: -----'
					println ret
					println '--------------------'
				}
			}
			return ret

		} catch (groovyx.net.http.HttpResponseException ex) {
			ex.printStackTrace()
			return null
		} catch (java.net.ConnectException ex) {
			ex.printStackTrace()
			return null
		}
	}
	 

	 
	static def getText(String baseUrl, String path, query) {
		return postText(baseUrl, path, query, Method.GET)
	}

}
