package impl.clt.soap;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import impl.srv.soap.SoapMediaStorageServer;
import microgram.api.soap.MicrogramException;
import microgram.api.soap.SoapMedia;

public class DownloadMediaClient {
	// timeouts in ms
	static final int SOAP_CONN_TIMEOUT = 10000;
	static final int SOAP_RECV_TIMEOUT = 4500;
	private static final int RETRY_PERIOD = 1000;

	private static Logger Log = Logger.getLogger(DownloadMediaClient.class.getName());

	private static final String WSDL = "?wsdl";

	public static void main(String[] args) throws IOException, InterruptedException {

		String serverUrl = args.length > 0 ? args[0]
				: String.format("http://localhost:%s/soap/%s", SoapMediaStorageServer.PORT, SoapMedia.NAME);

		QName QNAME = new QName(SoapMedia.NAMESPACE, SoapMedia.NAME);

		for (;;)
			try {
				Service service = Service.create(new URL(serverUrl + WSDL), QNAME);
				SoapMedia media = service.getPort(microgram.api.soap.SoapMedia.class);

				String id = "84486F586FA514F31F07057F39B68C673B7A091F";
				byte[] bytes = media.download(id);
				Log.info("Download completed: " + bytes.length + " bytes...");

				break;
			} catch (MicrogramException x) {
				Log.info("Download failed, reason: " + x.getMessage());
			} catch (WebServiceException ws) {
				Log.info("IO error, reason: " + ws.getMessage());
				Thread.sleep(RETRY_PERIOD);
			}
	}

}
