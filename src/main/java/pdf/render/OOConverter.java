package pdf.render;

import java.net.MalformedURLException;

import org.apache.log4j.Logger;

import com.sun.star.beans.PropertyValue;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.io.IOException;
import com.sun.star.io.SequenceInputStream;
import com.sun.star.io.XSeekableInputStream;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.sdbc.SQLException;
import com.sun.star.sdbc.XCloseable;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

/**
 * @author chenyi
 *
 */
public class OOConverter {
	private static Logger log = Logger.getLogger(OOConverter.class);

	// get the remote office component context
	private static XComponentContext createContext() throws Exception {
		// get the remote office component context
		return Bootstrap.bootstrap();
	}

	private static XComponentLoader createComponentLoader(XComponentContext context) throws com.sun.star.uno.Exception {
		// get the remote office service manager
		XMultiComponentFactory mcf = context.getServiceManager();
		Object desktop = mcf.createInstanceWithContext("com.sun.star.frame.Desktop", context);
		return UnoRuntime.queryInterface(XComponentLoader.class, desktop);
	}

	private static XComponent loadDocument(XComponentLoader loader, XSeekableInputStream sinput) throws com.sun.star.io.IOException, IllegalArgumentException, MalformedURLException {
		// Preparing properties for loading the document
		PropertyValue propertyValue1 = new PropertyValue();
		propertyValue1.Name = "Hidden";
		propertyValue1.Value = new Boolean(true);
		PropertyValue propertyValue2 = new PropertyValue();
		propertyValue2.Name = "InputStream";
		propertyValue2.Value = sinput;

		return loader.loadComponentFromURL("private:stream", "_blank", 0, new PropertyValue[] { propertyValue1, propertyValue2 });
	}

	public static byte[] convert(byte[] inputData) throws Exception {
		// Getting the given type to convert to
		String convertType = "writer_pdf_Export";

		log.debug("connecting to a running office ...");
		XComponentContext context = createContext();
		log.debug("connected.");

		XComponentLoader loader = createComponentLoader(context);
		log.debug("loader created ...");

		XSeekableInputStream xInput = SequenceInputStream.createStreamFromSequence(context, inputData);
		Object doc = loadDocument(loader, xInput); // Use Object type, it will
													// auto detect input
													// document type.
		log.debug("document loaded ...");
		byte[] buffer = convertDocument(doc, convertType);
		log.debug("document converted ...");
		closeDocument(doc);
		log.debug("document closed ...");

		return buffer;
	}

	private static byte[] convertDocument(Object doc, String convertType) throws com.sun.star.io.IOException, MalformedURLException {
		byte[] result = {};
		// Preparing properties for converting the document
		// Setting the flag for overwriting
		PropertyValue overwriteValue = new PropertyValue();
		overwriteValue.Name = "Overwrite";
		overwriteValue.Value = new Boolean(true);
		// Setting the filter name
		PropertyValue filterValue = new PropertyValue();
		filterValue.Name = "FilterName";
		filterValue.Value = convertType;
		// attach output stream
		OutStream outStream = new OutStream();
		PropertyValue outputValue = new PropertyValue();
		outputValue.Name = "OutputStream";
		outputValue.Value = outStream;

		XStorable storable = (XStorable) UnoRuntime.queryInterface(XStorable.class, doc);
		// Storing and converting the document
		try {
			storable.storeToURL("private:stream", new PropertyValue[] { overwriteValue, filterValue, outputValue });
			result = outStream.toByteArray();
		} catch (IOException ex) {
			log.error("Convert document failed.", ex);
		} finally {
			try {
				outStream.close();
			} catch (Exception e) {
				log.error("Close output stream error.", e);
			}
		}

		return result;
	}

	private static void closeDocument(Object doc) throws SQLException {
		// Closing the converted document. Use XCloseable.clsoe if the
		// interface is supported, otherwise use XComponent.dispose
		XCloseable closeable = (XCloseable) UnoRuntime.queryInterface(XCloseable.class, doc);
		if (closeable != null) {
			closeable.close();
		} else {
			XComponent component = (XComponent) UnoRuntime.queryInterface(XComponent.class, doc);
			component.dispose();
		}
	}
}
