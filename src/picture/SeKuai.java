package picture;

import java.util.LinkedList;

public class SeKuai {

	private int standardPixel;

	private int pointCount = 0;
	private LinkedList<Integer> xList = new LinkedList<Integer>();
	private LinkedList<Integer> yList = new LinkedList<Integer>();

	public int getStandardPixel() {
		return standardPixel;
	}

	public void setStandardPixel(int standardPixel) {
		this.standardPixel = standardPixel;
	}

	public int getPointCount() {
		return pointCount;
	}

	private void setPointCount(int pointCount) {
		this.pointCount = pointCount;
	}

	public int getXPointAt(int point) {
		return xList.get(point);
	}

	public int getYPointAt(int point) {
		return yList.get(point);
	}

	public void addPoint(int x, int y) {
		xList.add(x);
		yList.add(y);
		pointCount++;
	}

}
