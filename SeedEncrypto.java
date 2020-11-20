package test;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.io.UnsupportedEncodingException;

import org.json.simple.JSONObject;

public class mains {
	
	private static String CHARSET = "utf-8";
	private static String CHARSET_EUC = "euc-kr";
	private static byte [] seedKey = "interparkcookie0".getBytes(); //16
	
  // UTF-8 -> EUC-KR -> SEED -> base64 암호화 방식 
  // 암호화 파일은 아래 주소에서 다운로드 후 사용 (KISA_SEED_ECB)
  // https://seed.kisa.or.kr/kisa/Board/17/detailView.do 
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		JSONObject json = new JSONObject();
		String userId = "testCode";
		String userName = "테스트";
		json.put("userId", userId);
		json.put("userName", userName);
		
		String str = json.toJSONString(); //시작

		System.out.println("인코딩 전 텍스트 UTF-8: " + new String(str)); 
		
		byte[] base64EncodingStr = (new String(str)).getBytes(CHARSET_EUC);
		System.out.println("euc-kr 인코딩 텍스트 : " + new String(base64EncodingStr));
		
		byte [] seedEncryptByte = SeedEncrypt(base64EncodingStr);
		System.out.println("SEED 암호화 된 텍스트 : " + new String(seedEncryptByte)); 
		
		Encoder encoder = Base64.getEncoder(); 
		byte[] encodedBytes = encoder.encode(seedEncryptByte);
		System.out.println("base64 인코딩 text : " + new String(encodedBytes)); 
		
		
		System.out.println("===================================================="); 
		
		
		byte[] encodedByteStr = encodedBytes;
		
		System.out.println("euc-kr 상태인 text : " + new String(encodedByteStr));
		
		Decoder decoder = Base64.getDecoder(); 
		byte[] decodedBytes = decoder.decode(encodedByteStr);
		System.out.println("base64 디코딩 text : " + new String(decodedBytes));
		
		byte[] DecryptSeed = SeedDecrypt(decodedBytes);
		System.out.println("SEED 디코딩 결과 : " + new String(DecryptSeed)); 
	
		String decodeSeedUserNameAndUserId = new String(base64EncodingStr, CHARSET_EUC);
		System.out.println("euc-kr 디코딩 텍스트 : " + decodeSeedUserNameAndUserId);
    
    String decodeEUCKRUserNameAndUserId = new String(decodeSeedUserNameAndUserId, CHARSET_EUC);
		System.out.println("UTF-8 해독결과 : " + decodeEUCKRUserNameAndUserId);

	}
	
	//SEED 암호화
	public static byte []  SeedEncrypt(byte[] strData) {
		byte [] enc = null;
		enc = KISA_SEED_ECB.SEED_ECB_Encrypt(seedKey, strData, 0, strData.length);
		return enc;
	}
	
	//SEED 암호화 해제 
	public static byte [] SeedDecrypt(byte[]  strData) throws UnsupportedEncodingException {
		byte [] enc = null;
		enc = KISA_SEED_ECB.SEED_ECB_Decrypt(seedKey, strData, 0, strData.length);
		return enc;
	}


}
