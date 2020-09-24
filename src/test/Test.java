package test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import log.Log;

public class Test {
	public static void main(String[] args) {

		// 查看图片rgb 数值与 r, g, b 数值
		checkRGB();

		// 查看二进制运算
		// checkBinary();

		// 查看二位数组
		// checkErweiArr();
	}

	private static void checkRGB() {

		String src = "C:\\Users\\misez\\Desktop\\图片\\辽B5942B.jpg";

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

		int minX = bufferedImage.getMinX();
		int minY = bufferedImage.getMinY();

		String all = "";
		for (int x = minX; x < width; x++) {
			String line = null;
			for (int y = minY; y < height; y++) {
				// 像素点
				int pixel = bufferedImage.getRGB(x, y);
				if (null == line)
					line = pixel + "\t" + ((pixel & 0xFF0000) >> 16) + "\t" + ((pixel & 0xFF00) >> 8) + "\t" + (pixel & 0xFF) + "\t";
				else
					line += "\t" + pixel + "\t" + ((pixel & 0xFF0000) >> 16) + "\t" + ((pixel & 0xFF00) >> 8) + "\t" + (pixel & 0xFF) + "\t";
			}
			all += line + "\r\n";
		}

		Log log = new Log();
		log.debug(all);
		log.debugOutFile();
	}

	private static void checkErweiArr() {
		// int[][] arr = new int[10][11];
		// for (int i = 0; i < arr.length; i++)
		// for (int j = 0; j < arr.length; j++)
		// System.out.println(arr[i][j]);
		boolean[][] arr = new boolean[10][11];
		for (int i = 0; i < arr.length; i++)
			for (int j = 0; j < arr.length; j++)
				System.out.println(arr[i][j]);
	}

	private static void checkBinary() {

		int rgb = -16570290;
		System.out.println(Integer.toBinaryString(rgb));
		int and = 0xff0000;
		System.out.println(Integer.toBinaryString(and));
		and = 0xff00;
		System.out.println(Integer.toBinaryString(and));
		and = 0xff;
		System.out.println(Integer.toBinaryString(and));

		int r = (rgb & 0xFF0000) >> 16;
		System.out.println(r);
		int g = (rgb & 0xFF00) >> 8;
		System.out.println(g);
		int b = rgb & 0xFF;
		System.out.println(b);

		int abc = r << 16;
		System.out.println(abc);
		abc = r & 0xFF0000;
		System.out.println(abc);
	}
}
