package cas;  

import java.io.IOException;  
import java.util.logging.Logger;  
import java.util.regex.Matcher;  
import java.util.regex.Pattern;  

import org.apache.commons.httpclient.HttpClient;  
import org.apache.commons.httpclient.NameValuePair;  
import org.apache.commons.httpclient.methods.PostMethod;  

/**
 * des  网上下载的用于测试SSO Rest接口
 */ 

public final class SSOClient {  
	private static final Logger LOG = Logger.getLogger(SSOClient.class.getName());  

	private SSOClient(){  
		// static-only access  
	}  

	public static String getTicket(final String server, final String username, final String password, final String service)  {  
		notNull(server, "server must not be null");  
		notNull(username, "username must not be null");  
		notNull(password, "password must not be null");  
		notNull(service, "service must not be null");  

		String Ticket = getServiceTicket(server, getTicketGrantingTicket(server, username, password), service);

		return Ticket;
	}  
	
	//获取服务票据
	private static String getServiceTicket(final String server, final String ticketGrantingTicket, final String service) {  
		if (ticketGrantingTicket == null)  
			return null;  

		final HttpClient client = new HttpClient();  
		final PostMethod post = new PostMethod(server + "/" + ticketGrantingTicket);  
		post.setRequestBody(new NameValuePair[] { new NameValuePair("service", service) });  

		//返回值
		String Ticket="";
		try  {  
			client.executeMethod(post);  
			final String response = post.getResponseBodyAsString();
			Ticket = response;
			switch (post.getStatusCode())  {  
			case 200:  
				return response;  
			default:  
				LOG.warning("Invalid response code (" + post.getStatusCode() + ") from CAS server!");  
				LOG.info("Response (1k): " + response.substring(0, Math.min(1024, response.length())));  
				break;  
			}  
		}  
		catch (final IOException e) {  
			LOG.warning(e.getMessage());  
		}  
		finally  {  
			post.releaseConnection();  
		}  

		return Ticket;  
	}  

	//获取TGT
	private static String getTicketGrantingTicket(final String server, final String username, final String password) {  
		final HttpClient client = new HttpClient();  
		final PostMethod post = new PostMethod(server);  
		post.setRequestBody(new NameValuePair[] { new NameValuePair("username", username), new NameValuePair("password", password)});

		//返回值
		String TGTTmep="";

		try  {  
			client.executeMethod(post);  
			final String response = post.getResponseBodyAsString();  

			TGTTmep = response;

			switch (post.getStatusCode()) {  
			case 201: {  
				final Matcher matcher = Pattern.compile(".*action=\".*/(.*?)\".*").matcher(response);  

				if (matcher.matches())  
					return matcher.group(1);  
				LOG.warning("Successful ticket granting request, but no ticket found!");  
				LOG.info("Response (1k): " + response.substring(0, Math.min(1024, response.length())));  
				break;  
			}  
			default:  
				LOG.warning("Invalid response code (" + post.getStatusCode() + ") from CAS server!");  
				LOG.info("Response (1k): " + response.substring(0, Math.min(1024, response.length())));  
				break;  
			}  
		}  
		catch (final IOException e) {  
			LOG.warning(e.getMessage());  
		}  
		finally {  
			post.releaseConnection();  
		}  

		String TGT = TGTTmep.substring(TGTTmep.indexOf("TGT-"),TGTTmep.indexOf("resultCode")-3);
		
		return TGT;  
	}  

	private static void notNull(final Object object, final String message)	{  
		if (object == null)  
			throw new IllegalArgumentException(message);  
	}  

	public static void main(final String[] args) 	{  
/*		final String server = "http://192.168.97.94:8081/portal/rest/tickets";  
		final String username = "admin";  
		final String password = "123456";  
		final String service = "http://192.168.99.192:8088/pahfpt/";  */
		final String server = "http://112.29.132.30:28080/portal/rest/tickets";  
		final String username = "yunwei";  
		final String password = "123456";  
		final String service = "http://112.29.132.30:38081/wxhfyw/";  

		LOG.info(getTicket(server, username, password, service));  
	}  
}  