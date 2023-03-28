package picture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.imageio.ImageIO;

import log.Log;
import util.PicUtil;

public class Main {

	// static String src = "C:\\Users\\misez\\Desktop\\图片\\辽B5942B.jpg";
	static String cut = "C:\\Users\\misez\\Desktop\\图片\\辽B5942B_CUT.jpg";
	static String cut2 = "C:\\Users\\misez\\Desktop\\图片\\辽B5942B_CUT51.jpg";
	static String cut3 = "C:\\Users\\misez\\Desktop\\图片\\辽B5942B_CUT247.jpg";


	static String src = "C:\\Users\\kimmy\\Desktop\\图像识别\\识别.jpg";
	static String outFileName = "C:\\Users\\kimmy\\Desktop\\图像识别\\识别\\" + new SimpleDateFormat("yyyyMMddHHmmsss").format(Calendar.getInstance().getTime()) + ".jpg";

	public static void main(String[] args) throws IOException {

		Log log = new Log();

		// File file = new File("C:\\Users\\misez\\Desktop\\图片\\test.jpg");
		File file = new File(src);

		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();

		log.debug("宽:" + width);
		log.debug("高:" + height);

		// 查找区域
		getLocation(bufferedImage, width, height);

		BufferedImage bufferedImage0 = bufferedImage.getSubimage(68, 190, 413, 170);
		PicUtil.saveImgToFile(bufferedImage0, outFileName);


		// BufferedImage bufferedImage2 = bufferedImage.getSubimage(0, 0, width, 51);
		// OutputStream output2 = new FileOutputStream(new File(cut2));
		// ImageIO.write(bufferedImage2, "jpg", output2);
		//
		// BufferedImage bufferedImage3 = bufferedImage.getSubimage(0, 247, width, (height - 247));
		// OutputStream output3 = new FileOutputStream(new File(cut3));
		// ImageIO.write(bufferedImage3, "jpg", output3);

		// log.debugOutFile();
	}

	private static void getLocation(BufferedImage bufferedImage, int width, int height) {

		Log log = new Log();

		log.debug("宽:" + width);
		log.debug("高:" + height);

		// 图片左侧 1/8 往右, 右侧1/8 往左
		int widthLeftLine = width * 1 / 8;
		int widthRightLine = width * 7 / 8;
		// 图片从上方算 7/8 往上, 1/3 以下
		int heightTopLine = height * 7 / 8;
		int heightDownLine = height * 2 / 5;
		log.debug("heightTopLine:" + heightTopLine);
		log.debug("heightDownLine:" + heightDownLine);

		int minX = bufferedImage.getMinX();
		int minY = bufferedImage.getMinY();

		int length = 10;

		int[] rgb = new int[3];
		for (int x = minX; x < width; x++) {
			for (int y = minY; y < height; y++) {

				// 获取包含这个像素的颜色信息的值, int型
				int pixel = bufferedImage.getRGB(x, y);
				// log.debug(pixel);

				rgb[0] = (pixel & 0xff0000) >> 16; // r
				rgb[1] = (pixel & 0xff00) >> 8; // g
				rgb[2] = (pixel & 0xff); // b

				// log.debug("rgb[0]:" + rgb[0]);
				// log.debug("rgb[1]:" + rgb[1]);
				// log.debug("rgb[2]:" + rgb[2]);

				// 图片左侧 1/8 往右, 右侧1/8 往左
				if ((x - minX) < widthLeftLine || (x - minX) > widthRightLine)
					continue;
				// 图片上方 1/8 往上, 1/3 以下
				if ((y - minY) < heightDownLine || (y - minY) > heightTopLine)
					continue;

				if (x + length < width) {
					boolean yes = false;
					for (int i = 1; i < length; i++) {
						int pixelLineNext = bufferedImage.getRGB(x + i, y);
						if (rgb[0] != ((pixelLineNext & 0xff0000) >> 16)) {
							break;
						}
						yes = true;
					}

					if (yes) {
						log.debug("坐标:(" + x + ", " + y + ")");
					}
				}
			}
		}

		log.debugOutFile();
	}
}
