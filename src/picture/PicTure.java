package picture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Arrays;

import javax.imageio.ImageIO;

import util.MathUtil;

public class PicTure {

	// 原图像
	private int[][] pointImg;

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

		int[][] huidu = getHuiDuPic();

		// saveNewPicture(realFilePath, huidu);
		// saveNewPicture(realFilePath, pointImg);
		// return false;

		return saveGaoSiMohuPicture(realFilePath, huidu, 1);
	}

	private boolean saveGaoSiMohuPicture(String realFilePath, int[][] huidu, int windowRadius) throws Exception {

		if (null == huidu)
			return false;

		int[][] pointImg_gaosi = getBlurPic(huidu, windowRadius);

		return saveNewPicture(realFilePath, pointImg_gaosi);
	}

	// 图像模糊
	private int[][] getBlurPic(int[][] huidu, int windowRadius) throws Exception {

		// 高斯模糊矩阵
		BigDecimal[][] gaosiWindow = getGaosiWindow(windowRadius);

		// 卷积
		return getJuanJiPic(huidu, gaosiWindow);
	}

	// 对像素点进行卷积操作
	private int[][] getJuanJiPic(int[][] src, BigDecimal[][] juanjiWindow) throws Exception {

		int width = juanjiWindow.length;
		int height = juanjiWindow[0].length;

		if (width != height)
			throw new Exception("卷积窗口不是正方形");

		int windowRadius = (width - 1) / 2;

		int[][] dst = new int[src.length][src[0].length];

		for (int x = 0; x < src.length; x++) {
			for (int y = 0; y < src[x].length; y++) {

				if (x < windowRadius || y < windowRadius || x + windowRadius * 2 + 1 >= src.length || y + windowRadius * 2 + 1 >= src[x].length) {
					dst[x][y] = src[x][y];
					continue;
				}

				int pixel = src[x][y];
				int a = (pixel & 0xff000000) >> 24;

				BigDecimal rSum = new BigDecimal(0), gSum = new BigDecimal(0), bSum = new BigDecimal(0);
				for (int winY = 0; winY < juanjiWindow.length; winY++) {
					for (int winX = 0; winX < juanjiWindow[winY].length; winX++) {

						// 实际坐标
						int realX = x - windowRadius + winX;
						int realY = y - windowRadius + winY;

						// 像素点
						int pixelXY = src[realX][realY];
						// 权重
						BigDecimal quanzhong = juanjiWindow[winX][winY];

						int rXY = (pixelXY & 0xff0000) >> 16;
						int gXY = (pixelXY & 0xff00) >> 8;
						int bXY = pixelXY & 0xff;

						rSum = rSum.add(quanzhong.multiply(new BigDecimal(rXY)));
						gSum = gSum.add(quanzhong.multiply(new BigDecimal(gXY)));
						bSum = bSum.add(quanzhong.multiply(new BigDecimal(bXY)));
					}
				}

				dst[x][y] = (a << 24) + (rSum.intValue() << 16) + (gSum.intValue() << 8) + (bSum.intValue());
			}
		}

		return dst;
	}

	// 高斯模糊矩阵
	private static BigDecimal[][] getGaosiWindow(int windowRadius) throws Exception {

		BigDecimal σ = new BigDecimal(1.5);

		BigDecimal[][] gaosiBlureWindow = new BigDecimal[2 * windowRadius + 1][2 * windowRadius + 1];

		BigDecimal sum = new BigDecimal(0);
		for (int y = 0; y < gaosiBlureWindow.length; y++) {
			for (int x = 0; x < gaosiBlureWindow[y].length; x++) {
				BigDecimal zhengtaifenbuDaoshu = MathUtil.zhengtaifenbuDaoshu(x - windowRadius, y - windowRadius, σ.doubleValue());
				gaosiBlureWindow[x][y] = zhengtaifenbuDaoshu;

				sum = sum.add(zhengtaifenbuDaoshu);
			}
		}

		for (int y = 0; y < gaosiBlureWindow.length; y++) {
			for (int x = 0; x < gaosiBlureWindow[y].length; x++) {
				gaosiBlureWindow[x][y] = gaosiBlureWindow[x][y].divide(sum, 10, BigDecimal.ROUND_HALF_UP);
			}
		}

		return gaosiBlureWindow;
	}

	private int[][] getHuiDuPic() {

		int[][] huidu = new int[pointImg.length][pointImg[0].length];

		for (int x = 0; x < pointImg.length; x++) {
			for (int y = 0; y < pointImg[x].length; y++) {

				int pixel = pointImg[x][y];

				int a = (pixel & 0xff000000) >> 24;

				int r = (pixel & 0xff0000) >> 16;
				int g = (pixel & 0xff00) >> 8;
				int b = pixel & 0xff;

				int rgb_average = (r + g + b) / 3;
				int rgb = (a << 24) + (rgb_average << 16) + (rgb_average << 8) + rgb_average;

				huidu[x][y] = rgb;
			}
		}

		return huidu;
	}

	// 图像锐化
	public boolean saveSharpPicture(String tmp) throws Exception {

		//
		// cop.filter(image, bimg);

		int[][] huidu = getHuiDuPic();

		int[][] blur = getBlurPic(huidu, 1);

		int[][] median = getMedianPic(blur);
		// float[] elements = { 0.0f, -1.0f, 0.0f, -1.0f, 5.0f, -1.0f, 0.0f, -1.0f, 0, 0f };
		// BufferedImage bimg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		// Kernel kernel = new Kernel(3, 3, elements);
		// ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

		// 锐化窗口
		BigDecimal[][] sharpWindow = getSharpWindow();
		int[][] juanJi = getJuanJiPic(median, sharpWindow);

		return saveNewPicture(tmp, juanJi);
	}

	private BigDecimal[][] getSharpWindow() {

		BigDecimal[][] sharpWindow = new BigDecimal[3][3];

		sharpWindow[0] = new BigDecimal[] { new BigDecimal(0), new BigDecimal(-1), new BigDecimal(0) };
		sharpWindow[1] = new BigDecimal[] { new BigDecimal(-1), new BigDecimal(4), new BigDecimal(-1) };
		sharpWindow[2] = new BigDecimal[] { new BigDecimal(0), new BigDecimal(-1), new BigDecimal(0) };

		return sharpWindow;
	}

	// 中值滤波
	public boolean saveMedianPicture(String tmp) throws Exception {

		int[][] huidu = getHuiDuPic();

		int[][] blur = getBlurPic(huidu, 1);

		int[][] median = getMedianPic(blur);

		// int count = 0;
		// while (count++ < 3) {
		// median = getMedianPic(median);
		// }

		return saveNewPicture(tmp, median);
	}

	// 中值滤波
	private int[][] getMedianPic(int[][] src) {

		int[][] dst = new int[src.length][src[0].length];

		int radius = 1;

		for (int x = 0; x < src.length; x++) {
			for (int y = 0; y < src[x].length; y++) {

				if (x - radius < 0 || y - radius < 0 || x + radius >= dst.length || y + radius >= dst[y].length) {

					dst[x][y] = src[x][y];
					continue;
				}

				// 获取窗口
				int[][] medianWindow = getMedianWindow(radius);

				// 窗口取模
				for (int i = 0; i < medianWindow.length; i++) {
					for (int j = 0; j < medianWindow.length; j++) {

						int realX = x - radius + i;
						int realY = y - radius + j;

						medianWindow[i][j] = src[realX][realY];
					}
				}

				dst[x][y] = getMedianPoint(medianWindow);
			}
		}

		return dst;
	}

	private int getMedianPoint(int[][] medianWindow) {

		int[] arr = new int[medianWindow.length * medianWindow[0].length];

		int index = 0;
		for (int i = 0; i < medianWindow.length; i++)
			for (int j = 0; j < medianWindow[i].length; j++) {
				arr[index++] = medianWindow[i][j];
			}

		Arrays.sort(arr);

		return arr[arr.length >> 1];
	}

	// 中值滤波取模窗口
	private int[][] getMedianWindow(int radius) {

		int WindowLength = (radius << 1) + 1;

		return new int[WindowLength][WindowLength];
	}

	// 二值化
	public boolean saveTwoValue(String tmp) throws Exception {

		int[][] huidu = getHuiDuPic();

		int[][] blur = getBlurPic(huidu, 1);

		int[][] median = getMedianPic(blur);

		// 锐化窗口
		BigDecimal[][] sharpWindow = getSharpWindow();
		int[][] juanJi = getJuanJiPic(median, sharpWindow);

		// 二值化
		int[][] erzhihua = getErzhihuaPic(juanJi);

		return saveNewPicture(tmp, erzhihua);
	}

	// 图像二值化
	private int[][] getErzhihuaPic(int[][] src) {

		int[][] dst = new int[src.length][src[0].length];

		int a = (src[0][0] & 0xff000000) >> 24;

		for (int x = 0; x < src.length; x++) {
			for (int y = 0; y < src[x].length; y++) {
				int pixel = src[x][y];

				int b = pixel & 0xff;

				if (b > 150)
					dst[x][y] = (a << 24) + (255 << 16) + (255 << 8) + 255;
				else
					dst[x][y] = a << 24;
			}
		}

		return dst;
	}

	public boolean saveExpendAndCorrode(String tmp) throws Exception {

		int[][] huidu = getHuiDuPic();

		int[][] blur = getBlurPic(huidu, 1);

		int[][] median = getMedianPic(blur);

		// 锐化窗口
		BigDecimal[][] sharpWindow = getSharpWindow();
		int[][] juanJi = getJuanJiPic(median, sharpWindow);

		// 二值化
		int[][] erzhihua = getErzhihuaPic(juanJi);

		// 扩展和腐蚀
		int[][] expendAndCorrode = getExpendAndCorrodePic(erzhihua);

		return saveNewPicture(tmp, expendAndCorrode);
	}

	// 扩展和腐蚀
	private int[][] getExpendAndCorrodePic(int[][] src) {

		int radius = 1;

		// 腐蚀
		int[][] dst = corrode(src, radius);
		// 扩展
		dst = expend(dst, radius);

		return dst;
	}

	// 腐蚀
	private int[][] corrode(int[][] src, int radius) {

		int[][] dst = new int[src.length][src[0].length];

		int[][] window = getExpendAndCorrodeWindow(radius);

		for (int x = 0; x < src.length; x++) {
			for (int y = 0; y < src[x].length; y++) {

				if (x - radius < 0 || y - radius < 0 || x + radius >= src.length || y + radius >= src[x].length) {
					dst[x][y] = src[x][y];
					continue;
				}

				int a = (src[0][0] & 0Xff000000) >> 24;

				boolean expend = false;
				for (int windowX = 0; windowX < window.length; windowX++) {
					if (expend)
						break;
					for (int windowY = 0; windowY < window[windowX].length; windowY++) {

						int realX = x - radius + windowX;
						int realY = y - radius + windowY;

						int pixel = src[realX][realY];

						int b = pixel & 0Xff;

						if (b == 255) {
							expend = true;
							break;
						}
					}
				}
				if (expend)
					dst[x][y] = (a << 24) + (0 << 16) + (0 << 8) + 0;
				else
					dst[x][y] = (a << 24) + (255 << 16) + (255 << 8) + 255;
			}
		}

		return dst;
	}

	// 扩展
	private int[][] expend(int[][] src, int radius) {

		int[][] dst = new int[src.length][src[0].length];

		int[][] window = getExpendAndCorrodeWindow(radius);

		for (int x = 0; x < src.length; x++) {
			for (int y = 0; y < src[x].length; y++) {

				if (x - radius < 0 || y - radius < 0 || x + radius >= src.length || y + radius >= src[x].length) {
					dst[x][y] = src[x][y];
					continue;
				}

				int a = (src[0][0] & 0Xff000000) >> 24;

				boolean expend = false;
				for (int windowX = 0; windowX < window.length; windowX++) {
					if (expend)
						break;
					for (int windowY = 0; windowY < window[windowX].length; windowY++) {

						int realX = x - radius + windowX;
						int realY = y - radius + windowY;

						int pixel = src[realX][realY];

						int b = pixel & 0Xff;

						if (b == 255) {
							expend = true;
							break;
						}
					}
				}
				if (expend)
					dst[x][y] = (a << 24) + (255 << 16) + (255 << 8) + 255;
				else
					dst[x][y] = (a << 24) + (0 << 16) + (0 << 8) + 0;
			}
		}

		return dst;
	}

	private int[][] getExpendAndCorrodeWindow(int radius) {

		int WindowLength = (radius << 1) + 1;

		return new int[WindowLength][WindowLength];
	}

}
