package impl.srv.soap;

import impl.srv.shared.JavaMedia;
import microgram.api.java.Media;
import microgram.api.java.Result;
import microgram.api.rest.RestMediaStorage;
import microgram.api.soap.MicrogramException;
import microgram.api.soap.SoapMedia;

public class SoapMediaResources implements SoapMedia {

	final Media impl;
	final String baseUri;

	public SoapMediaResources(String baseUri) {
		this.baseUri = baseUri + RestMediaStorage.PATH;
		this.impl = new JavaMedia();
	}

	@Override
	public String upload(byte[] bytes) throws MicrogramException {
		Result<String> result = impl.upload(bytes);
		result.toString();
		if (result.isOK())
			return baseUri + "/" + result.value();
		else
			throw new MicrogramException(result.error().toString());
	}

	@Override
	public byte[] download(String id) throws MicrogramException {
		Result<byte[]> result = impl.download(id);
		if (result.isOK())
			return result.value();
		else
			throw new MicrogramException(result.error().toString());
	}
}
