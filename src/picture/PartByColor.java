package picture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class PartByColor {

	static String src = "C:\\Users\\misez\\Desktop\\图片\\辽B5942B.jpg";
	static String dst = "C:\\Users\\misez\\Desktop\\图片\\part\\辽B5942B_part.jpg";

	static int[][] pointImg;
	static boolean[][] pointChoosed;

	public static void main(String[] args) throws IOException {

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

		// 像素点二维数组
		pointImg = new int[width][height];
		pointChoosed = new boolean[width][height];

		int minX = bufferedImage.getMinX();
		int minY = bufferedImage.getMinY();

		for (int x = minX; x < width; x++) {
			for (int y = minY; y < height; y++) {
				// 像素点
				int pixel = bufferedImage.getRGB(x, y);
				pointImg[x][y] = pixel;
			}
		}

		//
		for (int i = 0; i < pointChoosed.length; i++) {
			boolean[] pointChoosedX = pointChoosed[i];
			for (int j = 0; j < pointChoosedX.length; j++) {

				boolean[][] pointChoosedTmp = new boolean[width][height];

				boolean xy = pointChoosedX[j];

				// 图片色块已经使用的像素点, 不再进行分块
				if (xy)
					continue;

				// 从该像素点, 检索相同 (或相似==待)色块
				pointChoosedX[j] = true;
				pointChoosedTmp[i][j] = true;

				// 像素点像素值
				int point = pointImg[i][j];

				// 上下左右, 进行扩展
				// 1.如果非上方边界, 上方像素点也未分配色块, 向上扩展
				// 2.如果非下方边界, 下方像素点也未分配色块, 向下扩展
				// 3.如果非左方边界, 左方像素点也未分配色块, 向左扩展
				// 4.如果非右方边界, 右方像素点也未分配色块, 向右扩展
				boolean hasNewPoint = true;
				while (hasNewPoint) {
					hasNewPoint = false;
					if(isLeftBorder())
				}
			}
		}

		OutputStream output = new FileOutputStream(new File(dst));
		ImageIO.write(bufferedImage, "jpg", output);
	}
}
