package com.sottotesto.shared;

public class Debug {
	static boolean ON = true;

	public static void printDbgLine(String text){
		if (ON){
			System.out.println(text);
		}
	}

}
