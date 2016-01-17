package org.fenix.llanfair;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.fenix.llanfair.config.Accuracy;
import org.fenix.llanfair.config.Compare;
import org.fenix.llanfair.config.Merge;
import org.fenix.utils.config.Configuration;

import javax.swing.*;
import java.awt.*;
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

		xml.alias("CompareMethod", Compare.class);
		xml.alias("TimeAccuracy", Accuracy.class);
		xml.alias("Merge", Merge.class);

		xml.registerConverter(new FontConverter());
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
				throw new RuntimeException(e);
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
				throw new RuntimeException(e);
			}
			reader.moveUp();
			return icon;
		}

		@Override
		public boolean canConvert(Class type) {
			return type.equals(ImageIcon.class);
		}
	}

	public static class FontConverter implements Converter {
		@Override
		public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
			Font font = (Font)source;
			writer.startNode("Font");
			writer.startNode("family");
			writer.setValue(font.getFamily());
			writer.endNode();
			writer.startNode("size");
			writer.setValue("" + font.getSize());
			writer.endNode();
			writer.startNode("style");
			writer.setValue("" + font.getStyle());
			writer.endNode();
			writer.endNode();
		}

		@Override
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			String family = null;
			int size = 0;
			int style = 0;
			reader.moveDown();
			while (reader.hasMoreChildren()) {
				reader.moveDown();
				if (reader.getNodeName().equals("family"))
					family = reader.getValue();
				else if (reader.getNodeName().equals("size"))
					size = Integer.parseInt(reader.getValue());
				else if (reader.getNodeName().equals("style"))
					style = Integer.parseInt(reader.getValue());
				reader.moveUp();
			}
			reader.moveUp();

			return new Font(family, style, size);
		}

		@Override
		public boolean canConvert(Class type) {
			return type.equals(Font.class);
		}
	}
}
