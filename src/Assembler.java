import java.io.*;
import java.util.*;

public class Assembler {
	
	// Code files
	File assemblyCode; 
	File machineCode;
	
	// Symbol table. Holds predefined values, labels, and variables
	HashMap<String, Integer> SymbolTable = new HashMap<String, Integer>();
	
	// C-instruction, Assembly code to Binary equivalent
	HashMap<String, String> comp = new HashMap<String, String>();
	HashMap<String, String> dest = new HashMap<String, String>();
	HashMap<String, String> jump = new HashMap<String, String>();
	
	// File reader
	BufferedReader br;
	
	// The register number in which any variable will be assigned to is stored here
	int variableRegister = 16;
	
	// Initialize files, and symbol table
	public Assembler (File asm, File hack) throws NullPointerException {
		
		try {
			assemblyCode = asm;
			machineCode = hack;
			
			// Setting up predefined symbols of Hack Assembly Language
			for (int i=0; i < 16; i++) {
				SymbolTable.put("R"+i, i);
			}
			SymbolTable.put("SCREEN", 16384);
			SymbolTable.put("KBD", 24576);
			SymbolTable.put("SP", 0);
			SymbolTable.put("LCL", 1);
			SymbolTable.put("ARG", 2);
			SymbolTable.put("THIS", 3);
			SymbolTable.put("THAT", 4);
			
			comp.put("0", "0101010");
			comp.put("1", "0111111");
			comp.put("-1", "0111010");
			comp.put("D", "0001100");
			comp.put("A", "0110000");
			comp.put("!D", "0001101");
			comp.put("!A", "0110001");
			comp.put("-D", "0001111");
			comp.put("-A", "0110011");
			comp.put("D+1", "0011111");
			comp.put("A+1", "0110111");
			comp.put("D-1", "0001110");
			comp.put("A-1", "0110010");
			comp.put("D+A", "0000010");
			comp.put("D-A", "0010011");
			comp.put("A-D", "0000111");
			comp.put("D&A", "0000000");
			comp.put("D|A", "0010101");
			
			comp.put("M", "1110000");
			comp.put("!M", "1110001");
			comp.put("-M", "1110011");
			comp.put("M+1", "1110111");
			comp.put("M-1", "1110010");
			comp.put("D+M", "1000010");
			comp.put("D-M", "1010011");
			comp.put("M-D", "1000111");
			comp.put("D&M", "1000000");
			comp.put("D|M", "1010101");
			
			dest.put("null", "000");
			dest.put("M", "001");
			dest.put("D", "010");
			dest.put("MD", "011");
			dest.put("A", "100");
			dest.put("AM", "101");
			dest.put("AD", "110");
			dest.put("AMD", "111");
			
			jump.put("null", "000");
			jump.put("JGT", "001");
			jump.put("JEQ", "010");
			jump.put("JGE", "011");
			jump.put("JLT", "100");
			jump.put("JNE", "101");
			jump.put("JLE", "110");
			jump.put("JMP", "111");
			
		} catch (NullPointerException e) {
			System.out.println ("Please select a file to be translated");
		}
		
	}
	
	// Adds binary code to hack file
	public void assemble () throws Exception{
		
		// Get Labels
		firstPass();
		
		// Get variables and translate
		secondPass();
	}
	
	// Goes through file and adds labels to symbol table
	private void firstPass () throws Exception{
		
		int n = -1;

		String str;
		
		br = new BufferedReader(new FileReader(assemblyCode));
		
		while ((str = br.readLine()) != null) { 
			
			if (str.isBlank() || str.indexOf("//") == 0) {
				continue;
			}
			
			if (str.indexOf("//") != -1) {
				
				str = str.substring(0, str.indexOf("//"));
				str = str.strip();
				
			} else {
				
				str = str.strip();
				
			}
			
			if (str.contains("(")) {
				SymbolTable.put(str.substring(1, str.indexOf(")")), n+1);
			} else {
				n++;
			}
			
		}
				
	}
	
	// Goes through assembly code and translates intructions, while also looking out for variable names
	private void secondPass () throws Exception {
		
		PrintWriter MLcode = new PrintWriter(machineCode);
		
		br = new BufferedReader(new FileReader(assemblyCode));
		
		String str;
		
		while ((str = br.readLine()) != null) { 
			
			if (str.isBlank() || str.indexOf("//") == 0 || str.contains("(")) {
				continue;
			}
			
			if (str.indexOf("//") != -1) {
				
				str = str.substring(0, str.indexOf("//"));
				str = str.strip();
				
			} else {
				
				str = str.strip();
				
			}
			
			MLcode.println(translateInstruction(str));
			
		}
		
		MLcode.close();
	}
	
	// Translates a line of assembly code to 16-bit machine code
	private String translateInstruction (String s) {
		
		String instruction = "";
		
		if (s.contains("@")) {
			instruction += 0;
			String address = null;
			
			try {
				
				address = Integer.toBinaryString(Integer.parseInt(s.substring(1)));
				
			} catch (NumberFormatException e) {
				
				if (SymbolTable.containsKey(s.substring(1))) {
					
					address = Integer.toBinaryString(SymbolTable.get(s.substring(1)));
					
				} else {
					
					SymbolTable.put(s.substring(1), variableRegister);
					address = Integer.toBinaryString(variableRegister);
					variableRegister++;
					
				}
			}
			
			int addZero = 15 - (address.length());
			
			for (int i = 0; i < addZero; i++) {
				instruction += 0;
			}
			
			instruction += address;
			
		} else {
			
			instruction += "111";
			if (!s.contains("=")) {
				s = "null=" + s;
			}
			if (!s.contains(";")) {
				s += ";null";
			}
			String[] fields = s.split("[=;]");
			
			instruction += comp.get(fields[1]);
			instruction += dest.get(fields[0]);
			instruction += jump.get(fields[2]);
			
		}
		
		return instruction;
	}
	
	
}
