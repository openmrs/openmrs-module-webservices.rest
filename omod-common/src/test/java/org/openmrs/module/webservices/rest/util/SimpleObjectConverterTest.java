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
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.core.TreeMarshaller;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.core.DefaultConverterLookup;
import com.thoughtworks.xstream.converters.collections.CharArrayConverter;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class SimpleObjectConverterTest {
	
	HierarchicalStreamWriter writer;
	
	MarshallingContext context;
	
	SimpleObjectConverter con;
	
	List l;
	
	@Before
	public void setup() {
		con = new SimpleObjectConverter((Mapper) null);
		writer = new PrettyPrintWriter(new StringWriter());
		DefaultConverterLookup conlook = new DefaultConverterLookup();
		conlook.registerConverter(new CharArrayConverter(), 1);
		context = new TreeMarshaller(writer, conlook, (Mapper) null);
	}
	
	//Map, writer, context
	@Test
	public void BaseChoiceTest() {
		Map m = new HashMap();
		
		con.marshal(m, writer, context);
		
	}
	
	//Map, writer, null
	@Test
	public void test1() {
		Map m = new HashMap();
		
		con.marshal(m, writer, null);
	}
	
	//Map, null, context
	@Test
	public void test2() {
		Map m = new HashMap();
		
		con.marshal(m, null, context);
	}
	
	//List, writer, context
	@Test
	public void test3() {
		List l = new LinkedList();
		
		con.marshal(l, writer, context);
	}
	
	//null, writer, context
	@Test
	public void test4() {
		con.marshal(null, writer, context);
	}
	
	//Integer, writer, context
	@Test
	public void test5() {
		char[] i = { 'a' };
		
		con.marshal(i, writer, context);
	}
	
}
