package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.RadiusXY;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Sign;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.Objects;

public class SightLineOccupancyGrid implements RenderInterface, OccupancyGrid {

    /** @param lbounds vector of length 2
     * @param range effective size of grid in coordinate space
     * @param cellDim non-negative dimension of cell with unit SI.METER
     * @param obstacleRadius with unit SI.METER
     * @return SightLineOccupancyGrid with grid dimensions ceil'ed to fit a whole number of cells per dimension */
    public static SightLineOccupancyGrid of(Tensor lbounds, Tensor range, Scalar cellDim, Scalar obstacleRadius) {
        // sizeCeil is for instance {200[m^-1], 200[m^-1]}
        Tensor sizeCeil = Ceiling.of(range.divide(Sign.requirePositive(cellDim)));
        Tensor rangeCeil = sizeCeil.multiply(cellDim);
        Dimension dimension = new Dimension( //
                Magnitude.PER_METER.toInt(sizeCeil.Get(0)), //
                Magnitude.PER_METER.toInt(sizeCeil.Get(1)));
        return new SightLineOccupancyGrid(lbounds, rangeCeil, dimension, obstacleRadius);
    }

    private static final byte MASK_OCCUPIED = 0;
    private static final Color COLOR_OCCUPIED = Color.BLACK;
    private static final Color COLOR_FREE = Color.WHITE;
    // ---
    private final Tensor lidar2gokart = SensorsConfig.GLOBAL.vlp16Gokart(); // from lidar frame to gokart frame
    private final Scalar cellDim; // [m] per cell
    private final Scalar cellDimInv;
    private final Tensor gridSize;
    private final BufferedImage obstacleImage;
    private final byte[] imagePixels;
    private final Graphics2D imageGraphics;
    private final Tensor scaling;
    private final Scalar obsDilationRadius;
    private final double obsPixelRadius;
    // ---
    private GeometricLayer lidar2cellLayer;
    private GeometricLayer world2cellLayer;
    private Tensor lbounds;
    private Tensor gokart2world = null;

    private SightLineOccupancyGrid(Tensor lbounds, Tensor rangeCeil, Dimension dimension, Scalar obstacleRadius) {
        VectorQ.requireLength(rangeCeil, 2);
        System.out.print("Grid range: " + rangeCeil + "\n");
        System.out.print("Grid size: " + dimension + "\n");
        this.lbounds = VectorQ.requireLength(lbounds, 2);
        gridSize = Tensors.vector(dimension.width, dimension.height).unmodifiable();
        cellDim = RadiusXY.requireSame(rangeCeil).divide(gridSize.Get(0));
        cellDimInv = cellDim.reciprocal();
        scaling = DiagonalMatrix.of(cellDim, cellDim, RealScalar.ONE).unmodifiable();
        // ---
        obstacleImage = new BufferedImage(dimX(), dimY(), BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster writableRaster = obstacleImage.getRaster();
        DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
        imagePixels = dataBufferByte.getData();
        imageGraphics = obstacleImage.createGraphics();
        imageGraphics.setColor(COLOR_OCCUPIED);
        imageGraphics.fillRect(0, 0, obstacleImage.getWidth(), obstacleImage.getHeight());
        obsDilationRadius = Magnitude.METER.apply(obstacleRadius);
        obsPixelRadius = obsDilationRadius.multiply(cellDimInv).number().doubleValue();
        // ---
        Tensor grid2cell = DiagonalMatrix.of(cellDimInv, cellDimInv, RealScalar.ONE);
        Tensor world2grid = getWorld2grid();
        //  ---
        lidar2cellLayer = GeometricLayer.of(grid2cell); // grid 2 cell
        lidar2cellLayer.pushMatrix(world2grid); // world to grid
        lidar2cellLayer.pushMatrix(IdentityMatrix.of(3)); // placeholder gokart2world
        lidar2cellLayer.pushMatrix(lidar2gokart); // lidar to gokart
        // ---
        world2cellLayer = GeometricLayer.of(grid2cell); // grid 2 cell
        world2cellLayer.pushMatrix(world2grid); // world to grid
    }

    private int dimX() {
        return gridSize.Get(0).number().intValue();
    }

    private int dimY() {
        return gridSize.Get(1).number().intValue();
    }

    private Tensor getWorld2grid() {
        return Se2Utils.toSE2Matrix(lbounds.negate().append(RealScalar.ZERO));
    }

    /** set vehicle pose w.r.t world frame
     * @param pose vector of the form {px, py, heading} */
    public void setPose(Tensor pose) {
        gokart2world = GokartPoseHelper.toSE2Matrix(pose);
        lidar2cellLayer.popMatrix();
        lidar2cellLayer.popMatrix();
        lidar2cellLayer.pushMatrix(gokart2world);
        lidar2cellLayer.pushMatrix(lidar2gokart);
    }

    public void updateMap(Tensor polygon) {
        if (Objects.nonNull(gokart2world)) {
            freeSpace(polygon);
            obstacles(polygon);
        }
    }

    private void freeSpace(Tensor polygon) {
        imageGraphics.setColor(COLOR_FREE);
        Path2D path2D = lidar2cellLayer.toPath2D(polygon);
        path2D.closePath();
        // TODO make are smaller by obstacle radius
        imageGraphics.fill(path2D);
    }

    private void obstacles(Tensor polygon) {
        imageGraphics.setColor(COLOR_OCCUPIED);
        polygon.forEach(point -> {
            if (!point.equals(Tensors.vector(0, 0))) {
                Point2D point2D = lidar2cellLayer.toPoint2D(point);
                Ellipse2D sphere = new Ellipse2D.Double(point2D.getX(), point2D.getY(), //
                        2 * obsPixelRadius, 2 * obsPixelRadius);
                imageGraphics.fill(sphere);
            }
        });
    }

    @Override // from RenderInterface
    public synchronized void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        // TODO JPH simplify use ImageRender?
        Tensor model2pixel = geometricLayer.getMatrix();
        Tensor translate = IdentityMatrix.of(3);
        translate.set(lbounds.get(0).multiply(cellDimInv), 0, 2);
        translate.set(lbounds.get(1).multiply(cellDimInv), 1, 2);
        Tensor matrix = model2pixel.dot(scaling).dot(translate);
        graphics.drawImage(obstacleImage, AffineTransforms.toAffineTransform(matrix), null);
    }

    @Override // from OccupancyGrid
    public Tensor getGridSize() {
        return gridSize;
    }

    private int cellToIdx(int pix, int piy) {
        return piy * dimX() + pix;
    }

    private boolean isCellInGrid(int pix, int piy) {
        return 0 <= pix && pix < dimX() && 0 <= piy && piy < dimY();
    }

    @Override // from OccupancyGrid
    public Tensor getTransform() {
        Tensor translate = IdentityMatrix.of(3);
        translate.set(lbounds.get(0).multiply(cellDimInv), 0, 2);
        translate.set(lbounds.get(1).multiply(cellDimInv), 1, 2);
        return IdentityMatrix.of(3).dot(scaling).dot(translate);
    }

    @Override // from OccupancyGrid
    public void clearStart(int startX, int startY, double orientation) {
        // ---
    }

    @Override // from OccupancyGrid
    public boolean isCellOccupied(int pix, int piy) {
        if (isCellInGrid(pix, piy))
            return imagePixels[cellToIdx(pix, piy)] == MASK_OCCUPIED;
        return true;
    }

    @Override // from Region<Tensor>
    public boolean isMember(Tensor state) {
        Point2D point2D = world2cellLayer.toPoint2D(state);
        int pix = (int) point2D.getX();
        int piy = (int) point2D.getY();
        return isCellOccupied(pix, piy);
    }
}
