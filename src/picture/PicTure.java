package picture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;

import javax.imageio.ImageIO;

import util.MathUtil;

public class PicTure {

	// 原图像
	private int[][] pointImg;
	// 高斯模糊图像
	private int[][] pointImg_gaosi;

	public PicTure(int[][] pointImg) {
		this.pointImg = pointImg;
	}

	public boolean saveNewPicture(String realFilePath) {

		return saveNewPicture(realFilePath, this.pointImg);
	}

	private boolean saveNewPicture(String realFilePath, int[][] pointImg) {

		File file = new File(realFilePath);
		if (!file.exists()) {
			File pacha = file.getParentFile();
			if (!pacha.exists()) {
				pacha.mkdirs();
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		if (null == pointImg)
			return false;

		BufferedImage image = new BufferedImage(pointImg.length, pointImg[0].length, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < pointImg.length; x++) {
			for (int y = 0; y < pointImg[y].length; y++) {
				try {
					image.setRGB(x, y, pointImg[x][y]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		OutputStream output = null;
		try {
			output = new FileOutputStream(new File(realFilePath));
			ImageIO.write(image, "jpg", output);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (null != output) {
				try {
					output.flush();
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}

		return true;
	}

	public boolean saveGaoSiMohuPicture(String realFilePath) throws Exception {

		if (null == pointImg)
			return false;

		int[][] huidu = getHuiDu();

		// saveNewPicture(realFilePath, huidu);
		// saveNewPicture(realFilePath, pointImg);
		// return false;

		return saveGaoSiMohuPicture(realFilePath, huidu, 10);
	}

	private boolean saveGaoSiMohuPicture(String realFilePath, int[][] huidu, int windowRadius) throws Exception {

		if (null == huidu)
			return false;

		int[][] pointImg_gaosi = getGaoSiMoHuPic(realFilePath, huidu, windowRadius);

		return saveNewPicture(realFilePath, pointImg_gaosi);
	}

	private int[][] getGaoSiMoHuPic(String realFilePath, int[][] huidu, int windowRadius) throws Exception {

		// int[][] pointImg_gaosi = new int[huidu.length][huidu[0].length];
		pointImg_gaosi = new int[huidu.length][huidu[0].length];
		BigDecimal[][] window_gaosi = getGaosiWindow(windowRadius);

		for (int x = 0; x < huidu.length; x++) {
			for (int y = 0; y < huidu[x].length; y++) {

				if (x < windowRadius || y < windowRadius || x + windowRadius * 2 + 1 >= huidu.length || y + windowRadius * 2 + 1 >= huidu[x].length) {
					pointImg_gaosi[x][y] = huidu[x][y];
					continue;
				}

				int pixel = huidu[x][y];
				int a = (pixel & 0xff000000) >> 24;
				// int r = (pixel & 0xff0000) >> 16;
				// int g = (pixel & 0xff00) >> 8;
				// int b = pixel & 0xff;

				// BigDecimal mohuPixel = new BigDecimal(0);
				BigDecimal rSum = new BigDecimal(0), gSum = new BigDecimal(0), bSum = new BigDecimal(0);
				for (int winY = 0; winY < window_gaosi.length; winY++) {
					for (int winX = 0; winX < window_gaosi[winY].length; winX++) {

						// 实际坐标
						int realX = x - windowRadius + winX;
						int realY = y - windowRadius + winY;

						// 像素点
						int pixelXY = huidu[realX][realY];
						// 权重
						BigDecimal quanzhong = window_gaosi[winX][winY];
						// mohuPixel = mohuPixel.add(quanzhong.multiply(new BigDecimal(pixel)));

						int rXY = (pixelXY & 0xff0000) >> 16;
						int gXY = (pixelXY & 0xff00) >> 8;
						int bXY = pixelXY & 0xff;

						rSum = rSum.add(quanzhong.multiply(new BigDecimal(rXY)));
						gSum = gSum.add(quanzhong.multiply(new BigDecimal(gXY)));
						bSum = bSum.add(quanzhong.multiply(new BigDecimal(bXY)));

						// System.out.println(quanzhong);
					}
				}

				// System.out.println(mohuPixel);

				pointImg_gaosi[x][y] = (a << 24) + (rSum.intValue() << 16) + (gSum.intValue() << 8) + (bSum.intValue());
			}
		}

		return pointImg_gaosi;
	}

	private static BigDecimal[][] getGaosiWindow(int windowRadius) throws Exception {

		// BigDecimal σ = new BigDecimal(1.5);

		BigDecimal[][] window_gaosi = new BigDecimal[2 * windowRadius + 1][2 * windowRadius + 1];

		BigDecimal sum = new BigDecimal(0);
		for (int y = 0; y < window_gaosi.length; y++) {
			for (int x = 0; x < window_gaosi[y].length; x++) {
				BigDecimal zhengtaifenbuDaoshu = MathUtil.zhengtaifenbuDaoshu(x - windowRadius, y - windowRadius, 1.5);
				window_gaosi[x][y] = zhengtaifenbuDaoshu;

				sum = sum.add(zhengtaifenbuDaoshu);
			}
		}

		for (int y = 0; y < window_gaosi.length; y++) {
			for (int x = 0; x < window_gaosi[y].length; x++) {
				window_gaosi[x][y] = window_gaosi[x][y].divide(sum, 10, BigDecimal.ROUND_HALF_UP);
			}
		}

		return window_gaosi;
	}

	private int[][] getHuiDu() {

		int[][] huidu = new int[pointImg.length][pointImg[0].length];

		for (int x = 0; x < pointImg.length; x++) {
			for (int y = 0; y < pointImg[x].length; y++) {

				int pixel = pointImg[x][y];

				int a = (pixel & 0xff000000) >> 24;

				int r = (pixel & 0xff0000) >> 16;
				int g = (pixel & 0xff00) >> 8;
				int b = pixel & 0xff;

				int rgb_average = (r + g + b) / 3;
				// int rgb_average = r > g ? r : g;
				// rgb_average = rgb_average > b ? rgb_average : b;

				// int rgb = a << 24 + rgb_average << 16 + rgb_average << 8 + rgb_average;
				int rgb = (a << 24) + (rgb_average << 16) + (rgb_average << 8) + rgb_average;

				// System.out.println(Integer.toBinaryString(pixel));
				// System.out.println(Integer.toBinaryString(r));
				// System.out.println(Integer.toBinaryString(g));
				// System.out.println(Integer.toBinaryString(b));
				//
				// System.out.println(Integer.toBinaryString(rgb_average << 16));
				// System.out.println(Integer.toBinaryString(rgb_average << 8));
				// System.out.println(Integer.toBinaryString(rgb_average));
				//
				// System.out.println(Integer.toBinaryString(rgb));
				//
				// System.out.println();

				huidu[x][y] = rgb;
			}
		}

		return huidu;
	}

}
