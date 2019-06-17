package dropbox;

public class DeleteFile {

	public static void main(String[] args) throws Exception {
		
		DropboxClient.createClientWithAccessToken().delete("/teste2.txt");
	}

	
}
