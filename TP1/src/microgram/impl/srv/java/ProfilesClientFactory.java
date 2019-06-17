package microgram.impl.srv.java;

import java.net.URI;

import microgram.api.java.Profiles;
import microgram.impl.clt.java.RetryProfilesClient;
import microgram.impl.clt.rest.RestProfilesClient;
import microgram.impl.clt.soap.SoapProfilesClient;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public class ProfilesClientFactory {

	private static final String REST = "/rest";
	private static final String SOAP = "/soap";

	public static Profiles getProfilesClient(URI uri) {
		
		Profiles profiles = null;
		String uriString = uri.toString();
		if (uriString.endsWith(REST))
			profiles = new RestProfilesClient(uri);
		else if (uriString.endsWith(SOAP))
			profiles = new SoapProfilesClient(uri);

		if (profiles == null)
			throw new RuntimeException("Unknown service type..." + uri);

		return new RetryProfilesClient(profiles);
	}
}
