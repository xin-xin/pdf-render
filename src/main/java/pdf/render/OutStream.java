package sg.astar.ihpc.oolib;
import com.sun.star.io.BufferSizeExceededException;
import com.sun.star.io.IOException;
import com.sun.star.io.NotConnectedException;
import com.sun.star.io.XOutputStream;

import java.io.ByteArrayOutputStream;

/**
 * @author chenyi
 *
 */
public class OutStream extends ByteArrayOutputStream implements XOutputStream {

	public OutStream() {
		super(32768);
	}

	public void writeBytes(byte[] bytes) throws NotConnectedException, BufferSizeExceededException, IOException {
		try {
			this.write(bytes);
		} catch (java.io.IOException e) {
			throw (new com.sun.star.io.IOException(e.getMessage()));
		}
	}

	public void closeOutput() throws NotConnectedException, BufferSizeExceededException, IOException {
		try {
			super.flush();
			super.close();
		} catch (java.io.IOException e) {
			throw (new com.sun.star.io.IOException(e.getMessage()));
		}
	}

	@Override
	public void flush() {
		try {
			super.flush();
		} catch (java.io.IOException e) {
		}
	}

}