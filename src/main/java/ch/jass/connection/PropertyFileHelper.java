package ch.jass.connection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class PropertyFileHelper {

	private static final String JASS_SERVER_PROPERTY_FILE_NAME = "jass_server.properties";

	public static URI getJassServerEndpointUri() {

		Properties prop = loadProperties();

		try {
			final URI endpointURI = new URI(prop.getProperty("jass.server.url"));
			return endpointURI;
		} catch (URISyntaxException ex) {
			System.err.println("URISyntaxException exception: " + ex.getMessage());
		}

		return null;
	}

	private static Properties loadProperties() {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			ClassLoader loader = PropertyFileHelper.class.getClassLoader();
			input = loader.getResourceAsStream(JASS_SERVER_PROPERTY_FILE_NAME);
			// load a properties file
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return prop;
	}
}
