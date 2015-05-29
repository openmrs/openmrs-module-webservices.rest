package org.openmrs.module.webservices.docs.swagger;

import java.io.IOException;
import java.util.TreeMap;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@SuppressWarnings("hiding")
public class Test2 {
	
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		Pair pair = new Pair(2, 3);
		TreeMap<Pair, String> map = new TreeMap<Test2.Pair, String>();
		map.put(pair, "Horsey");
		Thingie thingie = new Thingie();
		thingie.setMap(map);
		ObjectMapper mapper = new ObjectMapper();
		
		String str = mapper.writeValueAsString(thingie);
		System.out.println(str);
		Thingie result = mapper.readValue(str, Thingie.class);
		System.out.println(result.getMap());
	}
	
	public static class Thingie {
		
		public TreeMap<Pair, String> getMap() {
			return map;
		}
		
		public void setMap(TreeMap<Pair, String> map) {
			this.map = map;
		}
		
		@JsonSerialize(keyUsing = PairKS.class)
		private TreeMap<Pair, String> map;
	}
	
	public static class Pair {
		
		public Pair() {
		}
		
		public Pair(int arg1, int arg2) {
			this.arg1 = arg1;
			this.arg2 = arg2;
		}
		
		public int getArg1() {
			return arg1;
		}
		
		public void setArg1(int arg1) {
			this.arg1 = arg1;
		}
		
		public int getArg2() {
			return arg2;
		}
		
		public void setArg2(int arg2) {
			this.arg2 = arg2;
		}
		
		@Override
		public String toString() {
			return "Pair(" + arg1 + ", " + arg2 + ")";
		}
		
		private int arg1;
		
		private int arg2;
	}
	
	public static class PairKS extends StdSerializer<Pair> {
		
		PairKS() {
			super(Pair.class);
		}
		
		@Override
		public void serialize(Pair value, JsonGenerator jgen, SerializerProvider provider) throws JsonGenerationException,
		        IOException {
			ObjectMapper mapper = new ObjectMapper();
			jgen.writeFieldName(mapper.writeValueAsString(value));
		}
	}
}
