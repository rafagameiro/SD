package microgram.impl.clt.soap;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.api.soap.SoapProfiles;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public class SoapProfilesClient extends SoapClient implements Profiles {

	SoapProfiles impl;
	private static final String WSDL = "?wsdl";
	private static final String PROFILE = "/profiles";
	private static Logger Log = Logger.getLogger(SoapProfilesClient.class.getName());
	private String serverUri;

	public SoapProfilesClient(URI serverUri) {
		super(serverUri);
		this.serverUri = serverUri.toString() + PROFILE;
	}

	private SoapProfiles impl() {

		QName QNAME = new QName(SoapProfiles.NAMESPACE, SoapProfiles.NAME);

		if (impl == null) {
			try {
				Service service = Service.create(new URL(serverUri + WSDL), QNAME);
				impl = service.getPort(microgram.api.soap.SoapProfiles.class);

			} catch (MalformedURLException e) {
				Log.info("MalformedURL Exception, reason: " + e.getMessage());
				e.printStackTrace();
			}

		}
		return impl;
	}

	@Override
	public Result<Profile> getProfile(String userId) {
		return super.tryCatchResult(() -> impl().getProfile(userId));
	}

	@Override
	public Result<Void> createProfile(Profile profile) {

		return super.tryCatchVoid(() -> impl().createProfile(profile));
	}

	@Override
	public Result<Void> deleteProfile(String userId) {

		return super.tryCatchVoid(() -> impl().deleteProfile(userId));
	}

	@Override
	public Result<List<Profile>> search(String prefix) {

		return super.tryCatchResult(() -> impl().search(prefix));
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {

		return super.tryCatchVoid(() -> impl().follow(userId1, userId2, isFollowing));
	}

	@Override
	public Result<Set<String>> getFollowing(String userId) {

		return super.tryCatchResult(() -> impl().getFollowing(userId));
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {

		return super.tryCatchResult(() -> impl().isFollowing(userId1, userId2));
	}

}
