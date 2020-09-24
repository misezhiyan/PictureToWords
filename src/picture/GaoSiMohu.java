package picture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GaoSiMohu {

	static String src = "C:\\Users\\misez\\Desktop\\图片\\辽B5942B.jpg";

	public static void main(String[] args) {

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

		PicTure picture = new PicTure(pointImg);
		String tmp = "C:\\Users\\misez\\Desktop\\图片\\高斯模糊\\" + file.getName();
		// picture.saveNewPicture(tmp);
		try {
			// 制作高斯模糊二位数组
			// picture.saveGaoSiMohuPicture(tmp);
			// 边缘检测
			picture.saveGaoSiMohuPicture(tmp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
