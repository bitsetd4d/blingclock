package d3bug.licensing.server;

import java.util.ArrayList;

import d3bug.licensing.Base64;
import d3bug.licensing.CryptUtil;
import d3bug.licensing.ILicenseGenerator;

public class SignedStringLicenseGenerator implements ILicenseGenerator {
	
	private StringBuilder bits;
	
	public SignedStringLicenseGenerator() {
		bits = new StringBuilder();
	}
	
	public void put(String x) {
		int length = x.length();
		addTiny(length);
		for (int i=0; i<length; i++) {
			addChar(x.charAt(i));
		}
	}
	
	public void add(long l) {
		String x = Long.toBinaryString(l);
		putBits(x,64);
	}
	
	public byte[] toBytes() {
		ArrayList<Byte> list = new ArrayList<Byte>();
		int i = 0;
		while (i + 8 < bits.length()) {
			String sub = bits.substring(i,i+8);
			byte b = (byte)Integer.parseInt(sub, 2);
			list.add(b);
			i += 8;
		}
		String remainder = bits.substring(i) + "00000000";
		remainder = remainder.substring(0,8);
		byte b = (byte)Integer.parseInt(remainder, 2);
		list.add(b);
		byte[] bytes = new byte[list.size()];
		for (int j=0; j<bytes.length; j++) {
			bytes[j] = list.get(j);
		}
		return bytes;
	}
	
	private void addChar(char c) {
		byte b = (byte)c;
		String x = Integer.toBinaryString(b);
		putBits(x, 7);
	}

	private void addTiny(int length) {
		String x = Integer.toBinaryString(length);
		putBits(x,7);
	}
	
	private void putBits(String digits, int len) {
		String pad = "00000000000000000000000000000000000000000000000000000000000000000";
		String padded = pad + digits;
		String answer = padded.substring(padded.length() - len);
		bits.append(answer);
	}
	
	public String signAndGenerateStringCode() {
		String toHash = Base64.encodeBytes(toBytes());
		String hash = CryptUtil.SHA(toHash);
		//put(hash);
		put("HASH");
		String key = Base64.encodeBytes(toBytes());
		String z = key;
		for (int i=0; i<2; i++) {
			z = Base64.encodeBytes(z.getBytes());
		}
		return z;
	}
	
}
