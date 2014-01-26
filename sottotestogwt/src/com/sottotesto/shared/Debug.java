package com.sottotesto.shared;

public class Debug {
	static boolean debugON = false;
	static boolean errorON = true;

	public static void printDbgLine(String text){
		if (debugON){
			System.out.println(text);
		}
	}

	public static void printErrLine(String text){
		if (errorON){
			System.out.println(text);
		}
	}
}
