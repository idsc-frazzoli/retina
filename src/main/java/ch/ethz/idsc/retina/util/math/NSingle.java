// code by jph
package ch.ethz.idsc.retina.util.math;

import java.util.Formatter;

import ch.ethz.idsc.tensor.IntegerQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** for scalar conversion for export to CSV
 * 
 * the formatter indents to guarantee that the import
 * is as the values exported.
 * 
 * API inspired by {@link Formatter} */
// TODO move to tensor lib
public enum NSingle implements ScalarUnaryOperator {
  INSTANCE;
  // ---
  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof StringScalar) {
      String string = scalar.toString();
      boolean terminator = true;
      terminator &= string.charAt(0) == '\"';
      terminator &= string.charAt(string.length() - 1) == '\"';
      if (!terminator)
        return StringScalar.of("\"" + string + "\"");
      return scalar;
    }
    if (scalar instanceof RationalScalar)
      return IntegerQ.of(scalar) ? scalar : N.DOUBLE.apply(scalar);
    // TODO check complex
    if (scalar instanceof Quantity)
      throw TensorRuntimeException.of(scalar);
    return scalar;
  }
}
