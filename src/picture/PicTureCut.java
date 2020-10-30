package picture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import picture.cutout.PictureForPart;
import picture.cutout.part.Part;

public class PicTureCut {

	static String src = "C:\\Users\\misez\\Desktop\\图片\\part\\辽B5942B.jpg";

	public static void main(String[] args) {

		File file = new File(src);

		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		PictureForPart pictureForPart = new PictureForPart();
		pictureForPart.initMatrix(bufferedImage);

		// 按照颜色渐变幅度进行图片分块
		int sechayu = 10;
		List<Part> partList = pictureForPart.partByRGBSeCha(sechayu);

		System.out.println(partList.size());
	}

}
