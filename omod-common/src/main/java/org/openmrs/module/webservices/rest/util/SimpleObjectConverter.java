package org.openmrs.module.webservices.rest.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.Hyperlink;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Custom XStream converter to serialize XML in REST services
 *
 */
public class SimpleObjectConverter implements Converter {

    public boolean canConvert(Class clazz) {
        return SimpleObject.class.isAssignableFrom(clazz);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {

		if (value instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) value;
			for (Object obj : map.entrySet()) {
				Entry<?, ?> entry = (Entry<?, ?>) obj;
				writer.startNode(entry.getKey().toString());
				marshal(entry.getValue(), writer, context);
				writer.endNode();
			}
		} else if (value instanceof List) {
			List<?> list = (List<?>) value;
			for (Object obj : list) {
				marshal(obj, writer, context);
			}
		} else if (value instanceof Hyperlink) {
			Hyperlink hl = (Hyperlink)value;
			writer.startNode("link");
			writer.startNode("rel");
			writer.setValue(hl.getRel());
			writer.endNode();
			writer.startNode("uri");
			writer.setValue(hl.getUri());
			writer.endNode();
			writer.endNode();
		} else {
			writer.setValue(value.toString());
		}

    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return null;
    }

}
