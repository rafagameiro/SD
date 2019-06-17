package impl.clt.soap;

import java.net.URI;

import javax.xml.namespace.QName;

import microgram.api.java.Media;
import microgram.api.java.Result;
import microgram.api.soap.MicrogramException;
import microgram.api.soap.SoapMedia;

public class SoapMediaClient extends SoapClient implements Media {
	static final QName QNAME = new QName(SoapMedia.NAMESPACE, SoapMedia.NAME);

	final SoapMedia proxy;

	public SoapMediaClient(URI uri) {
		super(uri, QNAME);
		this.proxy = service.getPort(microgram.api.soap.SoapMedia.class);
	}

	@Override
	public Result<String> upload(byte[] bytes) {
		try {
			return Result.ok(proxy.upload(bytes));
		} catch (MicrogramException x) {
			return Result.error(super.errorCode(x));
		}
	}

	@Override
	public Result<byte[]> download(String id) {
		try {
			return Result.ok(proxy.download(id));
		} catch (MicrogramException x) {
			return Result.error(super.errorCode(x));
		}
	}

}
