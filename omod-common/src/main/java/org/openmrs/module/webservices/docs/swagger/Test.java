package org.openmrs.module.webservices.docs.swagger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.util.List;

import org.openmrs.module.webservices.docs.ResourceDoc;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Test {
	
	private static SwaggerSpecification swaggerSpecification = new SwaggerSpecification();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		List<ResourceDoc> resourceDoc = null;
		
		try {
			FileInputStream fileIn = new FileInputStream("C://Users//zakaria//Desktop//spec.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			swaggerSpecification = (SwaggerSpecification) in.readObject();
			in.close();
			fileIn.close();
		}
		catch (Exception exp) {
			exp.printStackTrace();
		}
		
		CreateJSON();
		
		System.out.println("Created");
		
	}
	
	private static String CreateJSON() {
		String json = "";
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
			mapper.setSerializationInclusion(Include.NON_NULL);
			mapper.getSerializerProvider().setNullKeySerializer(new NullSerializer());
			
			String resourceName = "localhost:8080/openmrs-dev/ws/rest/concept";
			String resourceName2 = "localhost:8080/openmrs-dev/ws/rest/concept";
			System.out.println("test ---------------------" + resourceName.equals(resourceName2));
			
			/*String url = "http://localhost:8080/openmrs-standalone";
			
			String modified = url.replace("http://", "");
			
			System.out.println(modified);
			
			json = mapper.writeValueAsString(swaggerSpecification);*/
			/*File file = new File("C://Users//zakaria//Desktop//output.json");
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(json);
			bw.close();*/
		}
		catch (Exception exp) {
			exp.printStackTrace();
		}
		
		return json;
	}
	
}
