package com.d3bug.util;

public class StringUtil {

	public static String toCols(String license,int col) {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (int i=0; i<license.length(); i++) {
			char c = license.charAt(i);
			if (c == '\n') continue;
			sb.append(c);
			count++;
			if (count > col) {
				sb.append('\n');
				count = 0;
			}
		}
		return sb.toString();
	}
	
//	public static String toCols(String license,int col) {
//		StringBuilder sb = new StringBuilder();
//		int i = 0;
//		boolean doit = true;
//		while (doit) {
//			String x;
//			if (i + col >= license.length()) {
//				x = license.substring(i);
//				doit = false;
//			} else {
//				x = license.substring(i,i+col);
//			}
//			sb.append(x);
//			sb.append("\n");
//			i += x.length();
//			if (x.length() == 0) break; // oops
//		}
//		return sb.toString();
//	}

	public static void main(String[] args) {
		String x = "0123456789ABCDE\nFGHIJKLMNOPQRSTUVWXYZ0123456789ABCDEF\nGHIJKLMNOPQRSTUVWXYZ0123456789ABCD\nEFGHIJKLMNOPQRSTUVWXYZ0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		System.out.println(toCols(x,20));
	}
	
}
