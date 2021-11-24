import static java.lang.System.arraycopy;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class UTF8Encoder {
	
	public static byte[] encode(String s) {
		List<byte[]> list = s.codePoints().mapToObj(UTF8Encoder::encode).toList();
		byte[] result = new byte[countStringBytes(s)];
		int index=0;
		for(byte[] b:list) {
			arraycopy(b,0,result,index,b.length);
			index+=b.length;
		}
		return result;
	}
	
	private static int countStringBytes(String s) {
		return s.codePoints().map(UTF8Encoder::countBytes).sum();
	}

	private static int countBytes(int codePoint) {
		if(codePoint>='\u0000' && codePoint<='\u007F') return 1;
		if(codePoint>='\u0080' && codePoint<='\u07FF') return 2;
		if(codePoint>='\u0800' && codePoint<='\uFFFF') return 3;
		if(codePoint>=0x10000 && codePoint<=0x10FFFF) return 4;
		throw new IllegalArgumentException("wrong codepoint");
	}

	public static byte[] encode(int codePoint) {
		if(codePoint>='\u0000' && codePoint<='\u007F') return encodeOneByte(codePoint);
		if(codePoint>='\u0080' && codePoint<='\u07FF') return encodeTwoBytes(codePoint);
		if(codePoint>='\u0800' && codePoint<='\uFFFF') return encodeThreeBytes(codePoint);
		if(codePoint>=0x10000 && codePoint<=0x10FFFF) return encodeFourBytes(codePoint);
		throw new IllegalArgumentException("wrong codepoint");
	}

	private static byte[] encodeOneByte(int codePoint) {
		return new byte[] {(byte)(codePoint & 0x7F)};
	}

	private static byte[] encodeTwoBytes(int codePoint) {
		return new byte[] {
				(byte)(0xC0 | codePoint>>>6 & 0x1F),
				(byte)(0x80 | codePoint & 0x3F)
		};
	}

	private static byte[] encodeThreeBytes(int codePoint) {
		return new byte[] {
				(byte)(0xE0 | codePoint>>>12 & 0xF),
				(byte)(0x80 | codePoint>>>6 & 0x3F),
				(byte)(0x80 | codePoint & 0x3F)
		};
	}

	private static byte[] encodeFourBytes(int codePoint) {
		return new byte[] {
				(byte)(0xF0 | codePoint>>>18 & 0x7),
				(byte)(0x80 | codePoint>>>12 & 0x3F),
				(byte)(0x80 | codePoint>>>6 & 0x3F),
				(byte)(0x80 | codePoint & 0x3F)
		};
	}

	public static void main(String[] args) {
		byte[] bytes1=encode("ABCDEFGHIJKLMN");
		System.out.println(new String(bytes1,StandardCharsets.UTF_8));

		byte[] bytes2=encode("ÀÁÂÃÄÅªÆÇÈ²¯É");
		System.out.println(new String(bytes2,StandardCharsets.UTF_8));
		
		byte[] bytes3=encode("\u20AC\u2116\u2122\u2018\u2019\u201A\u201C\u201D\u201E");
		System.out.println(new String(bytes3,StandardCharsets.UTF_8));
		
		byte[] bytes4=encode(0x1F600);
		System.out.println(new String(bytes4,StandardCharsets.UTF_8));

	}

}
