/**
 * 
 */
package microgram.impl.mongo;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public class MongoResources {
	
	private static final String MONGO_HOSTNAME = "localhost";
	private static final String DB_NAME = "sd19";
	protected static final String INDEX_KEY = "key";
	protected static final String INDEX_VALUE = "value";
	
	MongoDatabase dbName;
	
	public MongoResources() {
		
		MongoClient mongo = new MongoClient(MONGO_HOSTNAME);

		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		dbName = mongo.getDatabase(DB_NAME).withCodecRegistry(pojoCodecRegistry);
	}

}
