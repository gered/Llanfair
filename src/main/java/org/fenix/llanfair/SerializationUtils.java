package org.fenix.llanfair;

import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.fenix.utils.config.Configuration;

import javax.swing.*;
import java.io.*;
import java.util.Base64;

public class SerializationUtils {
	/**
	 * Adds custom serialization settings, such as aliases and converters, to the given
	 * XStream serialization object.
	 * @param xml the XStream serialization object to add custom settings to
	 */
	public static void customize(XStream xml) {
		xml.alias("Run", Run.class);
		xml.alias("Config", Configuration.class);
		xml.alias("Segment", Segment.class);

		xml.registerConverter(new ImageIconConverter());
	}

	@SuppressWarnings("unchecked")
	public static <T> T base64ToObject(String base64) throws IOException, ClassNotFoundException {
		byte[] data = Base64.getDecoder().decode(base64);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		T decoded = (T)ois.readObject();
		ois.close();
		return decoded;
	}

	public static String objectToBase64(Object obj) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		oos.close();
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}

	public static class ImageIconConverter implements Converter {
		@Override
		public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
			ImageIcon icon = (ImageIcon)source;
			writer.startNode("ImageIcon");
			try {
				writer.setValue(objectToBase64(icon));
			} catch (IOException e) {
				throw new SerializationException(e);
			}
			writer.endNode();
		}

		@Override
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			reader.moveDown();
			ImageIcon icon;
			try {
				icon = base64ToObject(reader.getValue());
			} catch (Exception e) {
				throw new SerializationException(e);
			}
			reader.moveUp();
			return icon;
		}

		@Override
		public boolean canConvert(Class type) {
			return type.equals(ImageIcon.class);
		}
	}
}
