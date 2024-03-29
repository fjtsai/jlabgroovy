package groovySci.math.plot.canvas;

import java.awt.*;
import java.awt.event.*;

import groovySci.math.plot.plotObjects.*;
import groovySci.math.plot.plots.*;
import groovySci.math.plot.render.*;

import static java.lang.Math.*;

import static groovySci.math.plot.plotObjects.Base.*;
import static groovySci.math.plot.utils.PArray.*;
import static groovySci.math.plot.utils.Histogram.*;

public class Plot3DCanvas extends PlotCanvas {

	private static final long serialVersionUID = 1L;

	public final static int ROTATION = 2;

	public Plot3DCanvas() {
		super();
		ActionMode = ROTATION;
	}

	public Plot3DCanvas(Base b, BasePlot bp) {
		super(b, bp);
		ActionMode = ROTATION;
	}

	public Plot3DCanvas(double[] min, double[] max, String[] axesScales, String[] axesLabels) {
		super(min, max, axesScales, axesLabels);
		ActionMode = ROTATION;
	}

	public void initDrawer() {
		draw = new AWTDrawer3D(this);
	}

	public void initBasenGrid(double[] min, double[] max) {
		initBasenGrid(min, max, new String[] { LINEAR, LINEAR, LINEAR }, new String[] { "X", "Y", "Z" });
	}

	public void initBasenGrid() {
		initBasenGrid(new double[] { 0, 0, 0 }, new double[] { 1, 1, 1 });
	}
	
	private static double[][] convertXYZ(double[]... XYZ) {
		if (XYZ.length == 3 && XYZ[0].length != 3)
			return mergeColumns(XYZ[0], XYZ[1], XYZ[2]);
		else
			return XYZ;
	}

	public int addScatterPlot(String name, Color c, double[][] XYZ) {
		return addPlot(new ScatterPlot(name, c, convertXYZ(XYZ)));
	}
	
	public int addScatterPlot(String name, Color c, double[] X, double[] Y, double[] Z) {
		return addPlot(new ScatterPlot(name, c, convertXYZ(X,Y,Z)));
	}
	
	public int addLinePlot(String name, Color c, double[][] XYZ) {
		return addPlot(new LinePlot(name, c, convertXYZ(XYZ)));
	}
	
	public int addLinePlot(String name, Color c, double[] X, double[] Y, double[] Z) {
		return addPlot(new LinePlot(name, c, convertXYZ(X,Y,Z)));
	}
	
	public int addBarPlot(String name, Color c, double[][] XYZ) {
		return addPlot(new BarPlot(name, c, convertXYZ(XYZ)));
	}
	
	public int addBarPlot(String name, Color c, double[] X, double[] Y, double[] Z) {
		return addPlot(new BarPlot(name, c, convertXYZ(X,Y,Z)));
	}

	public int addBoxPlot(String name, Color c, double[][] XY, double[][] dX) {
		return addPlot(new BoxPlot3D(XY, dX, c, name));
	}

	public int addBoxPlot(String name, Color c, double[][] XYdX) {
		return addPlot(new BoxPlot3D(getColumnsRangeCopy(XYdX, 0, 2), getColumnsRangeCopy(XYdX, 3, 5), c, name));
	}

	public int addHistogramPlot(String name, Color c, double[][] XY, double[][] dX) {
		return addPlot(new HistogramPlot3D(name, c, XY, dX));
	}

	public int addHistogramPlot(String name, Color c, double[][] XYdX) {
		return addPlot(new HistogramPlot3D(name, c, getColumnsRangeCopy(XYdX, 0, 2), getColumnsRangeCopy(XYdX, 3, 4)));
	}

	public int addHistogramPlot(String name, Color c, double[][] XY, int nX, int nY) {
		double[][] XYZ = histogram_classes_2D(XY, nX, nY);
		return addPlot(new HistogramPlot3D(name, c, XYZ, XYZ[1][0] - XYZ[0][0], XYZ[nX][1] - XYZ[0][1]));
	}

	public int addHistogramPlot(String name, Color c, double[][] XY, double[] boundsX, double[] boundsY) {
		double[][] XYZ = histogram_classes_2D(XY, boundsX, boundsY);
		return addPlot(new HistogramPlot3D(name, c, XYZ, XYZ[1][0] - XYZ[0][0], XYZ[boundsX.length-1][1] - XYZ[0][1]));
	}

	public int addHistogramPlot(String name, Color c, double[][] XY, double minX, double maxX, int nX, double minY, double maxY, int nY) {
		double[][] XYZ = histogram_classes_2D(XY, minX, maxX, nX, minY, maxY, nY);
		return addPlot(new HistogramPlot3D(name, c, XYZ, XYZ[1][0] - XYZ[0][0], XYZ[nX][1] - XYZ[0][1]));
	}
	


	public int addGridPlot(String name, Color c, double[] X, double[] Y, double[][] Z) {
		return addPlot(new GridPlot3D(name, c, X, Y, Z));
	}

                     public int addGridPlot(String name, Color c, boolean drawLines, boolean fillShape,  double[] X, double[] Y, double[][] Z) {
		return addPlot(new GridPlot3D(name, c, drawLines, fillShape, X, Y, Z));
	}

	public int addGridPlot(String name, Color c, double[][] XYZMatrix) {
		double[] X = new double[XYZMatrix[0].length - 1];
		System.arraycopy(XYZMatrix[0], 1, X, 0, XYZMatrix[0].length - 1);
		double[] Y = new double[XYZMatrix.length - 1];
		for (int i = 0; i < Y.length; i++)
			Y[i] = XYZMatrix[i + 1][0];
		double[][] Z = getSubMatrixRangeCopy(XYZMatrix, 1, XYZMatrix.length - 1, 1, XYZMatrix[0].length - 1);

		return addGridPlot(name, c, X, Y, Z);
	}
	
	public int addCloudPlot(String name, Color c, double[][] sampleXYZ, int nX, int nY, int nZ) {
		double[][] XYZh = histogram_classes_3D(sampleXYZ, nX, nY, nZ);
		return addPlot(new CloudPlot3D(name, c, XYZh, XYZh[1][0] - XYZh[0][0], XYZh[nX][1] - XYZh[0][1], XYZh[nX][2] - XYZh[0][2]));
	}

	public void mouseDragged(MouseEvent e) {
		//System.out.println("PlotCanvas.mouseDragged");

		dragging = true;
		/*
		 * System.out.println("PlotCanvas.mouseDragged"); System.out.println("
		 * mouseClick = [" + mouseClick[0] + " " + mouseClick[1] + "]");
		 * System.out.println(" mouseCurrent = [" + mouseCurrent[0] + " " +
		 * mouseCurrent[1] + "]");
		 */
		mouseCurrent[0] = e.getX();
		mouseCurrent[1] = e.getY();
		e.consume();
		switch (ActionMode) {
		case TRANSLATION:
			draw.translate(mouseCurrent[0] - mouseClick[0], mouseCurrent[1] - mouseClick[1]);
			mouseClick[0] = mouseCurrent[0];
			mouseClick[1] = mouseCurrent[1];
			repaint();
			break;
		case ZOOM:
			repaint();
			Graphics gcomp = getGraphics();
			gcomp.setColor(Color.black);
			gcomp.drawRect(min(mouseClick[0], mouseCurrent[0]), min(mouseClick[1], mouseCurrent[1]), abs(mouseCurrent[0] - mouseClick[0]), abs(mouseCurrent[1]
					- mouseClick[1]));
			break;
		case ROTATION:
			int[] t = new int[] { mouseCurrent[0] - mouseClick[0], mouseCurrent[1] - mouseClick[1] };
			((AWTDrawer3D) draw).rotate(t, new int[] {getWidth(),getHeight()});
			mouseClick[0] = mouseCurrent[0];
			mouseClick[1] = mouseCurrent[1];
			repaint();
			break;
		}
	}
}