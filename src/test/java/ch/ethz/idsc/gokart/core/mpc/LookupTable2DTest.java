// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

import ch.ethz.idsc.gokart.core.mpc.LookupTable2D.LookupFunction;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class LookupTable2DTest extends TestCase {
  public void testConsistency() throws Exception {
    // units not part of this unit test
    // save to file and reload again
    Random random = new Random(0);
    final int n = 10;
    float table[][] = new float[n][n];
    for (int i0 = 0; i0 < n; ++i0)
      for (int i1 = 0; i1 < n; ++i1)
        table[i0][i1] = random.nextFloat();
    LookupTable2D lookupTable = new LookupTable2D( //
        Tensors.matrixFloat(table), //
        Clip.absoluteOne(), //
        Clip.absoluteOne(), //
        SI.METER);
    final File file = new File("testLookupTable.csv");
    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
      lookupTable.saveTable(bufferedWriter);
    }
    LookupTable2D lookupTable2 = null;
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
      lookupTable2 = LookupTable2D.from(bufferedReader);
    }
    assertEquals(Tensors.matrixFloat(table), lookupTable2.tensor);
    file.delete();
  }

  public void testFidelity() throws Exception {
    LookupFunction function = (u, v) -> u;
    final int DimN = 1000;
    final Scalar fidelityLimit = Quantity.of(0.001, SI.ONE);
    final int testN = 100;
    LookupTable2D lookupTable2D = LookupTable2D.build(//
        function, //
        DimN, //
        DimN, //
        Clip.function(-0.3, 1.2), //
        Clip.function(-0.7, 3.1), //
        SI.ONE);
    Random rand = new Random(0);
    for (int i = 0; i < testN; i++) {
      Scalar x = Quantity.of(rand.nextFloat(), SI.ONE);
      Scalar y = Quantity.of(rand.nextFloat(), SI.ONE);
      Scalar out = lookupTable2D.lookup(x, y);
      Scalar refOut = function.apply(x, y);
      Scalar diff = out.subtract(refOut).abs();
      // System.out.println("For X="+ x + " and Y="+y+": "+diff);
      // System.out.println("out="+out+ " /ref="+refOut);
      assertTrue(Scalars.lessThan(diff, fidelityLimit));
    }
  }

  public void testInversion() throws Exception {
    final int DimN = 100;
    final Scalar xMin = Quantity.of(-0.3, SI.ONE);
    final Scalar xMax = Quantity.of(1.2, SI.ONE);
    // final Scalar yMin = Quantity.of(-0.7, SI.ONE);
    // final Scalar yMax = Quantity.of(3.1, SI.ONE);
    final Scalar inversionLimit = Quantity.of(0.001, SI.ONE);
    final int testN = 100;
    LookupTable2D lookupTable2D = LookupTable2D.build(//
        Scalar::add, //
        DimN, //
        DimN, //
        Clip.function(-0.3, 1.2), //
        Clip.function(-0.7, 3.1), //
        SI.ONE);
    LookupTable2D inverseLookupTable = lookupTable2D.getInverseLookupTableBinarySearch(//
        Scalar::add, //
        0, DimN, //
        DimN, //
        Quantity.of(-5, SI.ONE), //
        Quantity.of(5, SI.ONE));
    Random rand = new Random(0);
    for (int i = 0; i < testN; i++) {
      Scalar x = Quantity.of(rand.nextFloat(), SI.ONE);
      Scalar y = Quantity.of(rand.nextFloat(), SI.ONE);
      Scalar out = lookupTable2D.lookup(x, y);
      Scalar xb = inverseLookupTable.lookup(out, y);
      Scalar diff = x.subtract(xb).abs();
      // System.out.println("For X="+ x + " and Y="+y+": "+diff);
      // System.out.println("x="+x+ " /xb="+xb);
      assertTrue(Scalars.lessThan(diff, inversionLimit));
    }
    // check if values outside limits of the original lookup table are enforced:
    Scalar xb = inverseLookupTable.lookup(Quantity.of(-5, SI.ONE), Quantity.of(0, SI.ONE));
    assertTrue(Scalars.lessThan((xb.subtract(xMin)).abs(), inversionLimit));
    xb = inverseLookupTable.lookup(Quantity.of(5, SI.ONE), Quantity.of(0, SI.ONE));
    assertTrue(Scalars.lessThan((xb.subtract(xMax)).abs(), inversionLimit));
  }

  public void testInversion2() throws Exception {
    final int DimN = 100;
    final Scalar inversionLimit = Quantity.of(0.001, SI.ONE);
    final Scalar yMin = Quantity.of(-0.7, SI.ONE);
    final Scalar yMax = Quantity.of(3.1, SI.ONE);
    final int testN = 100;
    LookupTable2D lookUpTable2D = LookupTable2D.build(//
        Scalar::add, //
        DimN, //
        DimN, //
        Clip.function(-0.3, 1.2), //
        Clip.function(-0.7, 3.1), //
        SI.ONE);
    LookupTable2D inverseLookupTable = lookUpTable2D.getInverseLookupTableBinarySearch(//
        Scalar::add, //
        1, //
        DimN, //
        DimN, //
        Quantity.of(-5, SI.ONE), //
        Quantity.of(5, SI.ONE));
    Random rand = new Random(0);
    for (int i = 0; i < testN; i++) {
      Scalar x = Quantity.of(rand.nextFloat(), SI.ONE);
      Scalar y = Quantity.of(rand.nextFloat(), SI.ONE);
      Scalar out = lookUpTable2D.lookup(x, y);
      Scalar yb = inverseLookupTable.lookup(x, out);
      Scalar diff = y.subtract(yb).abs();
      // System.out.println("For X="+ x + " and Y="+y+": "+diff);
      // System.out.println("y="+y+ " /yb="+yb);
      assertTrue(Scalars.lessThan(diff, inversionLimit));
    }
    // check if values outside limits of the original lookup table are enforced:
    Scalar yb = inverseLookupTable.lookup(Quantity.of(0, SI.ONE), Quantity.of(-4, SI.ONE));
    assertTrue(Scalars.lessThan((yb.subtract(yMin)).abs(), inversionLimit));
    yb = inverseLookupTable.lookup(Quantity.of(0, SI.ONE), Quantity.of(+4, SI.ONE));
    assertTrue(Scalars.lessThan((yb.subtract(yMax)).abs(), inversionLimit));
  }

  public void testWithPowerFunction() {
    final int DimN = 250;
    // higher limit because of scaling of output [-2300, 2300]
    final Scalar inversionLimit = Quantity.of(2, NonSI.ARMS);
    Clip clip = Clip.function(Quantity.of(-2300, NonSI.ARMS), Quantity.of(+2300, NonSI.ARMS));
    final Scalar yMin = Quantity.of(-10, SI.VELOCITY);
    final Scalar yMax = Quantity.of(+10, SI.VELOCITY);
    final int testN = 100;
    LookupTable2D lookupTable2D = LookupTable2D.build(//
        MotorFunction::getAccelerationEstimation, //
        DimN, //
        DimN, //
        clip, //
        Clip.function(yMin, yMax), //
        SI.ACCELERATION);
    LookupTable2D inverseLookupTable = lookupTable2D.getInverseLookupTableBinarySearch(//
        MotorFunction::getAccelerationEstimation, //
        0, //
        DimN, //
        DimN, //
        Quantity.of(-2, SI.ACCELERATION), //
        Quantity.of(+2, SI.ACCELERATION));
    Random rand = new Random();
    for (int count = 0; count < testN; ++count) {
      Scalar x = Quantity.of(rand.nextFloat() * 1000, NonSI.ARMS);
      Scalar y = Quantity.of(rand.nextFloat(), SI.VELOCITY);
      Scalar out = lookupTable2D.lookup(x, y);
      Scalar xb = inverseLookupTable.lookup(out, y);
      Scalar diff = x.subtract(xb).abs();
      if (Scalars.lessThan(inversionLimit, diff)) {
        System.out.println("For X=" + x + " and Y=" + y + ": " + diff);
        System.out.println("out: " + out);
        System.out.println("fun out: " + MotorFunction.getAccelerationEstimation(x, y));
        System.out.println("x=" + x + " /xb=" + xb);
      }
    }
  }
}