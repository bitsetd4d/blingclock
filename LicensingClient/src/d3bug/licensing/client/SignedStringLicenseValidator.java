package d3bug.licensing.client;

import java.util.ArrayList;

import d3bug.licensing.Base64;
import d3bug.licensing.CryptUtil;
import d3bug.licensing.ILicenseValidator;

public class SignedStringLicenseValidator implements ILicenseValidator {

	private StringBuilder bits = new StringBuilder();
	private int ix = 0;

	public SignedStringLicenseValidator(String code) {
		try {
			String q = code;
			for (int i=0; i<2; i++) {
				q = new String(Base64.decode(q));
			}
			byte[] bytes = Base64.decode(q);
			for (byte b : bytes) {
				String x = Integer.toBinaryString(b);
				String padded = "00000000" + x;
				String answer = padded.substring(padded.length() - 8);
				bits.append(answer);
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public String readString() {
		int length = readTiny();
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<length; i++) {
			char c = readChar();
			sb.append(c);
		}
		return sb.toString();
	}

	private char readChar() {
		char c = 0;
		for (int i=6; i>=0; i--) {
			boolean bit = readBit();
			if (bit) {
				c += 1 << i; 
			}
		}
		return c;
	}

	private int readTiny() {
		int v = 0;
		for (int i=6; i>=0; i--) {
			boolean bit = readBit();
			if (bit) {
				v += 1 << i; 
			}
		}
		return v;
	}
	
	private boolean readBit() {
		char c = bits.charAt(ix++);
		return c == '1';
	}
	
	public boolean verifySignature() {
		String bitsSoFar = bits.substring(0,ix);
		String signature = readString();
//		String toHash = Base64.encodeBytes(toBytes(bitsSoFar));
//		String hash = CryptUtil.SHA(toHash);
		return "HASH".equals(signature);
	}

	private static byte[] toBytes(String bitsSoFar) {
		ArrayList<Byte> list = new ArrayList<Byte>();
		int i = 0;
		while (i + 8 < bitsSoFar.length()) {
			String sub = bitsSoFar.substring(i,i+8);
			byte b = (byte)Integer.parseInt(sub, 2);
			list.add(b);
			i += 8;
		}
		String remainder = bitsSoFar.substring(i) + "00000000";
		remainder = remainder.substring(0,8);
		byte b = (byte)Integer.parseInt(remainder, 2);
		list.add(b);
		byte[] bytes = new byte[list.size()];
		for (int j=0; j<bytes.length; j++) {
			bytes[j] = list.get(j);
		}
		return bytes;
	}
	
}
