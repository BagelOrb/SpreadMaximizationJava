package util.basics;

import generics.Tuple;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import network.analysis.Debug;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;



public class StringUtilsTK {

	public static String toString(Object o) {
		String ret = ""; 
		String claz = o.getClass().getSimpleName();
		String box = StringUtils.repeat('-', claz.length()) + "-+\r\n";
		ret += claz+" |\r\n"+box;

		int maxLenght =0;
		for (Field q : o.getClass().getDeclaredFields()) 
			if (!q.isSynthetic())
				maxLenght = Math.max(maxLenght, q.getName().length());
		try {
			for (Field q : o.getClass().getDeclaredFields()) 
				if (!q.isSynthetic()) {
					Object val = q.get(o);
					String valStr;
					if (Object[].class.isInstance(val)) valStr = Arrays.toString((Object[]) val);
					else valStr = val+"";
					ret+=(String.format("%-"+maxLenght+"s", q.getName())+
							" \t= "+valStr+"\r\n");
				}
			
		} catch (Exception e) { 
			e.printStackTrace(); }
		
		return ret;
	}
	public static void main(String[] args) {
		System.out.println(extractDouble("asfafsfsa:\t \r\n :q .421 fsa"));
	}
	public static Double extractDouble(String str) {
		String result="";
		String regex ="[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
		Matcher matcher = Pattern.compile( regex ).matcher( str);
		while (matcher.find( ))
		{
			result = matcher.group();  
			return Double.parseDouble(result);                 
		}
		return null;
	}
	
	public static String indent(String in) { return indent(in, "\t"); }
	public static String indent(String in, String indent) {
		String[] lines = lines(in);
		for (int l =0; l<lines.length; l++)
			lines[l] = indent+lines[l]+ "\r\n";
		return concatenate(lines);
	}
	
	public static String concatenate(String[] lines) {
		String ret = "";
		for (String line : lines)
			ret += line;
		return ret;
	}
	public static String[] lines(String in) {
		return in.split("\\r?\\n");
	}

	public static <T> String getMethod(Class<T> containingClass, String methodName) {
		String classStr = classToString(containingClass);
		
		String ret = "";
		int methodEnd = 1;
		
		while (methodEnd<classStr.length()) {
			int methodNameStart = classStr.indexOf(methodName, methodEnd);
			int methodStart = classStr.indexOf("{", methodNameStart);
			if (methodNameStart == -1) break;
			int nBrackets = 1;
			for (methodEnd = methodStart+1; nBrackets>0 && methodEnd < classStr.length(); methodEnd++) {
				if (classStr.charAt(methodEnd) == '{') nBrackets++;
				if (classStr.charAt(methodEnd) == '}') nBrackets--;
			}
			ret += classStr.substring(methodNameStart, methodEnd)+"\r\n";
		}
		ret = ret.replaceAll("//.*\r\\s*", ""); 
		return ret;
	}
	public static String subString(String or, String from, String to) {
		int i = or.indexOf(from);
		int j = or.indexOf(to,i);
		return or.substring(i, j);
	}
	
	public static <T> String classToString(Class<T> containingClass){
		String classPath = containingClass.getName().replaceAll("\\.", "\\\\");
		File javaFile = new File(	new File(containingClass.getProtectionDomain().getCodeSource().getLocation().getPath()	).getParentFile().getAbsolutePath()
							+"\\src\\"+classPath+".java");
		String classStr = "Error";
		try {
			classStr = FileUtils.readFileToString(javaFile);
		} catch (IOException e) { e.printStackTrace(); } 
		return classStr;
	}
	
	
	public static String subString(String original, String regex) {
		int end;
		boolean found = false;
		for (end = 1; end<original.length(); end++) {
			if (original.substring(0, end).matches(regex)) {
				found = true;
			}
			else if (found) return original.substring(0, end-1);
		}
		if (original.substring(0, original.length()-1).matches(regex)) return original.substring(0, original.length()-1);
		if (original.substring(0, original.length()).matches(regex)) return original;
		return "";
	}

	
	public static void testSubstring(String[] args) {
        //Debug.out(getMethod(SimpleNetwork.class,"public void learn(int nSamples)"));
        //Debug.out("qw.eerte".replaceAll("\\.", "\\\\"));
		
		
        Debug.out(subString("qwertya", "qwerty"));        
	}
	
	/**
	 * This method also skips java comments
	 * Note: this method assumes brackets to be consistent
	 * @param or the String on which to operate
	 * @param bracketOpen the openings bracket
	 * @param bracketClose the closing bracket
	 * @return the string between the first bracketOpen and its corresponding bracketClose
	 */
	public static String getStringWithinBrackets(String or, char bracketOpen, char bracketClose) {
		Tuple<Integer, Integer> tup = getIndicesOfStringWithinBrackets(or, bracketOpen, bracketClose, 0);
		return  or.substring(tup.fst, tup.snd);
	}
	/**
	 * This method also skips java comments
	 * Note: this method assumes brackets to be consistent
	 * @param or the String on which to operate
	 * @param bracketOpen the openings bracket
	 * @param bracketClose the closing bracket
	 * @return the first and last index of the substring between the first bracketOpen and its corresponding bracketClose
	 */
	public static Tuple<Integer, Integer> getIndicesOfStringWithinBrackets(String or, char bracketOpen, char bracketClose, int from) {
//		if (or==null) return null;
		
		String open = "{[<(";
		String close = "}]>)";
		int l = or.length();
				
		int pos;
		int depth = 0;
		boolean foundFirst = false;
		int depthFirst = -1;
		for (pos = from; pos<or.length(); pos++) {
			char now = or.charAt(pos);
			if (now=='\'' && or.charAt(pos-1)!='\\') {
				char prev = '\\';
				while (now!='\'' || prev == '\\') {pos++; prev = now; now = or.charAt(pos); if (pos==l) 
					return null;} 
				continue;
			}
			if (now=='\"' && or.charAt(pos-1)!='\\') {
				char prev = '\\';
				while (now!='\"' || prev == '\\') { pos++; prev = now; now = or.charAt(pos); if (pos==l) 
					return null;} 
				continue;
			}
			if (now=='/' && or.charAt(pos+1)=='/') {
				while (!(now=='\n' || now=='\r')) {
					pos++; 
					now = or.charAt(pos);
					if (pos==l) 
						return null;} 
				continue;
			}
//            if (now=='/') Debug.out(">>"+or.charAt(pos+1)+"<<");
			if (now=='/' && or.charAt(pos+1)=='*') {
				char prev = ' ';
				while (!(now=='/' && prev == '*')) { pos++; prev = now; now = or.charAt(pos); if (pos==l) 
					return null;} 
				continue;
			}

//            Debug.out(">>"+or.substring(pos, pos+10)+"<<");
//			if (now==bracketOpen) 
//                Debug.out(or.substring(pos, pos+10));
			if (now==bracketOpen && !foundFirst) { 
				foundFirst = true; from = pos; depthFirst= depth-1; continue;}
			if (open.indexOf(now)!=-1) depth++;
			if (close.indexOf(now)!=-1) depth--;
			if (depth==depthFirst && foundFirst) 
				return new Tuple<Integer,Integer>(from+1, pos);
						// && now==bracketClose
			if (pos==l) 
				return null;
			
		} 
		return null;
	}
	
	public static void test_getStringWithinBrackets() {
        Debug.out("//hgf[hdhsdf\r\nahdhfda/*sd[ghgd*/dgsgd[fhd]gd \t::\t>>"+getStringWithinBrackets("//hgf[hdhsdf\r\nahdhfda/*sd[ghgd*/dgsgd[fhd]gd",'[',']')+"<<");
        Debug.out(">>d(s{\"f[s}\"}aa)aaa<<\t::\t>>"+getStringWithinBrackets("d(s{\"f[s}\"}aa)aaa",'(',')')+"<<");
        Debug.out(">>daa<<\t::\t>>"+getStringWithinBrackets("daa",'(',')')+"<<");
        Debug.out(">><<\t::\t>>"+getStringWithinBrackets("",'(',')')+"<<");
	}
	
//	public static int indexOf_noComments(String str, String find) {
//		int ret = -1;
//		while (true) {
//			str.indexOf(find);
//			if (str.indexOf("//")))
//		}
//		
//	}
	static void mainTestSubString(String[] args) {
		try {
			String str = FileUtils.readFileToString(new File("crap.txt"));
//            Debug.out(str);
//            Debug.out(">>"+getStringWithinBrackets(str, '[',']')+"<<");
			
			LinkedList<String> ret = new LinkedList<String>(); 
			int q = str.indexOf("data[");
			Tuple<Integer, Integer> w = getIndicesOfStringWithinBrackets(str, '[', ']', q);
			Tuple<Integer, Integer> w2 = getIndicesOfStringWithinBrackets(str, '[', ']', w.snd);
			Tuple<Integer, Integer> w3 = getIndicesOfStringWithinBrackets(str, '[', ']', w2.snd);
			
//			ret.add(str.substring(0, q));
			ret.add("data(");
			ret.add(str.substring(w.fst, w.snd)+",");
			ret.add(str.substring(w2.fst, w2.snd)+",");
			ret.add(str.substring(w3.fst, w3.snd)+")");
			
            Debug.out(ret);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
