package org.alphallc.extras;

public class ByteOps {
	public static byte[] Remove(byte[] in, int pos) {
		int len = in.length-1;
		byte[] out = new byte[len];
		int x = 0;
		int i = 0;
		while(x < len) {
			if(x == pos) { i++; }
			out[x] = in[i];
			i++;
			x++;
		}
		return out;
	}

	public static String ContentDec(byte[] b) {
		int value = 0;
		String str = "";
		for(int i=0; i<b.length; i++) {
			value = 0;
			value = (value & 0xFF) | b[i];
			str = str.concat(value+"").concat(" ");
		}
		return str;
	}

	public static String ContentHex(byte[] b) {
		String str = "";
		int v = 0;

		for(int i=0; i<b.length; i++) {
			v = b[i];
			if (b[i] < 0) {
				v = v & 0xFF;
			}
			str = str.concat(Integer.toHexString(0x100 | v).substring(1).toUpperCase()).concat(" ");
		}
		return str;
	}

}
