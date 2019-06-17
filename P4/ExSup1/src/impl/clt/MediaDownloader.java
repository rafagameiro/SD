package impl.clt;

import java.net.URI;
import java.util.logging.Logger;

import discovery.Discovery;
import microgram.api.java.Media;
import microgram.api.java.Result;

public class MediaDownloader {
	private static Logger Log = Logger.getLogger(MediaDownloader.class.getName());

	public static final String SERVICE = "Microgram-MediaStorage";

	public static void main(String[] args) throws Exception {

		URI[] mediaURIs = Discovery.findUrisOf(SERVICE, 1);
		if (mediaURIs.length > 0) {
			Media media = MediaClientFactory.getMediaClient(mediaURIs[0]);

			String url = "http://localhost:9999/rest/media/84486F586FA514F31F07057F39B68C673B7A091F";
			Result<byte[]> uri = media.download(url);
			if (uri.isOK())
				Log.info("Download completed: " + uri);
			else
				Log.info("Download failed, reason: " + uri.error());
		}
	}
}
