package pdf;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PDFServer implements ApplicationRunner {
	private Logger log = Logger.getLogger(this.getClass());

	public void run(ApplicationArguments args) {
		List<String> params = args.getOptionValues("partner");
		log.debug("Server starting..." + params);
	}

	public static void main(String[] args) {
		SpringApplication.run(PDFServer.class, args);
	}
}
