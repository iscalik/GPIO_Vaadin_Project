package com.rpi.Utils;

public class Utils {
	
	private static final String OutputPrefix = "RPI_GPIO_CC: ";
	
	public static void ExecExternalProgram(String command, boolean waitForProgramFinish, boolean endProcess) {		
		Process p = null;
	    try {
	      p = Runtime.getRuntime().exec(command);
	      if (waitForProgramFinish) {
	    	  p.waitFor();
	      }
	    }
	    catch (Exception err) {
	      err.printStackTrace();
	    }
	    finally {
	    	if (endProcess) {
	    		p.destroy();
	    	}
	    }	    
	}
	
	public static void Output_Write(boolean addPrefix, String s) {
		if (addPrefix) {
			System.out.print(OutputPrefix);
		}
		System.out.print(s);
	}
	
	public static void Output_WriteLn(boolean addPrefix, String s) {
		if (addPrefix) {
			System.out.print(OutputPrefix);
		}
		System.out.println(s);
	}
}
