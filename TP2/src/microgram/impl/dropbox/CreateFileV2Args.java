package microgram.impl.dropbox;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public class CreateFileV2Args {
	final String path;
	final String mode;
	final boolean autorename;
	final boolean mute;

	public CreateFileV2Args(String path) {
		this.path = path;
		this.mode = "add";
		this.autorename = false;
		this.mute = false;
	}
	
}
