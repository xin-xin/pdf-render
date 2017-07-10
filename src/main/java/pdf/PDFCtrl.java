package pdf;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pdf.render.OOConverter;

@RestController
public class PDFCtrl {
	private Logger log = Logger.getLogger(this.getClass());

	@RequestMapping(value = "/pdf/rendering", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public byte[] convertPDF(@RequestParam("file") MultipartFile file, @RequestParam("doc_id") String docId, HttpServletResponse response) {
		byte[] result = {};

		try {
			result = OOConverter.convert(file.getBytes());
			log.debug("Convert file size: " + result.length);
			
			response.setContentType("application/octet_stream");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + docId + ".pdf\"");

			return result;
		} catch (Exception e) {
			return result;
		}
	}
}
