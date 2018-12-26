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
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import junit.framework.TestCase;

// TODO MH reduce printout in tests
public class LookupTable2DTest extends TestCase {
  public void testConsistency() throws Exception {
    // units not part of this unit test
    // save to file and reload again
    Unit testUnit = SI.ONE;
    Random random = new Random(0);
    float table[][] = new float[10][10];
    for (int i1 = 0; i1 < 10; i1++) {
      for (int i2 = 0; i2 < 10; i2++) {
        table[i1][i2] = random.nextFloat();
      }
    }
    LookupTable2D lookUpTable = new LookupTable2D(table, RealScalar.ONE.negate(), RealScalar.ONE, RealScalar.ONE.negate(), RealScalar.ONE,
        // testUnit, testUnit,
        testUnit);
    final File file = new File("testLookupTable.csv");
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
      lookUpTable.saveTable(bw);
    }
    LookupTable2D lookUpTable2 = null;
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      lookUpTable2 = LookupTable2D.from(br);
    }
    for (int i1 = 0; i1 < 10; i1++)
      for (int i2 = 0; i2 < 10; i2++)
        assertEquals(table[i1][i2], lookUpTable2.table[i1][i2]);
    file.delete();
  }

  public void testFidelity() throws Exception {
    LookupFunction function = new LookupFunction() {
      @Override
      public Scalar apply(Scalar firstValue, Scalar secondValue) {
        // TODO find sine in Tensor
        return Quantity.of(
            // Math.sin(firstValue.number().floatValue()) +
            // Math.sin(secondValue.number().floatValue() * 3),
            firstValue.number().floatValue(), SI.ONE);
      }
    };
    final int DimN = 1000;
    final Scalar fidelityLimit = Quantity.of(0.001, SI.ONE);
    final int testN = 100;
    LookupTable2D lookUpTable2D = LookupTable2D.build(//
        function, //
        DimN, //
        DimN, //
        Quantity.of(-0.3, SI.ONE), //
        Quantity.of(1.2, SI.ONE), //
        Quantity.of(-0.7, SI.ONE), //
        Quantity.of(3.1, SI.ONE), //
        SI.ONE);
    Random rand = new Random(0);
    for (int i = 0; i < testN; i++) {
      Scalar x = Quantity.of(rand.nextFloat(), SI.ONE);
      Scalar y = Quantity.of(rand.nextFloat(), SI.ONE);
      Scalar out = lookUpTable2D.lookup(x, y);
      Scalar refOut = function.apply(x, y);
      Scalar diff = out.subtract(refOut).abs();
      // System.out.println("For X="+ x + " and Y="+y+": "+diff);
      // System.out.println("out="+out+ " /ref="+refOut);
      assertTrue(Scalars.lessThan(diff, fidelityLimit));
    }
  }

  public void testInversion() throws Exception {
    LookupFunction function = new LookupFunction() {
      @Override
      public Scalar apply(Scalar firstValue, Scalar secondValue) {
        // TODO find sine in Tensor
        return Quantity.of(firstValue.number().floatValue() + secondValue.number().floatValue(),
            // firstValue.number().floatValue(),
            SI.ONE);
      }
    };
    final int DimN = 100;
    final Scalar xMin = Quantity.of(-0.3, SI.ONE);
    final Scalar xMax = Quantity.of(1.2, SI.ONE);
    final Scalar yMin = Quantity.of(-0.7, SI.ONE);
    final Scalar yMax = Quantity.of(3.1, SI.ONE);
    final Scalar inversionLimit = Quantity.of(0.001, SI.ONE);
    final int testN = 100;
    LookupTable2D lookUpTable2D = LookupTable2D.build(//
        function, //
        DimN, //
        DimN, //
        xMin, xMax, //
        yMin, yMax, //
        SI.ONE);
    LookupTable2D inverseLookupTable = lookUpTable2D.getInverseLookupTableBinarySearch(//
        function, //
        0, DimN, //
        DimN, //
        Quantity.of(-5, SI.ONE), //
        Quantity.of(5, SI.ONE));
    Random rand = new Random(0);
    for (int i = 0; i < testN; i++) {
      Scalar x = Quantity.of(rand.nextFloat(), SI.ONE);
      Scalar y = Quantity.of(rand.nextFloat(), SI.ONE);
      Scalar out = lookUpTable2D.lookup(x, y);
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
    LookupFunction function = new LookupFunction() {
      @Override
      public Scalar apply(Scalar firstValue, Scalar secondValue) {
        // TODO find sine in Tensor
        return Quantity.of(firstValue.number().floatValue() + secondValue.number().floatValue(),
            // firstValue.number().floatValue(),
            SI.ONE);
      }
    };
    final int DimN = 100;
    final Scalar inversionLimit = Quantity.of(0.001, SI.ONE);
    final Scalar xMin = Quantity.of(-0.3, SI.ONE);
    final Scalar xMax = Quantity.of(1.2, SI.ONE);
    final Scalar yMin = Quantity.of(-0.7, SI.ONE);
    final Scalar yMax = Quantity.of(3.1, SI.ONE);
    final int testN = 100;
    LookupTable2D lookUpTable2D = LookupTable2D.build(//
        function, //
        DimN, //
        DimN, //
        xMin, xMax, //
        yMin, yMax, //
        SI.ONE);
    LookupTable2D inverseLookupTable = lookUpTable2D.getInverseLookupTableBinarySearch(//
        function, //
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
    final Scalar xMin = Quantity.of(-2300, NonSI.ARMS);
    final Scalar xMax = Quantity.of(2300, NonSI.ARMS);
    final Scalar yMin = Quantity.of(-10, SI.VELOCITY);
    final Scalar yMax = Quantity.of(10, SI.VELOCITY);
    final int testN = 100;
    LookupTable2D lookUpTable2D = LookupTable2D.build(//
        MotorFunction::getAccelerationEstimation, //
        DimN, //
        DimN, //
        xMin, //
        xMax, //
        yMin, //
        yMax, //
        SI.ACCELERATION);
    LookupTable2D inverseLookupTable = lookUpTable2D.getInverseLookupTableBinarySearch(//
        MotorFunction::getAccelerationEstimation, //
        0, //
        DimN, //
        DimN, //
        Quantity.of(-2, SI.ACCELERATION), //
        Quantity.of(2, SI.ACCELERATION));
    Random rand = new Random(0);
    for (int i = 0; i < testN; i++) {
      Scalar x = Quantity.of(rand.nextFloat() * 1000, NonSI.ARMS);
      Scalar y = Quantity.of(rand.nextFloat(), SI.VELOCITY);
      Scalar out = lookUpTable2D.lookup(x, y);
      Scalar xb = inverseLookupTable.lookup(out, y);
      Scalar diff = x.subtract(xb).abs();
      System.out.println("For X=" + x + " and Y=" + y + ": " + diff);
      System.out.println("out: " + out);
      System.out.println("fun out: " + MotorFunction.getAccelerationEstimation(x, y));
      System.out.println("x=" + x + " /xb=" + xb);
      assertTrue(Scalars.lessThan(diff, inversionLimit));
    }
  }
}