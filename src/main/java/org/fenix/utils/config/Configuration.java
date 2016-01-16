//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.fenix.utils.config;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.fenix.llanfair.SerializationUtils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Configuration implements Serializable {
	private static final long serialVersionUID = 1001L;
	private transient File path;
	private Map<String, Map<String, Object>> values;
	private transient PropertyChangeSupport pcSupport;

	public Configuration() {
		this.values = new HashMap<>();
		this.pcSupport = new PropertyChangeSupport(this);
	}

	private Configuration(File path) {
		this();
		this.setPath(path);
	}

	public static Configuration newInstance(File path) {
		return path.exists()?deserialize(path):new Configuration(path);
	}

	public boolean isEmpty() {
		return this.values.isEmpty();
	}

	public File getPath() {
		return this.path;
	}

	public final void setPath(File path) {
		if(path == null) {
			throw new NullPointerException("Null path");
		} else if(path.isDirectory()) {
			throw new IllegalArgumentException("Path is a directory");
		} else {
			File parent = path.getAbsoluteFile().getParentFile();
			if(parent.canRead() && parent.canWrite()) {
				this.path = path;
			} else {
				throw new IllegalArgumentException("Denied: " + parent);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String section, String key) {
		if(!this.values.containsKey(section)) {
			return null;
		} else {
			if(section == null) {
				section = "";
			}

			Map<String, Object> subMap = this.values.get(section);
			return !subMap.containsKey(key)?null:(T)subMap.get(key);
		}
	}

	public <T> T get(String key) {
		if(key != null && key.contains(".")) {
			String[] split = key.split("\\.");
			return this.get(split[0], split[1]);
		} else {
			return this.get("", key);
		}
	}

	public void put(String section, String key, Object value) {
		if(key != null && !key.equals("")) {
			if(section == null) {
				section = "";
			}

			Map<String, Object> subMap = this.values.get(section);
			if(subMap == null) {
				subMap = new HashMap<>();
				this.values.put(section, subMap);
			}

			Object old = subMap.get(key);
			subMap.put(key, value);
			String propertyName;
			if (section.length() == 0)
				propertyName = key;
			else
				propertyName = section + "." + key;
			this.pcSupport.firePropertyChange(propertyName, old, value);
		} else {
			throw new NullPointerException("Null key");
		}
	}

	public void put(String key, Object value) {
		if(key != null && key.contains(".")) {
			String[] split = key.split("\\.");
			this.put(split[0], split[1], value);
		} else {
			this.put("", key, value);
		}

	}

	public void remove(String section, String key) {
		if(section == null) {
			section = "";
		}

		if(this.values.containsKey(section)) {
			((Map)this.values.get(section)).remove(key);
		}

	}

	public void remove(String key) {
		this.remove("", key);
	}

	public boolean contains(String section, String key) {
		if(section == null) {
			section = "";
		}

		return !this.values.containsKey(section)?false:this.values.get(section).containsKey(key);
	}

	public boolean contains(String key) {
		if(key != null && key.contains(".")) {
			String[] split = key.split("\\.");
			return this.contains(split[0], split[1]);
		} else {
			return this.contains("", key);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if(listener == null) {
			throw new NullPointerException("Null listener");
		} else {
			this.pcSupport.addPropertyChangeListener(listener);
		}
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		this.pcSupport.removePropertyChangeListener(pcl);
	}

	public void serialize() {
		XStream xstream = new XStream(new DomDriver());
		SerializationUtils.customize(xstream);
		String xmlOutput = xstream.toXML(this);
		FileWriter stream = null;

		try {
			stream = new FileWriter(this.path);
			stream.write(xmlOutput);
		} catch (Exception var12) {
			throw new IllegalStateException("I/O Error for: " + this.path);
		} finally {
			try {
				stream.close();
			} catch (Exception var11) {
			}

		}

	}

	private static Configuration deserialize(File path) {
		XStream xstream = new XStream(new DomDriver());
		SerializationUtils.customize(xstream);
		Configuration input = (Configuration)xstream.fromXML(path);
		input.setPath(path);
		return input;
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.pcSupport = new PropertyChangeSupport(this);
	}
}
