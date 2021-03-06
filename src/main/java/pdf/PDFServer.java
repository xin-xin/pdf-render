package pdf;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PDFServer implements ApplicationRunner {
	private Logger log = Logger.getLogger(this.getClass());
	private String openOfficeLib = "/usr/lib64/libreoffice/program/classes/";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void run(ApplicationArguments args) {
		List<String> params = args.getOptionValues("openoffice");
		log.debug("Server starting..." + params);

		if (params != null && !params.isEmpty()) {
			log.debug("Got partner information.");
			try {
				Configuration config = new PropertiesConfiguration(params.get(0));
				openOfficeLib = config.getString("openoffice");
				log.debug(openOfficeLib);
			} catch (Exception ex) {
				log.error("Failed to read openoffice information, please verify configuration file format.", ex);
				System.exit(-1);
			}
		}

		try {
			File libs = new File(openOfficeLib);
			URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Class urlClass = URLClassLoader.class;
			Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);

			if (libs.isDirectory()) {
				for (File file : libs.listFiles()) {
					method.invoke(urlClassLoader, new Object[] { file.toURI().toURL() });
				}
			} else {
				method.invoke(urlClassLoader, new Object[] { libs.toURI().toURL() });
			}

			log.debug(Arrays.toString(urlClassLoader.getURLs()));
		} catch (Exception ex) {
			log.error("Failed to set openoffice information, please verify classpath.", ex);
			System.exit(-1);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(PDFServer.class, args);
	}
}
