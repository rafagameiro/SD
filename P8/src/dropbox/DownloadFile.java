package dropbox;

public class DownloadFile {

	public static void main(String[] args) throws Exception {
		
		DropboxClient.createClientWithAccessToken().download("/teste.txt");
	}

	
}
