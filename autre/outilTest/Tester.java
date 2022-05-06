package linda.autre.outilTest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UnknownFormatConversionException;

/*
 *  Class Tester is a class used to test the Linda server based on a text file describing the test.
 */
public class Tester {

	Processes process;

	private Tester(File _file) {
		this.process = new Processes(_file);
	}

	
	
	
	public static void main(String[] args) {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
