package discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public class Discovery {

	private static Logger Log = Logger.getLogger(Discovery.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s\n");
	}

	static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("226.226.226.226", 2266);
	static final int DISCOVERY_PERIOD = 1000;
	static final int DISCOVERY_TIMEOUT = 30000;
	static final int MAX_DATAGRAM_SIZE = 65536;

	private static final String DELIMITER = "\t";

	/**
	 * 
	 * Announces periodically a service in a separate thread .
	 * 
	 * @param serviceName the name of the service being announced.
	 * @param serviceURI  the location of the service
	 */
	public static void announce(String serviceName, String serviceURI) {
		Log.info(String.format("Starting Discovery announcements on: %s for: %s -> %s", DISCOVERY_ADDR, serviceName,
				serviceURI));

		byte[] pktBytes = String.format("%s%s%s", serviceName, DELIMITER, serviceURI).getBytes();

		DatagramPacket pkt = new DatagramPacket(pktBytes, pktBytes.length, DISCOVERY_ADDR);
		new Thread(() -> {
			try (DatagramSocket ms = new DatagramSocket()) {
				for (;;) {
					ms.send(pkt);
					Thread.sleep(DISCOVERY_PERIOD);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * Performs discovery of instances of the service with the given name.
	 * 
	 * @param serviceName      the name of the service being discovered
	 * @param minRepliesNeeded the required number of service replicas to find.
	 * @return an array of URI with the service instances discovered. Returns an
	 *         empty, 0-length, array if the service is not found within the alloted
	 *         time.
	 * @throws IOException
	 * @throws URISyntaxException
	 * 
	 */
	public static URI[] findUrisOf(String serviceName, int minRepliesNeeded) throws IOException, URISyntaxException {
		URI[] servicesDisc = new URI[minRepliesNeeded];
		int replicasFound = 0;
		int currentTimeout = 500;

		try (MulticastSocket socket = new MulticastSocket(DISCOVERY_ADDR)) {
			socket.joinGroup(DISCOVERY_ADDR.getAddress());
			socket.setSoTimeout(currentTimeout);
			byte[] buffer = new byte[MAX_DATAGRAM_SIZE];
			while (replicasFound < minRepliesNeeded && currentTimeout < DISCOVERY_TIMEOUT) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				try {
					socket.receive(request);
				} catch (SocketTimeoutException e) {
					currentTimeout += 500;
				}

				String requestData = new String(request.getData(), 0, request.getLength());
				String[] content = requestData.split(DELIMITER);

				if (content[0].equalsIgnoreCase(serviceName)) {
					URI u = new URI(content[1]);
					servicesDisc[replicasFound++] = u;
				}
				buffer = new byte[MAX_DATAGRAM_SIZE];
			}
		}
		return servicesDisc;
	}
}
