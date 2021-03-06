import java.io.*;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

//import org.rosuda.JRI.Rengine;

import java.security.GeneralSecurityException;


public class Source {
	public static String _encryptAlg = "AES-256";
	public static String _hashAlg = "MD5";
	static final MngrFiles mgrFile = new MngrFiles();
	private static MngrScript _mgrScript = new MngrScript();
	
	public static Vector<String> fileList = new Vector<String>();
	public static Vector<Double> ProbVector = new Vector<Double>();
	public static Vector<String> MAC = new Vector<String>();
	
	/*
	 * MAIN()
	 */
	public static void main(String[] args) throws  Exception {
		// TODO Auto-generated method stub

		_mgrScript.exeCreateRScriptFile(100);
		
		ExecuteShellComand obj = new ExecuteShellComand();

		String command = "Rscript src\\Distribution.R";

		String output = obj.executeCommand(command);

		//System.out.println(output);

		//Vector<Double> ProbVector = new Vector<Double>();
		convertRToArray(output, ProbVector);
		/*-----------------------------------------------------------------------------------------*/
		// @ProbVector is a list of probability
		/*HMAC Test
		 * To excute HMAC set input key: String set input data: String
		 * initialize Hmac with _hashAlg and key update data
		 */
		//HMACTest();
		/*-----------------------------------------------------------------------------------------*/
		// for each file in encrypt, compute HMAC and save it into csv file
		mgrFile.getFileList(MngrFiles.folderData, fileList);
		//TestEncrypt();
		Primary();
		mgrFile.writeResultCsv();
		
		
	}
	/*
	 * copute HMAC for each file encrypted
	 * */
	public static void Primary() throws GeneralSecurityException, IOException{
		/*
		long startTime;
		double duration;
		long endTime;
		 */
		
		for (int i = 0; i < fileList.size(); i++) {
			HMAC hmac = new HMAC(_hashAlg, ProbVector.get(i).toString().getBytes());
			String tmpfile = MngrFiles.folderEncrypted + fileList.get(i) + ".enc";
			System.out.println(tmpfile);
			
			//startTime = System.nanoTime();
			byte[] sign = hmac.signFile(tmpfile);
			//endTime = System.nanoTime();
			//duration = (endTime - startTime) / 1000000.0;
			
	        //convert the byte to hex format method 1
	        String sb = new String();
	        for (int j = 0; j < sign.length; j++) {
	         sb = sb + (Integer.toString((sign[j] & 0xff) + 0x100, 16).substring(1));
	        }
	        System.out.println(sb);
	        MAC.addElement(sb);
	        //System.out.println(duration);
		}
	}
	
	public static void TestEncrypt() throws Exception{
		for(int i = 0; i < fileList.size(); i++){
			String tmp = fileList.get(i);
			String fileName = MngrFiles.folderData + tmp;
			String tempFileName = MngrFiles.folderEncrypted + tmp + ".enc";
			String resultFileName = MngrFiles.folderDecrypted + tmp;

			File file = new File(fileName);
			if(!file.exists()){
				System.out.println("No file "+fileName);
				return;
			}
			File file2 = new File(tempFileName);
			File file3 = new File(resultFileName);
			if(file2.exists() || file3.exists()){
				System.out.println("File for encrypted temp file or for the result decrypted file already exists. Please remove it or use a different file name");
				return;
			}

			AES.copy(Cipher.ENCRYPT_MODE, fileName, tempFileName, "passwordtrongcau");
			AES.copy(Cipher.DECRYPT_MODE, tempFileName, resultFileName, "passwordtrongcau");

			System.out.println("Success. Find encrypted and decrypted files in current directory");
		}
	}
	
	public static void HMACTest() throws GeneralSecurityException, IOException {
		long startTime;
		double duration;
		long endTime;
		String fileDirect = "C:\\Users\\trong\\Google Drive\\WORK DOCUMENT\\Computer Security\\K12\\Topic02_ComputerNetworkOverview\\CCDA DESGN 640-864.rar";
		String keyPhrase = "this is a key";
		HMAC hmac = new HMAC(_hashAlg, keyPhrase);
		
		startTime = System.nanoTime();
		byte[] sign = hmac.signFile(fileDirect);
		endTime = System.nanoTime();
		duration = (endTime - startTime) / 1000000.0;
		
        //convert the byte to hex format method 1
        String sb = new String();
        for (int i = 0; i < sign.length; i++) {
         sb = sb + (Integer.toString((sign[i] & 0xff) + 0x100, 16).substring(1));
        }
        System.out.println(sb);
        System.out.println(duration);
	}
	
	public static void convertRToArray(String in, Vector<Double> out) {
		// String[] splitString = in.split("(\\w)*.(\\w)*e-(\\w)*");

		String pattern = "\\d+\\.\\d*e?-?\\d+";
		// Create a Pattern object
		Pattern r = Pattern.compile(pattern);
		// Now create matcher object.
		Matcher m = r.matcher(in);

		while (m.find() == true) {
			out.addElement(Double.parseDouble(m.group(0)));
		}
		for (int i = 0; i < out.size(); i++) {
			//System.out.println(out.get(i));
		}

	}

}
