package test;

public class Test {
	public static void main(String[] args) {

		// 查看二进制运算
		// checkBinary();

		// 查看二位数组
		checkErweiArr();
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
	}
}
