//code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;
import java.util.ResourceBundle.Control;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MPCPathFollowingClient {
  private final MPCPathFollowingConfig mpcPathFollowingConfig;
  // hardcoded address to localhost TODO: check with jph if we can move that to Config
  private InetAddress serverAddress;
  private Socket socket;
  private Scanner scanner;
  private Boolean isLaunched = true;
  // TODO: check with jph how to "publish" the results to the rest of retina
  // for now just write it to an array
  // canAccess is set to false when array is written to.
  private ReadWriteLock controlAndPredictionStepsLock = new ReentrantReadWriteLock();
  private ControlAndPredictionStep[] controlAndPredictionSteps = new ControlAndPredictionStep[MPCNative.PREDICTIONSIZE];

  /** gives back a deep copy of the control and predictions steps
   * Result has to be checked if not null */
  public ControlAndPredictionStep[] getControlAndPredictionStepsDeepCopy() {
    controlAndPredictionStepsLock.readLock().lock();
    ControlAndPredictionStep[] result = null;
    try {
      result = this.controlAndPredictionSteps.clone();
    } finally {
      controlAndPredictionStepsLock.readLock().unlock();
    }
    return result;
  }

  /** gives back control and prediction at time t */
  public ControlAndPredictionStep getControlAndPredictionAtTime(double t) {
    controlAndPredictionStepsLock.readLock().lock();
    ControlAndPredictionStep result = null;
    try {
      // TODO: implement this
    } finally {
      controlAndPredictionStepsLock.readLock().unlock();
    }
    return result;
  }

  // path parameters
  private MPCPathParameters pathParameters = null;
  private Boolean pathParametersUpdated = false;
  private ReadWriteLock pathParametersLock = new ReentrantReadWriteLock();

  public void updatePathParameters(MPCPathParameters pathParameters) {
    pathParametersLock.writeLock().lock();
    try {
      this.pathParameters = pathParameters;
      this.pathParametersUpdated = true;
    } finally {
      pathParametersLock.writeLock().unlock();
    }
  }

  // optimization parameters
  private MPCOptimizationParameters optimizationParameters = null;
  private Boolean optimizationParametersUpdated = false;
  private ReadWriteLock optimizationParametersLock = new ReentrantReadWriteLock();

  public void updateOptimizationParameters(MPCOptimizationParameters optimizationParameters) {
    optimizationParametersLock.writeLock().lock();
    try {
      this.optimizationParameters = optimizationParameters;
      this.optimizationParametersUpdated = true;
    } finally {
      optimizationParametersLock.writeLock().unlock();
    }
  }

  // set current state
  private GokartState currentState = null;
  private ReadWriteLock currentStateLock = new ReentrantReadWriteLock();

  public void updateCurrentState(GokartState currentState) {
    currentStateLock.writeLock().lock();
    try {
      this.currentState = currentState;
    } finally {
      currentStateLock.writeLock().unlock();
    }
  }

  public MPCPathFollowingClient(MPCPathFollowingConfig mpcPathFollowingConfig) {
    this.mpcPathFollowingConfig = mpcPathFollowingConfig;
  }

  private Thread clientThread;
  private Runnable clientRunnable = new Runnable() {
    @Override
    public void run() {
      try {
        // This is to find out if were to place the native code
        System.out.println(System.getProperty("user.dir"));
        Process p;
        String command = "./mpcnativeserver";
        p = Runtime.getRuntime().exec(command);
        serverAddress = InetAddress.getLocalHost();
        final byte[] data = new byte[MPCNative.INITIALMSGSIZE];
        Socket socket = new Socket(serverAddress, MPCNative.TCP_PORT);
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        while (isLaunched) {
          // check if we should update path parameters
          if (pathParametersUpdated) {
            pathParametersLock.readLock().lock();
            try {
              //outputStream.writeObject(new MPCPathParameterMessage(pathParameters));
              pathParametersUpdated = false;
            } finally {
              pathParametersLock.readLock().unlock();
            }
          }
          // check if we should update optimization parameters
          if (optimizationParametersUpdated) {
            optimizationParametersLock.readLock().lock();
            try {
              //outputStream.writeObject(new MPCOptimizationParameterMessage(optimizationParameters));
              optimizationParametersUpdated = false;
            } finally {
              optimizationParametersLock.readLock().unlock();
            }
          }
          // send request for control
          currentStateLock.readLock().lock();
          try {
            // TODO: send request
          } finally {
            currentStateLock.readLock().unlock();
          }
          // read response
          controlAndPredictionStepsLock.writeLock().lock();
          try {
            // TODO: read stuff and write it to controlandpredictionsteps
          } finally {
            controlAndPredictionStepsLock.writeLock().unlock();
          }
        }
      } catch (Exception e) {
        // TODO: handle exception
      }
    }
  };

  /** start MPC node
   * @throws IOException
   * @throws InterruptedException */
  public void start() throws Exception {
    // TODO: ask jph if we should catch or throw
    // TODO: check were the runtime is started
    clientThread = new Thread(clientRunnable);
    clientThread.start();
  }

  /** finish MPC node
   * @throws Exception */
  public void finish() throws Exception {
    // TODO: finish the MPC node
    isLaunched = false;
    clientThread.join();
  }
}
