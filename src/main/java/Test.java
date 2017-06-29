import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import pdf.render.OOConverter;

/**
 * @author chenyi
 * Test the Open Office Library (build as genPDF-0.1.jar)
 * How to run:
 * e.g.
 * 	java -cp .:genPDF-0.1.jar:/usr/lib64/libreoffice/program/classes/* Test "STEMS_Schedule.pptx" "./output.pdf"
 */
public class Test {

	public static void main(String[] args) throws Exception {
		// Prepare testing inputs
		String inputFilePath = args[0];
		String outputFilePath = args[1];
		Path fileLocation = Paths.get(inputFilePath);
		byte[] input = Files.readAllBytes(fileLocation);
		
		// Call the convert method
		byte[] result = OOConverter.startConvert(input);
		
		// output testing result
		FileOutputStream fos = new FileOutputStream(outputFilePath);
		fos.write(result);
		fos.close();
		
		System.exit(0);
	}
}
