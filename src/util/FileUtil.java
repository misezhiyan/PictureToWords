package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	public static void main(String[] args) throws Exception {
		fileListInDir("C:\\Users\\misez\\Desktop\\翻译\\文件");
	}

	// 读取文件夹下文件列表
	public static List<File> fileListInDir(String dirPath) throws Exception {

		List<File> filePathList = new ArrayList<File>();

		File dir = new File(dirPath);
		if (!dir.exists()) {
			throw new Exception("文件夹不存在");
		}
		if (!dir.isDirectory()) {
			throw new Exception("选中文件不是文件夹");
		}

		File[] listFiles = dir.listFiles();

		for (int i = 0; i < listFiles.length; i++) {
			File file = listFiles[i];
			if (file.isFile()) {
				filePathList.add(file);
			}
		}

		return filePathList;
	}

	// 读取文件夹下文件路径列表
	public static List<String> filePathListInDir(String dirPath) throws Exception {

		List<String> filePathList = new ArrayList<String>();

		File dir = new File(dirPath);
		if (!dir.exists()) {
			throw new Exception("文件夹不存在");
		}
		if (!dir.isDirectory()) {
			throw new Exception("选中文件不是文件夹");
		}

		File[] listFiles = dir.listFiles();

		for (int i = 0; i < listFiles.length; i++) {
			File file = listFiles[i];
			if (file.isFile()) {
				String path = file.getPath();
				filePathList.add(path);
			}
		}

		return filePathList;
	}

	public static void writeIntoFileWithDir(String fileRealPath, String content) throws Exception {
		File file = new File(fileRealPath);
		File file_package = file.getParentFile();
		if (!file_package.exists())
			file_package.mkdirs();
		writeIntoFileByPath(fileRealPath, content);
	}

	public static void appendToFile(String fileRealPath, String content) throws Exception {

		String file_content = fileReadToStringByPath(fileRealPath);

		// file_content += "\r\n";
		// file_content += "\r\n";

		if (null == file_content)
			file_content = content;
		else
			file_content += content;

		writeIntoFileByPath(fileRealPath, file_content);
	}

	public static String fileReadToStringByPath(String fileRealPath) throws Exception {

		File file = new File(fileRealPath);
		if (!file.exists())
			throw new Exception("文件不存在");

		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			in.read(filecontent);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			in.close();
		}

		String encoding = EncodingUtil.getFileEncodingType(fileRealPath);

		String result = new String(filecontent, encoding);
		return result;
	}

	public static String fileReadToString(File file) throws Exception {

		if (!file.exists())
			throw new Exception("文件不存在");

		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			in.read(filecontent);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			in.close();
		}

		// String encoding = EncodingUtil.getFileEncodingType(file.getPath());
		// System.out.println("encoding:" + encoding);
		// String result = new String(filecontent, encoding);

		String result = new String(filecontent);

		// System.out.println("result:" + result);

		return result;
	}

	public static boolean writeIntoFile(String filePath, String fileName, String fileType, String content) throws Exception {

		if (StringUtil.isEmpty(filePath) || StringUtil.isEmpty(fileName) || StringUtil.isEmpty(fileType))
			throw new Exception("can not be null");

		String fileFullName = fileName.trim() + "." + fileType.trim();
		return writeIntoFile(filePath, fileFullName, content);
	}

	private static boolean writeIntoFile(String filePath, String fileFullName, String content) throws Exception {

		if (StringUtil.isEmpty(filePath) || StringUtil.isEmpty(fileFullName))
			throw new Exception("can not be null");
		String fileRealPath = filePath.trim() + fileFullName.trim();
		File file_package = new File(filePath);
		if (!file_package.exists())
			file_package.mkdirs();

		return writeIntoFileByPath(fileRealPath, content);
	}

	/*
	 * ***** // 编码格式问题 private static boolean writeIntoFile(String fileRealPath, String content) throws Exception { if (empty(fileRealPath)) throw new Exception("can not be null");
	 * 
	 * File file = new File(fileRealPath); if (!file.exists()) { file.createNewFile(); } FileWriter fileWriter = new FileWriter(file); try { fileWriter.write(content); fileWriter.flush(); } catch (Exception e) { e.printStackTrace(); } finally { fileWriter.close(); }
	 * 
	 * return true; }
	 */
	private static boolean writeIntoFileByPath(String fileRealPath, String content) throws Exception {
		if (StringUtil.isEmpty(fileRealPath))
			throw new Exception("can not be null");

		File file = new File(fileRealPath);
		if (!file.exists()) {
			File pkg = file.getParentFile();
			pkg.mkdirs();
			file.createNewFile();
		}

		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fileRealPath), "UTF-8");
		out.write(content);
		out.flush();
		out.close();

		return true;
	}

	public static boolean writeIntoFile(File file, String content) throws Exception {

		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file.getPath()), "UTF-8");
		out.write(content);
		out.flush();
		out.close();

		return true;
	}

}
