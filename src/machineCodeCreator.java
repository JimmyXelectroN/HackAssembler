import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;

public class machineCodeCreator {
	
	// takes in assembly code
	// creates empty machine language file
	// uses assembler to populate the the machine language file
	public static void main (String args[]) throws IOException{
		JFileChooser chooser = new JFileChooser();
		File assemblyCode = null;
		
		// Open file dialog to pick assembly code
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			assemblyCode = chooser.getSelectedFile();
		}
		
		// Creating empty machine language file
		File machineCode = null;
		try {
			machineCode = new File(assemblyCode.getParent() + "\\" + (assemblyCode.getName().substring(0, assemblyCode.getName().indexOf("."))) + ".hack");
			if (machineCode.createNewFile()) {
				System.out.println("File successfully created");
			} else {
				System.out.println("File already exists");
			}
			
		} catch (IOException e) {
			System.out.println("An error occurred.");
		    e.printStackTrace();
		}

		// initialize assembler, which takes as input the assembly code file and the empty machine code file
		Assembler assembler = new Assembler (assemblyCode, machineCode);
		// assemble code
		try {
			assembler.assemble();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		File testCode = null;
		
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			testCode = chooser.getSelectedFile();
		}
		
		tester (machineCode, testCode);
	}
	
	private static void tester (File f1, File f2) throws IOException {
		String str1 = "";
		String str2 = "";
		
		BufferedReader br = new BufferedReader(new FileReader(f1));
		
		while (br.readLine() != null) { 
			str1 += br.readLine();
		}
		
		br = new BufferedReader(new FileReader(f2));
		
		while (br.readLine() != null) { 
			str2 += br.readLine();
		}
		
		if (str1.equals(str2)) {
			System.out.println("Translation was successful");
		} else {
			System.out.println("Translation failed");
		}
	}
}
