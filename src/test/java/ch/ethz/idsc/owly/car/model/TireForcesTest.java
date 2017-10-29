// code by jph
package ch.ethz.idsc.owly.car.model;

import ch.ethz.idsc.owly.car.core.VehicleModel;
import ch.ethz.idsc.owly.car.shop.CHatchbackModel;
import ch.ethz.idsc.owly.math.FrictionCoefficients;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class TireForcesTest extends TestCase {
  /***************************************************/
  // FOR TESTS ONLY
  static Tensor asVectorFX(Tensor Forces) { // Tensors.of(Fx1L, Fx1R, Fx2L, Fx2R);
    return Forces.get(Tensor.ALL, 0);
  }

  static Tensor asVectorFY(Tensor Forces) { // Tensors.of(Fy1L, Fy1R, Fy2L, Fy2R);
    return Forces.get(Tensor.ALL, 1);
  }

  static Tensor asVectorFZ(Tensor Forces) { // Tensors.of(Fz1L, Fz1R, Fz2L, Fz2R);
    return Forces.get(Tensor.ALL, 2);
  }

  static Tensor asVector_fX(Tensor fwheel) { // Tensors.of(fx1L, fx1R, fx2L, fx2R);
    return fwheel.get(Tensor.ALL, 0);
  }

  static Tensor asVector_fY(Tensor fwheel) { // Tensors.of(fy1L, fy1R, fy2L, fy2R);
    return fwheel.get(Tensor.ALL, 1);
  }

  public void testDemo1() {
    // System.out.println("TireForcesTest::demo1");
    VehicleModel carModel = CHatchbackModel.standard();
    CarState carState = CarStatic.x0_demo1(carModel);
    // System.out.println(carState.asVector());
    CarControl carControl = carModel.createControl(Tensors.vector(0, 0, 0, 0));
    TireForces tireForces = new TireForces(carModel, carState, carControl, FrictionCoefficients.TIRE_DRY_ROAD);
    assertTrue(Chop._10.allZero(asVectorFX(tireForces.Forces)));
    assertTrue(Chop._10.allZero(asVectorFY(tireForces.Forces)));
    assertTrue(Chop._10.allZero(asVector_fX(tireForces.Forces)));
    assertTrue(Chop._10.allZero(asVector_fY(tireForces.fwheel)));
    Tensor Fz = asVectorFZ(tireForces.Forces);
    // System.out.println(Fz);
    assertTrue(Chop._05.close(Fz.Get(0), Fz.Get(1)));
    assertTrue(Chop._05.close(Fz.Get(2), Fz.Get(3)));
  }

  // public void testDemo2() {
  // // System.out.println("TireForcesTest::demo2");
  // CarModel carModel = new CHatchbackModel();
  // CarState carState = CarStatic.x0_demo2();
  // // System.out.println(carState.asVector());
  // CarControl carControl = carModel.createControl(Tensors.vector(0, 0, 0, 0));
  // TireForces tireForces = new TireForces(carModel, carState, carControl);
  // assertTrue(Chop.isZeros(tireForces.asVectorFX()));
  // assertTrue(Chop.isZeros(tireForces.asVector_fX()));
  // // ---
  // assertTrue(tireForces.asVectorFY().Get(0).toString().startsWith("-2178."));
  // assertTrue(tireForces.asVectorFY().Get(1).toString().startsWith("-5086."));
  // assertTrue(tireForces.asVectorFY().Get(2).toString().startsWith("-497."));
  // assertTrue(tireForces.asVectorFY().Get(3).toString().startsWith("-3439."));
  // // ---
  // assertTrue(tireForces.asVectorFZ().Get(0).toString().startsWith("2704."));
  // assertTrue(tireForces.asVectorFZ().Get(1).toString().startsWith("6315."));
  // assertTrue(tireForces.asVectorFZ().Get(2).toString().startsWith("610."));
  // assertTrue(tireForces.asVectorFZ().Get(3).toString().startsWith("4221."));
  // // ---
  // assertTrue(tireForces.asVector_fY().Get(0).toString().startsWith("-2178."));
  // assertTrue(tireForces.asVector_fY().Get(1).toString().startsWith("-5086."));
  // assertTrue(tireForces.asVector_fY().Get(2).toString().startsWith("-497."));
  // assertTrue(tireForces.asVector_fY().Get(3).toString().startsWith("-3439."));
  // }
  /** {903.610973774307, -1807.7084631684347, 2736.1718856177345, -865.0967379905104}
   * {4386.855391683335, 1896.4937941266203, 766.4628278978114, 202.1501561222724}
   * {5768.831655182105, 3430.8856422219737, 3494.974357778026, 1157.0283448178939}
   * ---
   * {2896.1637422536455, -677.1858655495801, 2736.1718856177345, -865.0967379905104}
   * {3416.6136154856404, 2530.991486052735, 766.4628278978114, 202.1501561222724}
   * 
   * FORCES = 1.0e+03 *
   * 0.9036
   * -1.8077
   * 2.7362
   * -0.8651
   * 
   * 4.3869
   * 1.8965
   * 0.7665
   * 0.2022
   * 
   * 5.7688
   * 3.4309
   * 3.4950
   * 1.1570
   * 
   * forces = 1.0e+03 *
   * 2.8962
   * -0.6772
   * 2.7362
   * -0.8651
   * 
   * 3.4166
   * 2.5310
   * 0.7665
   * 0.2022 */
  public void testDemo3() {
    // System.out.println("TireForcesTest::demo3");
    VehicleModel carModel = CHatchbackModel.standard();
    CarState carState = CarStatic.x0_demo3();
    // System.out.println(carState.asVector());
    double maxDelta = 30 * Math.PI / 180;
    CarControl carControl = carModel.createControl(Tensors.vector(.5 / maxDelta, 0, 0, 0));
    // new CarControl();
    @SuppressWarnings("unused")
    TireForces tireForces = new TireForces(carModel, carState, carControl, FrictionCoefficients.TIRE_DRY_ROAD);
    // CONFIRMED
    // System.out.println(tireForces.asVectorFX());
    // System.out.println(tireForces.asVectorFY());
    // System.out.println(tireForces.asVectorFZ());
    // // ---
    // System.out.println("---");
    // System.out.println(tireForces.asVector_fX());
    // System.out.println(tireForces.asVector_fY());
  }

  public void testDemo3ackermann() {
    VehicleModel carModel = new CHatchbackModel(CarSteering.FRONT, RealScalar.of(0.5));
    CarState carState = CarStatic.x0_demo3();
    CarControl carControl = carModel.createControl(Tensors.vector(.3, 0, 0, 0));
    TireForces tireForces = new TireForces(carModel, carState, carControl, FrictionCoefficients.TIRE_DRY_ROAD);
    assertTrue(tireForces.isTorqueConsistent());
    assertTrue(tireForces.isFzConsistent());
    assertTrue(tireForces.isGForceConsistent());
    {
      Tensor ref = Tensors.fromString("{2738.193007092158, -3947.6788060735894, 1170.8731696720263, -2185.584181004175}");
      assertTrue(Chop._06.close(asVectorFX(tireForces.Forces), ref));
    }
    {
      Tensor ref = Tensors.fromString("{1751.9092384505734, 1837.4033663178448, 327.9877135839681, 510.7130382142411}");
      assertTrue(Chop._06.close(asVectorFY(tireForces.Forces), ref));
    }
    {
      Tensor ref = Tensors.fromString("{4002.7385312017072, 5430.277165557515, 1495.5828344424856, 2923.121468798294}");
      assertTrue(Chop._06.close(asVectorFZ(tireForces.Forces), ref));
    }
    {
      Tensor ref = Tensors.fromString("{3055.5116561664586, -3363.7523920949643, 1170.8731696720263, -2185.584181004175}");
      assertTrue(Chop._06.close(asVector_fX(tireForces.fwheel), ref));
    }
    {
      Tensor ref = Tensors.fromString("{1109.3851643535563, 2765.0296438111022, 327.9877135839681, 510.7130382142411}");
      assertTrue(Chop._06.close(asVector_fY(tireForces.fwheel), ref));
    }
  }

  public void testDemo3ackermann2() {
    CHatchbackModel carModel = new CHatchbackModel(CarSteering.BOTH, RealScalar.of(0.5));
    CarState carState = CarStatic.x0_demo3();
    CarControl carControl = carModel.createControl(Tensors.vector(.3, 0, 0, 0));
    TireForces tireForces = new TireForces(carModel, carState, carControl, FrictionCoefficients.TIRE_DRY_ROAD);
    assertTrue(tireForces.isTorqueConsistent());
    assertTrue(tireForces.isFzConsistent());
    assertTrue(tireForces.isGForceConsistent());
    {
      Tensor ref = Tensors.fromString("{3727.207496471704, -3538.798572155104, 2048.7077387254126, -1806.3756234894938}");
      assertTrue(Chop._06.close(asVectorFX(tireForces.Forces), ref));
    }
    {
      Tensor ref = Tensors.fromString("{-1.5386478805418733, -211.93848309716915, -403.93105996690156, -32.03339931296793}");
      assertTrue(Chop._06.close(asVectorFY(tireForces.Forces), ref));
    }
    {
      Tensor ref = Tensors.fromString("{4574.85951578761, 4365.487301615016, 2560.3726983849833, 2351.000484212391}");
      assertTrue(Chop._06.close(asVectorFZ(tireForces.Forces), ref));
    }
    {
      Tensor ref = Tensors.fromString("{3705.562023175224, -3536.306797616178, 2081.0180655406984, -1788.4808508788399}");
      assertTrue(Chop._06.close(asVector_fX(tireForces.fwheel), ref));
    }
    {
      Tensor ref = Tensors.fromString("{-401.1086904988554, 250.0949580422282, -172.41667788421276, -255.64678337171037}");
      assertTrue(Chop._06.close(asVector_fY(tireForces.fwheel), ref));
    }
  }
}
