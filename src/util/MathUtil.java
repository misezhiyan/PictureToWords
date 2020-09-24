package util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import util.constant.MathConstant;

public class MathUtil {

	public static void main(String[] args) throws Exception {

		// kaifang();
		// zhengtaifenbuDaoshu(new BigDecimal(0), new BigDecimal(0), new BigDecimal(1.5));
		BigDecimal zhengtaifenbuDaoshu = zhengtaifenbuDaoshu(new BigDecimal(1), new BigDecimal(1), new BigDecimal(1.5));
		System.out.println(zhengtaifenbuDaoshu);
	}

	// 开方运算
	public static BigDecimal kaifang() {

		System.out.println(Math.pow(1.2, 3));

		System.out.println(1.2 * 1.2 * 1.2);

		System.out.println(Math.sqrt(1.44));
		System.out.println(Math.cbrt(27));

		System.out.println((double) (1 / 2));

		return null;
	}

	public static BigDecimal zhengtaifenbuDaoshu(int x, int y, double σ) throws Exception {
		return zhengtaifenbuDaoshu(new BigDecimal(x), new BigDecimal(y), new BigDecimal(σ));
	}

	// public BigDecimal zhengtaifenbuDaoshu(BigDecimal μ, BigDecimal σ) {
	public static BigDecimal zhengtaifenbuDaoshu(double x, double y, double σ) throws Exception {
		return zhengtaifenbuDaoshu(new BigDecimal(x), new BigDecimal(y), new BigDecimal(σ));
	}

	// 正态分布 导数 函数
	private static BigDecimal zhengtaifenbuDaoshu(BigDecimal x, BigDecimal y, BigDecimal σ) throws Exception {

		if (σ.compareTo(new BigDecimal(0)) == 0)
			throw new Exception("σ 不能为 0");

		BigDecimal divide = new BigDecimal(1).divide(new BigDecimal(2).multiply(MathConstant.π).multiply(new BigDecimal(Math.pow(σ.doubleValue(), 2)).setScale(10)), 10, BigDecimal.ROUND_HALF_UP);

		BigDecimal pow = new BigDecimal(Math.pow(MathConstant.e.doubleValue(), -((new BigDecimal(Math.pow(x.doubleValue(), 2)).add(new BigDecimal(Math.pow(y.doubleValue(), 2)))).divide(new BigDecimal(2).multiply(new BigDecimal(Math.pow(σ.doubleValue(), 2))), 10, BigDecimal.ROUND_HALF_UP).doubleValue())));

		return divide.multiply(pow, new MathContext(10, RoundingMode.HALF_UP));
	}
}
