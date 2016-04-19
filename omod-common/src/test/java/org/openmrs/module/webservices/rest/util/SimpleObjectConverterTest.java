package org.openmrs.module.webservices.rest.util;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.*;
import java.io.*;
import java.io.StringWriter;

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.Hyperlink;
import org.openmrs.module.webservices.rest.web.RestInit;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.core.TreeMarshaller;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.core.DefaultConverterLookup;
import com.thoughtworks.xstream.converters.basic.StringConverter;
import com.thoughtworks.xstream.converters.SingleValueConverterWrapper;
import com.thoughtworks.xstream.converters.Converter;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

class HyperlinkConverter implements Converter {
	
	public boolean canConvert(Class clazz) {
		return Hyperlink.class.isAssignableFrom(clazz);
	}
	
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		Hyperlink link = (Hyperlink) value;
		
		writer.startNode("Link");
		
		writer.startNode("rel");
		writer.setValue(link.getRel());
		writer.endNode();
		
		writer.startNode("uri");
		writer.setValue(link.getUri());
		writer.endNode();
		
		writer.endNode();
	}
	
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		return null;
	}
}

public class SimpleObjectConverterTest {
	
	HierarchicalStreamWriter writer;
	
	StringWriter swriter;
	
	MarshallingContext context;
	
	SimpleObjectConverter con;
	
	DefaultConverterLookup conlook;
	
	List l;
	
	//@TODO General cleanup before it's ready for a pull request
	@Before
	public void setup() {
		con = new SimpleObjectConverter((Mapper) null);
		swriter = new StringWriter();
		writer = new PrettyPrintWriter(swriter);
		
		conlook = new DefaultConverterLookup();
		conlook.registerConverter(new SingleValueConverterWrapper(new StringConverter()), 1);
		conlook.registerConverter(new HyperlinkConverter(), 1);
		conlook.registerConverter(new CollectionConverter((Mapper) null), 1);
		context = new TreeMarshaller(writer, conlook, (Mapper) null);
	}
	
	@Test
	public void HashMapTest() {
		Map m = new HashMap();
		m.put("key", "value");
		
		con.marshal(m, writer, context);
		String convertedResult = swriter.toString();
		assertTrue(convertedResult.contains("<key>value"));
		
	}
	
	@Test
	public void ListTest() {
		List l = new ArrayList();
		SimpleObject simpl = new SimpleObject();
		simpl.add("key", "value");
		l.add(simpl);
		
		con.marshal(l, writer, context);
		String convertedResult = swriter.toString();
		assertTrue(convertedResult.contains("<key>value"));
		
	}
	
	@Test
	public void SimpleObjectWithString() {
		SimpleObject simpl = new SimpleObject();
		List l = new ArrayList();
		l.add("hello");
		simpl.add("links", l);
		
		con.marshal(l, writer, context);
		
	}
	
	@Test
	public void ListTestWithCustomSubresource() {
		ArrayList l = new ArrayList();
		SimpleObject simpl = new SimpleObject();
		
		List links = new ArrayList();
		Hyperlink hlink = new Hyperlink("self", "uri");
		hlink.setResourceAlias("uri");
		links.add(hlink);
		
		simpl.add("key", "value");
		simpl.add("links", links);
		
		l.add(simpl);
		
		con.marshal(l, writer, context);
		String convertedResult = swriter.toString();
		System.out.println(convertedResult);//.contains("<key>value"));
		assertTrue(convertedResult.contains("<uri>"));
	}
	
}
