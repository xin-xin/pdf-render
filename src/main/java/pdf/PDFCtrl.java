package pdf;

import javax.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pdf.render.OOConverter;

@RestController
public class PDFCtrl {

	@RequestMapping(value = "/pdf/rendering", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public byte[] uploadEmailTemplate(@RequestParam("file") MultipartFile file, @RequestParam("doc_id") String docId, HttpSession session) {
		byte[] empty = {};

		try {
			return OOConverter.convert(file.getBytes());
		} catch (Exception e) {
			return empty;
		}
	}
}
