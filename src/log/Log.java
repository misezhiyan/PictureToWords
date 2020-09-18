package log;

import java.io.File;

import util.FileUtil;

public class Log {

	private String debug = "";
	private String logFilePath = "C:\\Users\\misez\\Desktop\\图片\\debug.log";

	public void debug(Object log) {
		if (log.getClass() == String.class) {
			debug += (String) log + "\r\n";
		} else {
			debug += log.toString() + "\r\n";
		}
	}

	public void debugOut() {
		System.out.println(debug);
	}

	public void debugOutFile() {

		File logFile = new File(logFilePath);
		if (!logFile.exists()) {
			logFile.getParentFile().mkdirs();
			try {
				logFile.createNewFile();
			} catch (Exception e) {
				System.out.println("日志文件创建失败");
				e.printStackTrace();
				return;
			}
		}

		try {
			FileUtil.appendToFile(logFilePath, debug);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("日志文件写入失败");
		}
	}

}
