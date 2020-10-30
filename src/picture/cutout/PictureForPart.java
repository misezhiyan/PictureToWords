package picture.cutout;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import picture.cutout.part.Part;

public class PictureForPart {

	private int[][] pointImg;
	private int[][] a;
	private int[][] r;
	private int[][] g;
	private int[][] b;

	private List<Part> partList;

	// 矩阵转入
	public void initMatrix(BufferedImage bufferedImage) {

		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();

		System.out.println(width * height);

		initMatrix(width, height);

		int minX = bufferedImage.getMinX();
		int minY = bufferedImage.getMinY();

		for (int x = minX; x < width; x++) {
			for (int y = minY; y < height; y++) {
				// 像素点
				int pixel = bufferedImage.getRGB(x, y);
				setPixel(x, y, pixel);
			}
		}
	}

	// 初始化矩阵
	private void initMatrix(int width, int height) {
		pointImg = new int[height][width];
		a = new int[height][width];
		r = new int[height][width];
		g = new int[height][width];
		b = new int[height][width];
	}

	// 获取像素点
	public void setPixel(int x, int y, int pixel) {
		pointImg[y][x] = pixel;
		a[y][x] = (pixel & 0xFF000000) >> 24;
		r[y][x] = (pixel & 0xFF0000) >> 16;
		g[y][x] = (pixel & 0xFF00) >> 8;
		b[y][x] = pixel & 0xFF;
	}

	// 根据 R G B 进行色差值分片
	public List<Part> partByRGBSeCha(int sechayu) {

		partList = new ArrayList<Part>();
		boolean[][] pointChoosed = new boolean[pointImg.length][pointImg[0].length];

		for (int y = 0; y < pointImg.length; y++) {
			for (int x = 0; x < pointImg[y].length; x++) {

				if (pointChoosed[y][x])
					continue;

				// 1.获取当前点的扩展点, 如果有扩展点, 继续获取扩展点, 如果没有扩展点, 返回
				pointChoosed[y][x] = true;
				List<Integer[]> pointChoosedOnePart = getPartFromPonit(x, y, pointChoosed, sechayu);
				Part part = new Part(pointChoosedOnePart);
				partList.add(part);
			}
		}

		return partList;
	}

	// 1.获取当前点的扩展点, 如果有扩展点, 继续获取扩展点, 如果没有扩展点, 返回
	private List<Integer[]> getPartFromPonit(int x, int y, boolean[][] pointChoosed, int sechayu) {

		// 选中点列表
		List<Integer[]> dstList = new ArrayList<Integer[]>();

		// 是否有选中点
		// boolean circleHasChoosedPoint = false;
		// 选中点列表
		List<Integer[]> partOneCircle = new ArrayList<Integer[]>();

		// 判断规则, 点未超出边界, 未被选中过, rgb 分别在色差之内
		// 左
		if (x - 1 > 0 && !pointChoosed[y][x - 1] && (r[y][x - 1] - r[y][x]) <= sechayu && (g[y][x - 1] - g[y][x]) <= sechayu && (b[y][x - 1] - b[y][x]) <= sechayu) {
			// circleHasChoosedPoint = true;
			partOneCircle.add(new Integer[] { x - 1, y });
			pointChoosed[y][x - 1] = true;
		}
		// 右
		if (x + 1 < pointImg[y].length && !pointChoosed[y][x + 1] && (r[y][x + 1] - r[y][x]) <= sechayu && (g[y][x + 1] - g[y][x]) <= sechayu && (b[y][x + 1] - b[y][x]) <= sechayu) {
			// circleHasChoosedPoint = true;
			partOneCircle.add(new Integer[] { x + 1, y });
			pointChoosed[y][x + 1] = true;
		}
		// 上
		if (y - 1 > 0 && !pointChoosed[y - 1][x] && (r[y - 1][x] - r[y][x]) <= sechayu && (g[y - 1][x] - g[y][x]) <= sechayu && (b[y - 1][x] - b[y][x]) <= sechayu) {
			// circleHasChoosedPoint = true;
			// partOneCircle[y - 1][x] = true;
			partOneCircle.add(new Integer[] { x, y - 1 });
			pointChoosed[y - 1][x] = true;
		}
		// 下
		if (y + 1 < pointImg.length && !pointChoosed[y + 1][x] && (r[y + 1][x] - r[y][x]) <= sechayu && (g[y + 1][x] - g[y][x]) <= sechayu && (b[y + 1][x] - b[y][x]) <= sechayu) {
			// circleHasChoosedPoint = true;
			// partOneCircle[y + 1][x] = true;
			partOneCircle.add(new Integer[] { x, y + 1 });
			pointChoosed[y + 1][x] = true;
		}

		dstList.addAll(partOneCircle);
		if (partOneCircle.size() > 0) {
			Iterator<Integer[]> iterator = partOneCircle.iterator();
			while (iterator.hasNext()) {
				Integer[] xy = iterator.next();
				List<Integer[]> partOneCircleSub = getPartFromPonit(xy[0], xy[1], pointChoosed, sechayu);

				dstList.addAll(partOneCircleSub);
			}
		}

		return dstList;
	}

}
