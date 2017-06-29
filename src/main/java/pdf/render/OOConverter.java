package sg.astar.ihpc.oolib;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;

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
	public static byte[] startConvert(byte[] inputData) throws Exception {

		// Getting the given type to convert to
		String convertType = "writer_pdf_Export";

		XComponentContext context = createContext();
		System.out.println("connected to a running office ...");

		XComponentLoader loader = createComponentLoader(context);
		System.out.println("loader created ...");

		XSeekableInputStream xInput = SequenceInputStream.createStreamFromSequence(context, inputData);
		
		Object doc = loadDocument(loader, xInput); // Use Object type, it will auto detect input document type.
		System.out.println("document loaded ...");

		ByteArrayOutputStream byteOutStream = convertDocument(doc, convertType);
		System.out.println("document converted ...");

		byte[] buffer = byteOutStream.toByteArray();
		byteOutStream.flush();
		
		closeDocument(doc);
		System.out.println("document closed ...");
		
		return buffer;
	}

	// get the remote office component context
	static XComponentContext createContext() throws Exception {
		// get the remote office component context
		return Bootstrap.bootstrap();
	}

	static XComponentLoader createComponentLoader(XComponentContext context) throws com.sun.star.uno.Exception {
		// get the remote office service manager
		XMultiComponentFactory mcf = context.getServiceManager();
		Object desktop = mcf.createInstanceWithContext("com.sun.star.frame.Desktop", context);
		return UnoRuntime.queryInterface(XComponentLoader.class, desktop);
	}

	static XComponent loadDocument(XComponentLoader loader, XSeekableInputStream sinput) throws com.sun.star.io.IOException, IllegalArgumentException, MalformedURLException {
		// Preparing properties for loading the document
		PropertyValue propertyValue1 = new PropertyValue();
		propertyValue1.Name = "Hidden";
		propertyValue1.Value = new Boolean(true);
		PropertyValue propertyValue2 = new PropertyValue();
		propertyValue2.Name = "InputStream";
		propertyValue2.Value = sinput;
		
		return loader.loadComponentFromURL("private:stream", "_blank", 0, new PropertyValue[] { propertyValue1, propertyValue2 });
	}

	static ByteArrayOutputStream convertDocument(Object doc, String convertType) throws com.sun.star.io.IOException, MalformedURLException {
		ByteArrayOutputStream outputStream = null;
		
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
        	storable.storeToURL("private:stream", new PropertyValue[] {overwriteValue, filterValue, outputValue});
        } catch (IOException ex) { 
            ex.printStackTrace();
        } 
        try {
        	outputStream = new ByteArrayOutputStream();
            outputStream.write(outStream.toByteArray()); 
        } catch (java.io.IOException ex) { 
            ex.printStackTrace();
        }
        
        return outputStream;
	}

	static void closeDocument(Object doc) throws SQLException {
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
