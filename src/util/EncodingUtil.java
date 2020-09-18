package util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class EncodingUtil {
	
	public static String getFileEncodingType(String file_real_path) throws IOException {
		
		BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file_real_path));  
		int p = (bin.read() << 8) + bin.read();  
		String code = null;  
		
		bin.close();
		
		switch (p) {  
		    case 0xefbb:  
		        code = "UTF-8";  
		    break;  
		case 0xfffe:  
		    code = "Unicode";  
		    break;  
		case 0xfeff:  
		    code = "UTF-16BE";  
		    break;  
		default:  
		    code = "GBK";  
		}  
		  
		return code;  
	}
}
