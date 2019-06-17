package impl.clt.soap;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import impl.srv.soap.SoapMediaStorageServer;
import microgram.api.soap.MicrogramException;
import microgram.api.soap.SoapMedia;

public class UploadMediaClient {
	private static Logger Log = Logger.getLogger(UploadMediaClient.class.getName());

	private static final String WSDL = "?wsdl";

	private static final File EARTH = new File("earth.jpg");

	private static final int RETRY_PERIOD = 1000;

	public static void main(String[] args) throws IOException, InterruptedException {

		String serverUrl = args.length > 0 ? args[0]
				: String.format("http://localhost:%s/soap/%s", SoapMediaStorageServer.PORT, SoapMedia.NAME);

		QName QNAME = new QName(SoapMedia.NAMESPACE, SoapMedia.NAME);

		for (;;)
			try {
				Service service = Service.create(new URL(serverUrl + WSDL), QNAME);
				SoapMedia media = service.getPort(microgram.api.soap.SoapMedia.class);

				byte[] bytes = Files.readAllBytes(EARTH.toPath());
				String uri = media.upload(bytes);
				Log.info("Upload completed: " + uri);

				break;
			} catch (MicrogramException x) {
				Log.info("Upload failed, reason: " + x.getMessage());
			} catch (WebServiceException ws) {
				Log.info("IO error, reason: " + ws.getMessage());
				Thread.sleep(RETRY_PERIOD);
			}
	}

}
