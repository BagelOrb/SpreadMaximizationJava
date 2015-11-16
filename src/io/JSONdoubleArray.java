package io;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;


public class JSONdoubleArray {

	/*
	 * writing new JSON file
	 */
	public static void write(double[] in, File file) throws IOException {
		JsonGenerator gen = Json.createGenerator(new FileWriter(file));
		writeArray(gen, in);
		gen.close();
	}
	public static void write(double[][] in, File file) throws IOException {
		JsonGenerator gen = Json.createGenerator(new FileWriter(file));
		writeArray(gen, in); 
		gen.close();
	}
	public static void write(double[][][] in, File file) throws IOException {
		JsonGenerator gen = Json.createGenerator(new FileWriter(file));
		writeArray(gen, in);
		gen.close();
	}
	public static void write(double[][][][] in, File file) throws IOException {
		JsonGenerator gen = Json.createGenerator(new FileWriter(file));
		writeArray(gen, in);
		gen.close();
	}
	/*
	 * writing to generator
	 */
	public static void writeArray(JsonGenerator gen, double[] re) {
		gen.writeStartArray();
		for (double d : re)
			gen.write(Double.toString(d));
		gen.writeEnd();
	}
	public static void writeArray(JsonGenerator gen, double[][] re, String name) {
		gen.writeStartArray(name);
		for (double[] l : re)
			writeArray(gen, l);
		gen.writeEnd();
	}
	public static void writeArray(JsonGenerator gen, double[][] re) {
		gen.writeStartArray();
		for (double[] l : re)
			writeArray(gen, l);
		gen.writeEnd();
	}
	public static void writeArray(JsonGenerator gen, double[][][] re, String name) {
		gen.writeStartArray(name);
		for (double[][] l : re)
			writeArray(gen, l);  
		gen.writeEnd();
	}
	public static void writeArray(JsonGenerator gen, double[][][] re) {
		gen.writeStartArray();
		for (double[][] l : re)
			writeArray(gen, l);  
		gen.writeEnd();
	}
	public static void writeArray(JsonGenerator gen, double[][][][] re) {
		gen.writeStartArray();
		for (double[][][] l : re)
			writeArray(gen, l);
		gen.writeEnd();
	}
	
	
	
	/*
	 * parsing from file
	 */
	public static double[] read1D(File file ) throws IOException {
		JsonParser parser = Json.createParser(new FileInputStream(file));
		return parse1D(parser, -1);
	}
	public static double[][] read2D(File file ) throws IOException {
		JsonParser parser = Json.createParser(new FileInputStream(file));
		return parse2D(parser, -1, -1);
	}
	public static double[][][] read3D(File file ) throws IOException {
		JsonParser parser = Json.createParser(new FileInputStream(file));
		return parse3D(parser, -1, -1, -1);
	}
	public static double[][][][] read4D(File file ) throws IOException {
		JsonParser parser = Json.createParser(new FileInputStream(file));
		return parse4D(parser, -1, -1, -1, -1);
	}

	/*
	 * parsing from JSON parser
	 */
	public static double[] parse1D(JsonParser parser, int length) throws IOException {
		ArrayList<Double> l = new ArrayList<Double>();
		double[] ret = new double[Math.max(length, 0)];
		
		int counter = 0;
		outerLoop:
			while (parser.hasNext()) {
			   JsonParser.Event event = parser.next();
			   switch(event) {
			      case START_ARRAY: break;
			      case END_ARRAY:
			    	  break outerLoop;
			      case VALUE_STRING:
			    	  double d = Double.parseDouble(parser.getString());
			    	  if (length < 0)	l.add(d);
			    	  else 				{ ret[counter] = d; counter++; }
			         break;
			      default:
			    	  throw new IOException("Not a 1 dimensional array! Seeing: "+event.toString()+" at "+parser.getLocation()+ "\r\n, expecting VALUE_STRING (Double.toString())");
			   }
			}
		if (length >= 0)
			return ret;
		ret = new double[l.size()];
		for (int i = 0; i<l.size(); i++)
			ret[i] = l.get(i);
		return ret;
	}
	public static double[][] parse2D(JsonParser parser, int l2D, int l1D) throws IOException {
		ArrayList<double[]> l = new ArrayList<double[]>();
		double[][] ret = new double[Math.max(l2D, 0)][Math.max(l1D, 0)];
		
		int counter = 0;
		outerLoop:
			while (parser.hasNext()) {
				JsonParser.Event event = parser.next();
				switch(event) {
				case START_ARRAY: 
					double[] list = parse1D(parser, l1D);
					if (l2D <0) l.add(list);
					else 		{ ret[counter] = list; counter++; }
					l1D = list.length;
					break;
				case END_ARRAY:
					break outerLoop;
				default:
					throw new IOException("Not a 2 dimensional array! Seeing: "+event.toString()+" at "+parser.getLocation()+ "\r\n, expecting START_ARRAY");
				}
			}
		if (l2D >= 0)
			return ret;
		ret = new double[l.size()][l1D];
		for (int i = 0; i<l.size(); i++)
			ret[i] = l.get(i);
		return ret;
	}
	public static double[][][] parse3D(JsonParser parser, int l3D, int l2D, int l1D) throws IOException {
		ArrayList<double[][]> l = new ArrayList<double[][]>();
		double[][][] ret = new double[Math.max(l3D, 0)][Math.max(l2D, 0)][Math.max(l1D, 0)];
		
		int counter = 0;
		outerLoop:
			while (parser.hasNext()) {
				JsonParser.Event event = parser.next();
//                Debug.out(event);
				switch(event) {
				case START_ARRAY: 
					double[][] list = parse2D(parser, l2D, l1D);
					if (l3D <0) l.add(list);
					else 		{ ret[counter] = list; counter++; }
					l2D = list.length; l1D = list[0].length;
					break;
				case END_ARRAY:
					break outerLoop;
				default:
					String str = "";
					try { str = " : '"+parser.getString()+ "'"; } catch (Exception e) {} 
					throw new IOException("Not a 3 dimensional array! Seeing: "+event.toString()+str+ " at "+parser.getLocation()+ "\r\n, expecting START_ARRAY");
				}
			}
		if (l3D >= 0)
			return ret;
		ret = new double[l.size()][l2D][l1D];
		for (int i = 0; i<l.size(); i++)
			ret[i] = l.get(i);
		return ret;
	}
	public static double[][][][] parse4D(JsonParser parser, int l4D, int l3D, int l2D, int l1D) throws IOException {
		ArrayList<double[][][]> l = new ArrayList<double[][][]>();
		double[][][][] ret = new double[Math.max(l4D, 0)][Math.max(l3D, 0)][Math.max(l2D, 0)][Math.max(l1D, 0)];
		
		int counter = 0;
		outerLoop:
			while (parser.hasNext()) {
				JsonParser.Event event = parser.next();
				switch(event) {
				case START_ARRAY: 
					double[][][] list = parse3D(parser, l3D, l2D, l1D);
					if (l4D <0) l.add(list);
					else 		{ ret[counter] = list; counter++; }
					l3D = list.length; l2D = list[0].length; l1D = list[0][0].length;
					break;
				case END_ARRAY:
					break outerLoop;
				default:
					throw new IOException("Not a 4 dimensional array! Seeing: "+event.toString()+" at "+parser.getLocation()+ "\r\n, expecting START_ARRAY");
				}
			}
		if (l3D >= 0)
			return ret;
		ret = new double[l.size()][l3D][l2D][l1D];
		for (int i = 0; i<l.size(); i++)
			ret[i] = l.get(i);
		return ret;
	}
}
