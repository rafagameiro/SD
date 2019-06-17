package impl.clt.rest;

import java.net.URI;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import microgram.api.java.Media;
import microgram.api.java.Result;
import microgram.api.rest.RestMediaStorage;

public class RestMediaClient extends RestClient implements Media {

	public RestMediaClient(URI mediaStorage) {
		super(mediaStorage, RestMediaStorage.PATH);
	}

	public Result<String> upload(byte[] bytes) {
		Response r = super.target.request().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(bytes, MediaType.APPLICATION_OCTET_STREAM));

		if (r.getStatusInfo().equals(Status.OK))
			return Result.ok(r.readEntity(String.class));
		else
			return Result.error(super.errorCode(r.getStatus()));
	}

	public Result<byte[]> download(String url) {
		Response r = client.target(url).request().accept(MediaType.APPLICATION_OCTET_STREAM).get();
		
		if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
			return Result.ok(r.readEntity(byte[].class));
		else
			return Result.error(super.errorCode(r.getStatus()));
	}

}