// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

import org.apache.xalan.xsltc.runtime.Operators;

import ch.ethz.idsc.gokart.core.mpc.LookUpTable2D.LookupFunction;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import junit.framework.TestCase;

public class LookUpTable2DTest extends TestCase {
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
		LookUpTable2D lookUpTable = new LookUpTable2D(table, -1f, 1f, -1f, 1f, testUnit, testUnit, testUnit);
		FileWriter fw = new FileWriter("testLookupTable.csv");
		BufferedWriter bw = new BufferedWriter(fw);
		lookUpTable.saveTable(bw);
		bw.close();

		FileReader fr = new FileReader("testLookupTable.csv");
		BufferedReader br = new BufferedReader(fr);
		LookUpTable2D lookUpTable2 = new LookUpTable2D(br);
		fr.close();

		for (int i1 = 0; i1 < 10; i1++) {
			for (int i2 = 0; i2 < 10; i2++) {
				assertEquals(table[i1][i2], lookUpTable2.table[i1][i2]);
			}
		}
		File file = new File("testLookupTable.csv");
		file.delete();
	}

	public void testFidelity() throws Exception {
		LookupFunction function = new LookupFunction() {
			@Override
			public Scalar getValue(Scalar firstValue, Scalar secondValue) {
				// TODO find sine in Tensor
				return Quantity.of(
						//Math.sin(firstValue.number().floatValue()) + Math.sin(secondValue.number().floatValue() * 3),
						firstValue.number().floatValue(),
						SI.ONE);
			}
		};
		final int DimN = 1000;
		final Scalar fidelityLimit = Quantity.of(0.001, SI.ONE);
		final int testN = 100;
		LookUpTable2D lookUpTable2D = new LookUpTable2D(//
				function, //
				DimN, //
				DimN, //
				Quantity.of(-0.3, SI.ONE), //
				Quantity.of(1.2, SI.ONE), //
				Quantity.of(-0.7, SI.ONE), //
				Quantity.of(3.1, SI.ONE), //
				SI.ONE, SI.ONE, SI.ONE);

		Random rand = new Random(0);
		for (int i = 0; i < testN; i++) {
			Scalar x = Quantity.of(rand.nextFloat(), SI.ONE);
			Scalar y = Quantity.of(rand.nextFloat(), SI.ONE);
			Scalar out = lookUpTable2D.lookup(x, y);
			Scalar refOut = function.getValue(x, y);
			Scalar diff = out.subtract(refOut).abs();
			//System.out.println("For X="+ x + " and Y="+y+": "+diff);
			//System.out.println("out="+out+ " /ref="+refOut);
			assertTrue(Scalars.lessThan(diff,fidelityLimit));
		}
	}
	
	public void testInversion() throws Exception {
		LookupFunction function = new LookupFunction() {
			@Override
			public Scalar getValue(Scalar firstValue, Scalar secondValue) {
				// TODO find sine in Tensor
				return Quantity.of(
						firstValue.number().floatValue()+secondValue.number().floatValue(),
						//firstValue.number().floatValue(),
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
		LookUpTable2D lookUpTable2D = new LookUpTable2D(//
				function, //
				DimN, //
				DimN, //
				xMin, //
				xMax, //
				yMin, //
				yMax, //
				SI.ONE, SI.ONE, SI.ONE);
		
		LookUpTable2D inverseLookupTable = lookUpTable2D.getInverseLookupTable(//
				0, 				
				DimN, //
				DimN, //
				Quantity.of(-5, SI.ONE), //
				Quantity.of(5, SI.ONE), //
				Quantity.of(-5, SI.ONE), //
				Quantity.of(5, SI.ONE));
				

		Random rand = new Random(0);
		for (int i = 0; i < testN; i++) {
			Scalar x = Quantity.of(rand.nextFloat(), SI.ONE);
			Scalar y = Quantity.of(rand.nextFloat(), SI.ONE);
			Scalar out = lookUpTable2D.lookup(x, y);
			Scalar xb = inverseLookupTable.lookup(out, y);
			Scalar diff = x.subtract(xb).abs();
			//System.out.println("For X="+ x + " and Y="+y+": "+diff);
			//System.out.println("x="+x+ " /xb="+xb);
			assertTrue(Scalars.lessThan(diff,inversionLimit));
		}
		
		//check if values outside limits of the original lookup table are enforced:
		Scalar xb = inverseLookupTable.lookup(Quantity.of(-5, SI.ONE), Quantity.of(0, SI.ONE));
		assertTrue(Scalars.lessThan((xb.subtract(xMin)).abs(),inversionLimit));
		xb = inverseLookupTable.lookup(Quantity.of(5, SI.ONE), Quantity.of(0, SI.ONE));
		assertTrue(Scalars.lessThan((xb.subtract(xMax)).abs(),inversionLimit));
	}
	
	public void testInversion2() throws Exception {
		LookupFunction function = new LookupFunction() {
			@Override
			public Scalar getValue(Scalar firstValue, Scalar secondValue) {
				// TODO find sine in Tensor
				return Quantity.of(
						firstValue.number().floatValue()+secondValue.number().floatValue(),
						//firstValue.number().floatValue(),
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
		LookUpTable2D lookUpTable2D = new LookUpTable2D(//
				function, //
				DimN, //
				DimN, //
				xMin, //
				xMax, //
				yMin, //
				yMax, //
				SI.ONE, SI.ONE, SI.ONE);
		
		LookUpTable2D inverseLookupTable = lookUpTable2D.getInverseLookupTable(//
				1, 				
				DimN, //
				DimN, //
				Quantity.of(-5, SI.ONE), //
				Quantity.of(5, SI.ONE), //
				Quantity.of(-5, SI.ONE), //
				Quantity.of(5, SI.ONE));
				

		Random rand = new Random(0);
		System.out.println("second dimension");
		for (int i = 0; i < testN; i++) {
			Scalar x = Quantity.of(rand.nextFloat(), SI.ONE);
			Scalar y = Quantity.of(rand.nextFloat(), SI.ONE);
			Scalar out = lookUpTable2D.lookup(x, y);
			Scalar yb = inverseLookupTable.lookup(x, out);
			Scalar diff = y.subtract(yb).abs();
			System.out.println("For X="+ x + " and Y="+y+": "+diff);
			System.out.println("y="+y+ " /yb="+yb);
			assertTrue(Scalars.lessThan(diff,inversionLimit));
		}
	
		//check if values outside limits of the original lookup table are enforced:
		Scalar yb = inverseLookupTable.lookup(Quantity.of(0, SI.ONE), Quantity.of(-4, SI.ONE));
		assertTrue(Scalars.lessThan((yb.subtract(yMin)).abs(),inversionLimit));
		yb = inverseLookupTable.lookup(Quantity.of(0, SI.ONE), Quantity.of(+4, SI.ONE));
		assertTrue(Scalars.lessThan((yb.subtract(yMax)).abs(),inversionLimit));
	}
}