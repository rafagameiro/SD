package microgram.impl.srv.soap;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.xml.ws.Endpoint;

import com.sun.net.httpserver.HttpServer;

import discovery.Discovery;
import microgram.api.soap.SoapProfiles;
import utils.IP;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public class ProfilesSoapServer {
	private static Logger Log = Logger.getLogger(ProfilesSoapServer.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s");
	}

	public static final int PORT = 5555;
	public static final String SERVICE = "Microgram-Profiles";
	public static String SERVER_BASE_URI = "http://%s:%s/soap";
	public static String SERVER_BASE_PATH ="/soap/"+SoapProfiles.NAME;

	public static void main(String[] args) throws Exception {

		// Create an HTTP server, accepting requests at PORT (from all local interfaces)
		HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);

		String ip = IP.hostAddress();
		String serverUrl = String.format(SERVER_BASE_URI, ip, PORT);
	
		// Create the SOAP Endpoint
		Endpoint soapEndpoint = Endpoint.create(new ProfilesWebService());
		
		// Publish the SOAP webservice, under the "http://<ip>:<port>/soap"
		soapEndpoint.publish(server.createContext(SERVER_BASE_PATH));
		
		server.setExecutor(Executors.newCachedThreadPool());
		
		// Start Serving Requests: both SOAP Requests
		server.start();
		Log.info(String.format("%s Soap Server ready @ %s\n", SERVICE, ip + ":" + PORT));

		Discovery.announce(SERVICE, serverUrl);
	}

}
