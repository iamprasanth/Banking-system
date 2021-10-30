/**
 * 
 */
package JavaProject;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author Prasanth
 * A helper class for writing common reusable functions
 */
public class HelperClass {

	/**
	 * Function returns a file whose name is matching with the keyword
	 * Possible inputs can be in the format "accountnumber_" or "_username"
	 * @param keyword
	 * @return The file whose name is matching with the keyword
	 */
	public static File getMatchingFile(String keyword) {
		File directory = new File(Constant.pathToRoot + Constant.clientDataPath);
		if (directory != null) {// To check if there are files in this directory
	        File[] listOfFiles = directory.listFiles();
	        String currentFile;
	        for (int i = 0; i < listOfFiles.length; i++) {
	            if (listOfFiles[i].isFile()) {
	            	currentFile = listOfFiles[i].getName();
	                if (currentFile.endsWith(keyword) || currentFile.startsWith(keyword)) {
	                    return listOfFiles[i];// User name already exist
	                }
	            }
	        }
		}
        // User name does not exist
    	return null;
	}
	
	// For success message popup
	static void showSuccessMessage(String message) {
	    JFrame frame =new JFrame();  
	    JOptionPane.showMessageDialog(frame ,message);  
	}

	/**
	 * Function to check if a string contains a valid numeric value
 	 * @param value it holds the string to be checked for numeric value
 	 * @return true if the string is a positive numerical else returns false
	 */	
	public static boolean isNumericString(String value) {
	    try {
		    if (Double.parseDouble(value) > 0) {
		        return true;
		    }
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return false;
	}
}
