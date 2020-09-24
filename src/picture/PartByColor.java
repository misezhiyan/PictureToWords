package picture;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class PartByColor {

	static String src = "C:\\Users\\misez\\Desktop\\图片\\辽B5942B.jpg";
	static String dst = "C:\\Users\\misez\\Desktop\\图片\\part\\辽B5942B_part.jpg";
	static String tmpPath = "C:\\Users\\misez\\Desktop\\图片\\part\\tmp\\";
	static int secha = 28;

	static int[][] pointImg;
	static boolean[][] pointChoosed;

	public static void main(String[] args) throws IOException {

		// Log log = new Log();

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
		System.out.println("面积:" + width * height);

		// 像素点二维数组
		pointImg = new int[width][height];
		pointChoosed = new boolean[width][height];

		int minX = bufferedImage.getMinX();
		int minY = bufferedImage.getMinY();

		for (int x = minX; x < width; x++) {
			// System.out.println("行:" + x);
			for (int y = minY; y < height; y++) {
				// 像素点
				int pixel = bufferedImage.getRGB(x, y);
				pointImg[x][y] = pixel;
				// log.debug("(" + x + "," + y + "):" + pixel);
			}
		}

		// System.out.println("开始记录日志");
		// log.debugOutFile();
		// System.out.println("日志完成");
		// boolean debugBreak = true;
		// if (debugBreak)
		// return;

		int sekuaiCount = 0;
		// 分色块处理
		List<SeKuai> sekuaiList = new ArrayList<SeKuai>();
		for (int x = 0; x < pointChoosed.length; x++) {
			boolean[] pointChoosedX = pointChoosed[x];
			for (int y = 0; y < pointChoosedX.length; y++) {

				boolean[][] pointChoosedTmp = new boolean[width][height];
				boolean[][] pointChoosedConfirmed = new boolean[width][height];

				boolean xy = pointChoosedX[y];

				// 图片色块已经使用的像素点, 不再进行分块
				if (xy)
					continue;

				// 从该像素点, 检索相同 (或相似==待)色块
				pointChoosed[x][y] = true;
				pointChoosedTmp[x][y] = true;

				// 像素点像素值
				int point = pointImg[x][y];

				// 色块
				SeKuai seKuai = new SeKuai();
				seKuai.setStandardPixel(point);

				// 上下左右, 进行扩展
				// 1.如果非上方边界, 上方像素点也未分配色块, 向上扩展
				// 2.如果非下方边界, 下方像素点也未分配色块, 向下扩展
				// 3.如果非左方边界, 左方像素点也未分配色块, 向左扩展
				// 4.如果非右方边界, 右方像素点也未分配色块, 向右扩展
				List<Point> extendPointList = new ArrayList<Point>();
				extendPointList.add(new Point(x, y));
				List<Point> tmpPointList = new ArrayList<Point>();
				for (int i = 0; i < extendPointList.size(); i++) {

					// System.out.println(i);

					Point pointAt = extendPointList.get(i);
					int pointAtX = pointAt.getX();
					int pointAtY = pointAt.getY();

					// 1.如果非上方边界, 上方像素点也未分配色块, 向上扩展
					// if (pointAtY != 0 && !pointChoosed[pointAtX][pointAtY - 1]) {
					if (pointAtY != 0) {
						// 扩展
						if (inSeCha(point, pointImg[pointAtX][pointAtY - 1])) {
							pointChoosed[pointAtX][pointAtY - 1] = true;
							pointChoosedTmp[pointAtX][pointAtY - 1] = true;
							if (!pointChoosedConfirmed[pointAtX][pointAtY - 1])
								tmpPointList.add(new Point(pointAtX, pointAtY - 1));
							seKuai.addPoint(pointAtX, pointAtY - 1);
						}
					}

					// 2.如果非下方边界, 下方像素点也未分配色块, 向下扩展
					// if (pointAtY != (height - 1) && !pointChoosed[pointAtX][pointAtY + 1]) {
					if (pointAtY != (height - 1)) {
						// 扩展
						if (inSeCha(point, pointImg[pointAtX][pointAtY + 1])) {
							pointChoosed[pointAtX][pointAtY + 1] = true;
							pointChoosedTmp[pointAtX][pointAtY + 1] = true;
							if (!pointChoosedConfirmed[pointAtX][pointAtY + 1])
								tmpPointList.add(new Point(pointAtX, pointAtY + 1));
							seKuai.addPoint(pointAtX, pointAtY + 1);
						}
					}

					// 3.如果非左方边界, 左方像素点也未分配色块, 向左扩展
					// if (pointAtX != 0 && !pointChoosed[pointAtX - 1][pointAtY]) {
					if (pointAtX != 0) {
						// 扩展
						if (inSeCha(point, pointImg[pointAtX - 1][pointAtY])) {
							pointChoosed[pointAtX - 1][pointAtY] = true;
							pointChoosedTmp[pointAtX - 1][pointAtY] = true;
							if (!pointChoosedConfirmed[pointAtX - 1][pointAtY])
								tmpPointList.add(new Point(pointAtX - 1, pointAtY));
							seKuai.addPoint(pointAtX - 1, pointAtY);
						}
					}

					// 4.如果非右方边界, 右方像素点也未分配色块, 向右扩展
					// if (pointAtX != (width - 1) && !pointChoosed[pointAtX + 1][pointAtY]) {
					if (pointAtX != (width - 1)) {
						// 扩展
						if (inSeCha(point, pointImg[pointAtX + 1][pointAtY])) {
							pointChoosed[pointAtX + 1][pointAtY] = true;
							pointChoosedTmp[pointAtX + 1][pointAtY] = true;
							if (!pointChoosedConfirmed[pointAtX + 1][pointAtY])
								tmpPointList.add(new Point(pointAtX + 1, pointAtY));
							seKuai.addPoint(pointAtX + 1, pointAtY);
						}
					}

					pointChoosedConfirmed[pointAtX][pointAtY] = true;

					if (i == extendPointList.size() - 1) {
						extendPointList = tmpPointList;
						tmpPointList = new ArrayList<Point>();
						i = -1;
						sekuaiCount++;
					}
				}
				sekuaiList.add(seKuai);
			}
		}

		System.out.println("色块总数量:" + sekuaiList.size());

		int count = 0;
		for (int i = 0; i < sekuaiList.size(); i++) {
			SeKuai seKuai = sekuaiList.get(i);
			if (seKuai.getPointCount() < 100)
				continue;
			count++;

			int standardPixel = seKuai.getStandardPixel();
			int r = (standardPixel & 0xFF0000) >> 16;
			int g = (standardPixel & 0xFF00) >> 8;
			int b = standardPixel & 0xFF;

			// BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			// 获取图片处理对象
			Graphics2D graphics = (Graphics2D) image.getGraphics();
			// 填充背景色
			graphics.setColor(Color.WHITE);
			// graphics.setColor(new Color(255 - r, 255 - g, 255 - b));
			graphics.fillRect(1, 1, width - 1, height - 1);
			graphics.drawImage(image, minX, minY, null);
			graphics.dispose();

			for (int j = 0; j < seKuai.getPointCount(); j++) {

				int xPointAt = seKuai.getXPointAt(j);
				int yPointAt = seKuai.getYPointAt(j);

				int rgb = pointImg[xPointAt][yPointAt];

				int pointR = (rgb & 0xFF0000) >> 16;
				int pointG = (rgb & 0xFF00) >> 8;
				int pointB = rgb & 0xFF;

				// image.setRGB(xPointAt, yPointAt, (pointR << 16 + pointG << 8 + pointB));
				image.setRGB(xPointAt, yPointAt, rgb);
			}

			String tmpFileName = "";

			String[] split = file.getName().split("\\.");
			if (split.length == 1) {
				tmpFileName = file.getName();
			} else {
				for (int iName = 0; iName < split.length - 1; iName++) {
					tmpFileName += split[iName];
				}
				tmpFileName += "_" + i + "." + split[split.length - 1];
			}

			String tmpImgPath = tmpPath + tmpFileName;
			// System.out.println("tmpImgPath:" + tmpImgPath);

			File tmpImg = new File(tmpImgPath);
			if (!tmpImg.getParentFile().exists()) {
				tmpImg.getParentFile().mkdirs();
			}

			OutputStream output = new FileOutputStream(new File(tmpImgPath));
			ImageIO.write(image, "jpg", output);
		}

		System.out.println("过滤色块数量:" + count);

		// OutputStream output = new FileOutputStream(new File(dst));
		// ImageIO.write(bufferedImage, "jpg", output);
	}

	private static boolean inSeCha(int standardPointRGB, int realPonitRGB) {

		int r = (standardPointRGB & 0xFF0000) >> 16;
		int g = (standardPointRGB & 0xFF00) >> 8;
		int b = standardPointRGB & 0xFF;

		int rR = (realPonitRGB & 0xFF0000) >> 16;
		int gR = (realPonitRGB & 0xFF00) >> 8;
		int bR = realPonitRGB & 0xFF;

		boolean rCheck;
		if (r > rR)
			rCheck = (r - rR) <= secha;
		else
			rCheck = (rR - r) <= secha;

		boolean gCheck;
		if (g > gR)
			gCheck = (g - gR) <= secha;
		else
			gCheck = (gR - g) <= secha;

		boolean bCheck;
		if (b > bR)
			bCheck = (b - bR) <= secha;
		else
			bCheck = (bR - b) <= secha;

		return rCheck && gCheck && bCheck;
	}
}
