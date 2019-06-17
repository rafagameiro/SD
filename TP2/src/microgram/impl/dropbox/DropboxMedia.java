package microgram.impl.dropbox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.pac4j.scribe.builder.api.DropboxApi20;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import microgram.api.java.Media;
import microgram.api.java.Result;
import utils.Hash;
import utils.JSON;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public class DropboxMedia implements Media {

	private static final String apiKey = "jldv945b6bej8gv";
	private static final String apiSecret = "uo47zkse5qfyrh7";
	private static final String accessTokenStr = "WmV0QM_iQOAAAAAAAAAAMlA3c42aJZmqoMBtxUbDLirc88sNUsbE4pZreEI2wvyj";

	protected static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";
	protected static final String OCTETSTREAM_CONTENT_TYPE = "application/octet-stream";

	private static final String CREATE_FILE_V2_URL = "https://content.dropboxapi.com/2/files/upload";
	private static final String DELETE_FILE_V2_URL = "https://api.dropboxapi.com/2/files/delete";
	private static final String DOWNLOAD_FILE_V2_URL = "https://content.dropboxapi.com/2/files/download";

	private static final String DROPBOX_API_ARG = "Dropbox-API-Arg";
	private static final String MEDIA_EXTENSION = ".jpg";
	private static final String ROOT_DIR = "/sd2019-tp2/";

	protected OAuth20Service service;
	protected OAuth2AccessToken accessToken;

	/**
	 * Creates a dropbox client, given the access token.
	 * 
	 * @param accessTokenStr String with the previously obtained access token.
	 * @throws Exception Throws exception if something failed.
	 */
	public static DropboxMedia createClientWithAccessToken() throws Exception {
		try {
			OAuth20Service service = new ServiceBuilder(apiKey).apiSecret(apiSecret).build(DropboxApi20.INSTANCE);
			OAuth2AccessToken accessToken = new OAuth2AccessToken(accessTokenStr);
			
			return new DropboxMedia(service, accessToken);
		} catch (Exception x) {
			x.printStackTrace();
			throw new Exception(x);
		}
	}

	/**
	 * Creates a dropbox client, given the access token.
	 * 
	 * @param accessTokenStr String with the previously obtained access token.
	 * @throws Exception Throws exception if something failed.
	 */
	public static DropboxMedia createClientWithAccessToken(String accessTokenStr) throws Exception {
		try {
			OAuth20Service service = new ServiceBuilder(apiKey).apiSecret(apiSecret).build(DropboxApi20.INSTANCE);
			OAuth2AccessToken accessToken = new OAuth2AccessToken(accessTokenStr);

			return new DropboxMedia(service, accessToken);
		} catch (Exception x) {
			x.printStackTrace();
			throw new Exception(x);
		}
	}

	/**
	 * Creates a dropbox client, given a file containing an access token.
	 * 
	 * @param accessTokenFile File containing the previously obtained access token.
	 * @throws Exception Throws exception if something failed.
	 */
	public static DropboxMedia createClientWithAccessTokenFile(File accessTokenFile) throws Exception {
		try {
			String accessTokenStr = new String(Files.readAllBytes(accessTokenFile.toPath()), StandardCharsets.UTF_8);
			return createClientWithAccessToken(accessTokenStr);
		} catch (Exception x) {
			x.printStackTrace();
			throw new Exception(x);
		}
	}

	protected DropboxMedia(OAuth20Service service, OAuth2AccessToken accessToken) {
		this.service = service;
		this.accessToken = accessToken;
	}

	@Override
	public Result<String> upload(byte[] bytes) {
		try {
			String id = Hash.of(bytes);
			String filename = new String(ROOT_DIR + id + MEDIA_EXTENSION);

			OAuthRequest createFile = new OAuthRequest(Verb.POST, CREATE_FILE_V2_URL);
			createFile.addHeader("Content-Type", OCTETSTREAM_CONTENT_TYPE);
			createFile.addHeader(DROPBOX_API_ARG, JSON.encode(new CreateFileV2Args(filename)));

			createFile.setPayload(bytes);

			service.signRequest(accessToken, createFile);
			Response r = service.execute(createFile);

			if (r.getCode() == 409) {
				System.err.println("Dropbox file already exists");
				return Result.error(Result.ErrorCode.CONFLICT);
			} else if (r.getCode() == 200) {
				System.err.println("Dropbox file was created with success");
				return Result.ok(id);
			} else {
				System.err.println("Unexpected error HTTP: " + r.getCode());
				return Result.error(Result.ErrorCode.INTERNAL_ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(Result.ErrorCode.INTERNAL_ERROR);
		}
	}

	@Override
	public Result<byte[]> download(String id) {
		try {
			String filename = new String(ROOT_DIR + id + MEDIA_EXTENSION);

			OAuthRequest downloadFile = new OAuthRequest(Verb.POST, DOWNLOAD_FILE_V2_URL);
			downloadFile.addHeader("Content-Type", OCTETSTREAM_CONTENT_TYPE);
			downloadFile.addHeader(DROPBOX_API_ARG, JSON.encode(new AccessFileV2Args(filename)));

			service.signRequest(accessToken, downloadFile);
			Response r = service.execute(downloadFile);
				
			if (r.getCode() == 200) {

				InputStream in = r.getStream();
				ByteArrayOutputStream os = new ByteArrayOutputStream();

				byte[] buffer = new byte[1024];
				int len;

				while ((len = in.read(buffer)) != -1) {
					os.write(buffer, 0, len);
					buffer = new byte[1024];
				}

				System.err.println("Dropbox file was downloaded with success");
				return Result.ok(os.toByteArray());
			} else {
				System.err.println("Dropbox file does not exists");
				return Result.error(Result.ErrorCode.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(Result.ErrorCode.INTERNAL_ERROR);
		}
	}

	@Override
	public Result<Void> delete(String id) {
		try {
			String filename = new String(ROOT_DIR + id + MEDIA_EXTENSION);

			OAuthRequest deleteFile = new OAuthRequest(Verb.POST, DELETE_FILE_V2_URL);
			deleteFile.addHeader("Content-Type", JSON_CONTENT_TYPE);

			deleteFile.setPayload(JSON.encode(new AccessFileV2Args(filename)));

			service.signRequest(accessToken, deleteFile);
			Response r = service.execute(deleteFile);

			if (r.getCode() == 200) {
				System.err.println("Dropbox file was deleted with success");
				return Result.ok();
			} else {
				System.err.println("Dropbox file does not exists");
				return Result.error(Result.ErrorCode.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(Result.ErrorCode.INTERNAL_ERROR);
		}
	}

}
