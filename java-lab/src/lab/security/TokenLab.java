package lab.security;

import java.util.Date;
import java.util.Random;

import utils.Assert;
import utils.Base64;
import utils.Hex;
import utils.Sha512DigestUtils;
import utils.StringUtils;
import utils.Utf8;

public class TokenLab {

	public Token allocateToken(String extendedInformation) {
		// 创建时间
		long creationTime = new Date().getTime();

		// 生成一个伪随机数
		byte[] randomizedBits = new byte[256];
		Random random = new Random();
		random.nextBytes(randomizedBits);
		String pseudoRandomNumber = new String(Hex.encode(randomizedBits));

		// Server Secret
		Integer serverInteger = 100;
		String serverSecret = "123456" + ":" + new Long(creationTime % serverInteger.intValue()).intValue();

		String content = Long.toString(creationTime) + ":" + pseudoRandomNumber + ":" + extendedInformation;

		// Compute key
		String sha512Hex = Sha512DigestUtils.shaHex(content + ":" + serverSecret);
		String keyPayload = content + ":" + sha512Hex;
		String key = Utf8.decode(Base64.encode(Utf8.encode(keyPayload)));

		return new Token(key, creationTime, extendedInformation);
	}

	public Token verifyToken(String key) {
		if (key == null || "".equals(key)) {
			return null;
		}
		String[] tokens = StringUtils.delimitedListToStringArray(Utf8.decode(Base64.decode(Utf8.encode(key))), ":");

		// creation time
		long creationTime;
		try {
			creationTime = Long.decode(tokens[0]).longValue();
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Expected number but found " + tokens[0]);
		}
		
		// pseudo randome number
		String pseudoRandomNumber = tokens[1];

		// Server Secret
		Integer serverInteger = 100;
		String serverSecret = "123456" + ":" + new Long(creationTime % serverInteger.intValue()).intValue();
		
		// Permit extendedInfo to itself contain ":" characters
        StringBuilder extendedInfo = new StringBuilder();
        for (int i = 2; i < tokens.length-1; i++) {
            if (i > 2) {
                extendedInfo.append(":");
            }
            extendedInfo.append(tokens[i]);
        }

        String sha1Hex = tokens[tokens.length-1];

        // Verification
        String content = Long.toString(creationTime) + ":" + pseudoRandomNumber + ":" + extendedInfo.toString();
        String expectedSha512Hex = Sha512DigestUtils.shaHex(content + ":" + serverSecret);
        
        Assert.isTrue(expectedSha512Hex.equals(sha1Hex), "Key verification failure");

        return new Token(key, creationTime, extendedInfo.toString());
	}
	
	public static void main(String[] args) {
		TokenLab lab = new TokenLab();
		Token token = lab.allocateToken("ansen:xxx");
		System.out.println("Token is: "+token);
		
		token = lab.verifyToken(token.getKey());
		System.out.println("Token is: "+token);
	}

	class Token {
		private final String key;
		private final long keyCreationTime;
		private final String extendedInformation;

		public Token(String key, long keyCreationTime, String extendedInformation) {
			this.key = key;
			this.keyCreationTime = keyCreationTime;
			this.extendedInformation = extendedInformation;
		}

		public String getKey() {
			return key;
		}

		public long getKeyCreationTime() {
			return keyCreationTime;
		}

		public String getExtendedInformation() {
			return extendedInformation;
		}
		
		public String toString() {
	        return "Token[key=" + key + "; creation=" + new Date(keyCreationTime) + "; extended=" + extendedInformation + "]";
	    }
	}

}
