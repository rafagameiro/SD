/**
 * 
 */
package microgram.api;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public class Pair {
	
	String key;
	String value;
	
	public Pair() {
	}
	
	@BsonCreator
	public Pair(@BsonProperty("key") String key, @BsonProperty("value") String value) {
		this.key = key;
		this.value = value;
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getKey() {
		return this.key;
	}
	
	public String getValue() {
		return this.value;
	}
	
	

}
