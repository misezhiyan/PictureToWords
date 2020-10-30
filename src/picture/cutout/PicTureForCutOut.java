package picture.cutout;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Arrays;

import javax.imageio.ImageIO;

import util.MathUtil;

public class PicTureForCutOut {

	// 原图像
	private int[][] pointImg;

	public PicTureForCutOut(int[][] pointImg) {
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

		// int[][] huidu = getHuiDuPic();
		//
		// int[][] blur = getBlurPic(huidu, 1);
		//
		// // 中值滤波
		// int[][] median = getMedianPic(blur);
		//
		// // 锐化窗口
		// BigDecimal[][] sharpWindow = getSharpWindow();
		// int[][] juanJi = getJuanJiPic(median, sharpWindow);
		//
		// // 二值化
		// int[][] erzhihua = getErzhihuaPic(juanJi);
		//
		// return saveNewPicture(tmp, erzhihua);

		int[][] huidu = getHuiDuPic();
		// 二值化
		int[][] erzhihua = getErzhihuaPic(huidu);

		return saveNewPicture(tmp, erzhihua);
	}

	// 图像二值化
	private int[][] getErzhihuaPic(int[][] src) {

		int erzhiLine = 120;

		int[][] dst = new int[src.length][src[0].length];

		int a = (src[0][0] & 0xff000000) >> 24;

		for (int x = 0; x < src.length; x++) {
			for (int y = 0; y < src[x].length; y++) {
				int pixel = src[x][y];

				int b = pixel & 0xff;

				if (b > erzhiLine)
					dst[x][y] = (a << 24) + (255 << 16) + (255 << 8) + 255;
				else
					dst[x][y] = a << 24;
			}
		}

		return dst;
	}

	public boolean saveExpendAndCorrodePicture(String tmp) throws Exception {

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

	// 膨胀和腐蚀
	private int[][] getExpendAndCorrodePic(int[][] src) {

		int radius = 1;

		// 腐蚀
		int[][] dst = corrode(src, radius);
		// 膨胀
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

				boolean corrode = false;
				for (int windowX = 0; windowX < window.length; windowX++) {
					if (corrode)
						break;
					for (int windowY = 0; windowY < window[windowX].length; windowY++) {

						int realX = x - radius + windowX;
						int realY = y - radius + windowY;

						int pixel = src[realX][realY];

						int b = pixel & 0Xff;

						if (b == 255) {
							corrode = true;
							break;
						}
					}
				}
				if (corrode)
					dst[x][y] = (a << 24) + (0 << 16) + (0 << 8) + 0;
				else
					dst[x][y] = (a << 24) + (255 << 16) + (255 << 8) + 255;
			}
		}

		return dst;
	}

	// 膨胀
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

	// 查找轮廓
	public boolean saveOutLinePicture(String tmp) throws Exception {

		// int[][] huidu = getHuiDuPic();
		//
		// int[][] blur = getBlurPic(huidu, 1);
		//
		// int[][] median = getMedianPic(blur);
		//
		// // 锐化窗口
		// BigDecimal[][] sharpWindow = getSharpWindow();
		// int[][] juanJi = getJuanJiPic(median, sharpWindow);
		//
		// // 二值化
		// int[][] erzhihua = getErzhihuaPic(juanJi);
		//
		// // 扩展和腐蚀
		// int[][] expendAndCorrode = getExpendAndCorrodePic(erzhihua);
		//
		// // 查找轮廓
		// int[][] outLine = getOutLinePic(expendAndCorrode);
		//
		// return saveNewPicture(tmp, outLine);

		int[][] huidu = getHuiDuPic();
		// 二值化
		int[][] erzhihua = getErzhihuaPic(huidu);
		// 扩展和腐蚀
		int[][] expendAndCorrode = getExpendAndCorrodePic(erzhihua);
		// 查找轮廓
		int[][] outLine = getOutLinePic(expendAndCorrode);

		return saveNewPicture(tmp, erzhihua);
	}

	// 查找轮廓
	private int[][] getOutLinePic(int[][] src) {

		// 1.轮廓提取法
		// 2.边界跟踪法
		// 3.区域增长法
		// 4.区域分裂合并法

		// 暂时只使用 轮廓提取法

		int[][] dst = new int[src.length][src[0].length];

		int pixel00 = src[0][0];
		int a = (pixel00 & 0Xff000000) >> 24;
		int white = (a << 24) + (255 << 16) + (255 << 8) + 255;
		int black = (a << 24);

		for (int x = 0; x < src.length; x++) {
			for (int y = 0; y < src[x].length; y++) {

				int pixel = src[x][y];

				// int a = (pixel & 0Xff000000) >> 24;
				// int r = (pixel&0Xff0000) >> 16;
				// int g = (pixel&0Xff00) >> 8;
				// int b = pixel & 0Xff;

				if (pixel == white) {
					dst[x][y] = pixel;
					continue;
				}

				int leftX = x - 1;
				int rightX = x + 1;
				int upY = y + 1;
				int downY = y - 1;

				boolean isBlack_lea = true;

				if (leftX > 0 && upY < src[x].length) {
					int left_up = src[leftX][upY];
					isBlack_lea = isBlack_lea && (left_up == black);
				}

				if (leftX > 0) {
					int left = src[leftX][y];
					isBlack_lea = isBlack_lea && (left == black);
				}

				if (leftX > 0 && downY > 0) {
					int left_down = src[leftX][downY];
					isBlack_lea = isBlack_lea && (left_down == black);
				}

				if (upY < src[x].length) {
					int up = src[x][upY];
					isBlack_lea = isBlack_lea && (up == black);
				}

				if (downY > 0) {
					int down = src[x][downY];
					isBlack_lea = isBlack_lea && (down == black);
				}

				if (rightX < src.length && upY < src[x].length) {
					int right_up = src[rightX][upY];
					isBlack_lea = isBlack_lea && (right_up == black);
				}

				if (rightX < src.length) {
					int right = src[rightX][y];
					isBlack_lea = isBlack_lea && (right == black);
				}

				if (rightX < src.length && downY > 0) {
					int right_down = src[rightX][downY];
					isBlack_lea = isBlack_lea && (right_down == black);
				}

				if (isBlack_lea) {
					dst[x][y] = white;
				}
			}
		}

		return dst;
	}

	public static void main(String[] args) {

		String src = "C:\\Users\\misez\\Desktop\\图片\\晋000888.jpg";
		// String src = "C:\\Users\\misez\\Desktop\\图片\\辽B5942B.jpg";
		// String src = "C:\\Users\\misez\\Desktop\\图片\\鲁B2N506.jpeg";
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

		System.out.println("width:" + width);
		System.out.println("height:" + height);

		int minX = bufferedImage.getMinX();
		int minY = bufferedImage.getMinY();

		int[][] pointImg = new int[width][height];
		for (int x = minX; x < width; x++) {
			for (int y = minY; y < height; y++) {
				// 像素点
				int pixel = bufferedImage.getRGB(x, y);
				pointImg[x][y] = pixel;
			}
		}

		String tmp = "C:\\Users\\misez\\Desktop\\图片\\高斯模糊\\dst\\" + file.getName();
		PicTureForCutOut picture = new PicTureForCutOut(pointImg);
		// picture.saveNewPicture(tmp);
		try {
			// 制作高斯模糊二位数组
			// picture.saveGaoSiMohuPicture(tmp);
			// 制作中值滤波二位数组
			// picture.saveMedianPicture(tmp);
			// 边缘检测
			// picture.saveSharpPicture(tmp);
			// 二值化
			// picture.saveTwoValue(tmp);
			// 进行膨胀和腐蚀
			// picture.saveExpendAndCorrodePicture(tmp);
			// 查找轮廓
			picture.saveOutLinePicture(tmp);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
