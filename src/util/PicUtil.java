package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @description: TODO
 * @author: liyq
 * @createtime: 2023-03-27 20:08
 */
public class PicUtil {

    /**
     * @description: 保存图片
     * @author: liyq
     * @createtime: 2023-03-27 20:49:27
     * @param: square
     * @param: fileName
     */
    public static void saveRgbToFile(int[][] square, String fileName) throws IOException {

        BufferedImage image = new BufferedImage(square.length, square[0].length, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                image.setRGB(i, j, square[i][j]);
            }
        }
        saveImgToFile(image, fileName);
    }

    /**
     * @description: 保存图片
     * @author: liyq
     * @createtime: 2023-03-28 15:38:03
     * @param: image
     * @param: outFileName
     */
    public static void saveImgToFile(BufferedImage image, String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if (!file.exists())
            file.createNewFile();
        ImageIO.write(image, "jpg", file);
    }

    /**
     * @description: 图片转二维RGB数组
     * @author: liyq
     * @createtime: 2023-03-27 20:51:27
     * @param: fileName
     * @return int[][]
     */
    public static int[][] toRgbSquare(String fileName) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(fileName));
        int[][] square = new int[bufferedImage.getWidth()][bufferedImage.getHeight()];
        for (int i = 0; i < square.length; i++) {
            for (int j = 0; j < square[i].length; j++) {
                // System.out.println(bufferedImage.getRGB(i, j));
                square[i][j] = bufferedImage.getRGB(i, j);
            }
        }
        return square;
    }

    /**
     * @description: 二值化
     * @author: liyq
     * @createtime: 2023-03-27 20:53:46
     * @param: sourceRgbSquare
     * @return int[][]
     */
    public static int[][] colorToBlack(int[][] sourceRgbSquare, int threshold) {

        int[][] colorToBSquare = new int[sourceRgbSquare.length][sourceRgbSquare[1].length];
        int rToB = 255 / 2;
        int gToB = 255 / 2;
        int bToB = 255 / 2;
        for (int i = 0; i < sourceRgbSquare.length; i++) {
            for (int j = 0; j < sourceRgbSquare[i].length; j++) {
                int rgb = sourceRgbSquare[i][j];
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                // colorToBSquare[i][j] = new Double(r * 0.3 + g * 0.59 + b * 0.11).intValue();
                int averag = (r + g + b) / 3;
                // int rgbT = averag << 16 | averag << 8 | averag;
                colorToBSquare[i][j] = averag > threshold ? (255 << 16 | 255 << 8 | 255) : 0;
            }
        }

        return colorToBSquare;
    }

    /**
     * @description: 去噪
     * @author: liyq
     * @createtime: 2023-03-28 16:46:26
     * @param: sourceRgbSquare
     * @return int[][]
     */
    public static int[][] disnoize(int[][] square) {
        return square;
    }

}
