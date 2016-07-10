package cas;  

import java.io.IOException;  
import java.util.UUID;
import java.util.logging.Logger;  

import org.apache.commons.httpclient.HttpClient;  
import org.apache.commons.httpclient.NameValuePair;  
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.PostMethod;  

/**
 * des 公司内部SSO Rest接口测试
 */ 

public final class CASClientUtil {  
	private static final Logger LOG = Logger.getLogger(CASClientUtil.class.getName());  

	private CASClientUtil(){  
		// static-only access  
	}  

	/**
	 * Rest登录系统
	 * @param server
	 * @param username
	 * @param password
	 * @param service
	 * @return TGT
	 */
	public static String sysLogin(final String server, final String username, final String password, final String service)  { 
		//final String server = "http://192.168.99.122:8081/portal/rest/tickets";  
		//注意登录成功后，就会带用户的权限
		notNull(server, "server must not be null");  
		notNull(username, "username must not be null");  
		notNull(password, "password must not be null");  
		notNull(service, "service must not be null");  

		final HttpClient client = new HttpClient();  
		final PostMethod post = new PostMethod(server);  
		post.setRequestBody(new NameValuePair[] { new NameValuePair("service", service),new NameValuePair("username", username),new NameValuePair("password", password)});  

		String TGT="";
		try  {  
			client.executeMethod(post);  
			final String response = post.getResponseBodyAsString();
			TGT = response;

			switch (post.getStatusCode())  {  
			case 201:  
				TGT = response;
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

		TGT = TGT.substring(TGT.indexOf("TGT-"),TGT.indexOf("resultCode")-3);
		return TGT;  
	} 

	/**
	 * 获取票据
	 * @param server
	 * @param service
	 * @param sessionid
	 * @return Ticket
	 */
	public static String getTicket(final String server , final String service , final String sessionid)  { 
		//final String server = "http://192.168.99.122:8081/portal/rest/tickets";  
		//sessionid一般即为这个TGT
		notNull(server, "server must not be null");  
		notNull(sessionid, "sessionid must not be null");  
		notNull(service, "service must not be null"); 

		String Ticket="";

		final HttpClient client = new HttpClient();  
		final PostMethod post = new PostMethod(server + "/" + sessionid);  
		post.setRequestBody(new NameValuePair[] { new NameValuePair("service", service)});

		try  {  
			client.executeMethod(post);  
			final String response = post.getResponseBodyAsString();
			Ticket = response;
		}  
		catch (final IOException e) {  
			LOG.warning(e.getMessage());  
		}  
		finally  {  
			post.releaseConnection();  
		}  
		return Ticket;
	}  

	/**
	 * 验证票据
	 * @param ticket
	 * @param service
	 * @return 
	 */
	public static String ticketValidate(final String validateServer, final String ticket, final String service){
		//final String validateServer = "http://192.168.99.122:8081/portal/ticketValidate";  
		notNull(validateServer, "validateServer must not be null");  
		notNull(ticket, "ticket must not be null");  
		notNull(service, "service must not be null");  

		final HttpClient client = new HttpClient();  
		final PostMethod post = new PostMethod(validateServer); 
		post.setRequestBody(new NameValuePair[] {new NameValuePair("ticket", ticket),new NameValuePair("service", service)});

		String result  = "";
		try  {  
			client.executeMethod(post);  
			final String response = post.getResponseBodyAsString();
			result = response;
		}  
		catch (final IOException e) {  
			LOG.warning(e.getMessage());  
		}  
		finally  {  
			post.releaseConnection();  
		}  

		return result;
	}

	/**
	 * Rest获取token
	 * @param server
	 * @param sysKey
	 * @return
	 */
	public static String getToken(final String grantTicketsServer, final String sysKey, final String service){
		//final String grantTicketsServer = "http://192.168.99.122:8081/portal/rest/granttickets";  
		notNull(grantTicketsServer, "grantTicketsServer must not be null");  
		notNull(sysKey, "sysKey must not be null");  
		notNull(service, "service must not be null");  

		final HttpClient client = new HttpClient();  
		final PostMethod post = new PostMethod(grantTicketsServer + "/" + sysKey);  
		post.setRequestBody(new NameValuePair[] { new NameValuePair("service", service)});

		String token  = "";
		try  {  
			client.executeMethod(post);  
			final String response = post.getResponseBodyAsString();
			token = response;
		}  
		catch (final IOException e) {  
			LOG.warning(e.getMessage());  
		}  
		finally  {  
			post.releaseConnection();  
		}  

		return token;
	}

	/**
	 * 验证token
	 * @param validateticketsServer
	 * @param sysKey
	 * @param token
	 * @return
	 */
	public static String tokenValite(final String validateticketsServer, final String sysKey, final String token){
		//final String validateticketsServer = "http://192.168.99.122:8081/portal/rest/validatetickets";  
		notNull(validateticketsServer, "validateticketsServer must not be null");  
		notNull(sysKey, "sysKey must not be null");  
		notNull(token, "token must not be null");  

		final HttpClient client = new HttpClient();  
		final PostMethod post = new PostMethod(validateticketsServer + "/" + sysKey);  
		post.setRequestBody(new NameValuePair[] { new NameValuePair("ticket", token)});

		String result  = "";
		try  {  
			client.executeMethod(post);  
			final String response = post.getResponseBodyAsString();
			result = response;
		}  
		catch (final IOException e) {  
			LOG.warning(e.getMessage());  
		}  
		finally  {  
			post.releaseConnection();  
		}  
		return result;
	}

	/**
	 * Rest退出系统
	 * @param server
	 * @param sessionid
	 * @return
	 */
	public static String sysLogout(final String server, final String sessionid)  {  
		//final String server = "http://192.168.99.122:8081/portal/rest/tickets";  
		notNull(server, "server must not be null");  
		notNull(sessionid, "sessionid must not be null");  

		final HttpClient client = new HttpClient();  
		final DeleteMethod delete = new DeleteMethod(server + "/" + sessionid);
		try  {  
			client.executeMethod(delete);  
			final int response = delete.getStatusCode();
			LOG.info("删除session请求返回:" + response);
		}  
		catch (final IOException e) {  
			LOG.warning(e.getMessage());  
		}  
		finally  {  
			delete.releaseConnection();  
		}  
		return null;  
	} 

	/**
	 * 对象是否为空
	 * @param object
	 * @param message
	 */
	private static void notNull(final Object object, final String message)	{  
		if (object == null)  
			throw new IllegalArgumentException(message);  
	}  

	public static void main(final String[] args) 	{  

		//登录
		final String server = "http://192.168.99.122:8081/portal/rest/tickets";  
		final String validateServer = "http://192.168.99.122:8081/portal/ticketValidate";  
		final String grantTicketsServer = "http://192.168.99.122:8081/portal/rest/granttickets";  
		final String validateticketsServer = "http://192.168.99.122:8081/portal/rest/validatetickets";  
		final String username = "admin";  
		final String password = "123456";  
		final String service = "http://192.168.99.122:8088/pabzkk"; 

		//获取TGT 也就是session
		String TGT = sysLogin(server, username, password, service);
		LOG.info("登录后的TGT： " + TGT);

		//获取票据
		String Ticket = getTicket(server, service ,TGT);
		LOG.info("获取的票据： " + Ticket);

		//验证票据
		String result = ticketValidate(validateServer,Ticket,service);	
		LOG.info("验证票据是否有效： " + result);

		//获取Token
		String sysKey = UUID.randomUUID().toString();//每次调用前重新生成
		String token = getToken(grantTicketsServer,sysKey,service);
		LOG.info("token的票据： " + token);

		//验证Token
		String tokenResult = tokenValite(validateticketsServer,sysKey,token);
		LOG.info("验证token是否有效： "+  tokenResult);
		
		//退出
		String resultLogout = sysLogout(server,TGT);
		LOG.info("单点登录退出: " +  resultLogout);

	}  
}