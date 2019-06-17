package dropbox;

public class CreateFile {

	public static void main(String[] args) throws Exception {
		
		String msg = "primeiro ficheiro do Rafa e da Ana";
		
		DropboxClient.createClientWithAccessToken().upload("/teste2.txt", msg.getBytes());
	}

	
}
