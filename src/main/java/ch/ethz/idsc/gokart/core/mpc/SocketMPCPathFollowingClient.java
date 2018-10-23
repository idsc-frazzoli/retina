// code by mh
// !!!!!note: this is not used!!!!!
package ch.ethz.idsc.gokart.core.mpc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* package */ class SocketMPCPathFollowingClient implements MPCControlClient {
  public static MPCControlClient create() {
    Optional<File> optional = MPCNative.binary();
    if (optional.isPresent())
      return new SocketMPCPathFollowingClient(MPCPathFollowingConfig.GLOBAL);
    return new VoidMPCPathFollowingClient();
  }

  // ---
  private final MPCPathFollowingConfig mpcPathFollowingConfig;
  private InetAddress serverAddress;
  private Socket socket;
  private Scanner scanner;
  private Boolean isLaunched = true;
  private Process process;
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
  private MPCPathParameter pathParameters = null;
  private Boolean pathParametersUpdated = false;
  private ReadWriteLock pathParametersLock = new ReentrantReadWriteLock();

  public void updatePathParameters(MPCPathParameter pathParameters) {
    pathParametersLock.writeLock().lock();
    try {
      this.pathParameters = pathParameters;
      this.pathParametersUpdated = true;
    } finally {
      pathParametersLock.writeLock().unlock();
    }
  }

  // optimization parameters
  private MPCOptimizationParameter optimizationParameters = null;
  private Boolean optimizationParametersUpdated = false;
  private ReadWriteLock optimizationParametersLock = new ReentrantReadWriteLock();

  public void updateOptimizationParameters(MPCOptimizationParameter optimizationParameters) {
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

  private SocketMPCPathFollowingClient(MPCPathFollowingConfig mpcPathFollowingConfig) {
    this.mpcPathFollowingConfig = mpcPathFollowingConfig;
  }

  private Thread clientThread;
  private Runnable clientRunnable = new Runnable() {
    @Override
    public void run() {
      try {
        // This is to find out if were to place the native code
        System.out.println("not connected");
        serverAddress = InetAddress.getLocalHost();
        Socket socket = new Socket(serverAddress, MPCNative.TCP_SERVER_PORT);
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        System.out.println("connected");
        while (isLaunched) {
          // check if we should update path parameters
          if (pathParametersUpdated) {
            // outputStream.writeObject(new MPCPathParameterMessage(pathParameters));
            pathParametersUpdated = false;
          }
          // check if we should update optimization parameters
          if (optimizationParametersUpdated) {
            // outputStream.writeObject(new MPCOptimizationParameterMessage(optimizationParameters));
            optimizationParametersUpdated = false;
          }
          // send request for control
          // TODO: send request
          outputStream.flush();
          // read response
          // TODO: read stuff and write it to controlandpredictionsteps
          // only for testing
          String testString = "hello";
          outputStream.write(testString.getBytes(StandardCharsets.UTF_8));
          outputStream.flush();
        }
        socket.close();
      } catch (Exception e) {
        System.out.println("could not connect!");
        System.out.println(e.getMessage());
      }
    }
  };

  /** start MPC node
   * @throws IOException
   * @throws InterruptedException */
  @Override
  public void start() {
    String fullPath = MPCNative.binary().get().getAbsolutePath();
    // start server
    List<String> list = Arrays.asList(fullPath
    // String.valueOf(MPCNative.TCP_SERVER_PORT)
    );
    ProcessBuilder processBuilder = new ProcessBuilder(list);
    try {
      process = processBuilder.start();
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        System.out.println(new Date() + " mpc-server: isAlive=" + process.isAlive());
        process.destroy();
      }));
      System.out.println(new Date() + " mpc-server: started");
      // TODO: check were the runtime is started
      clientThread = new Thread(clientRunnable);
      clientThread.start();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /** finish MPC node
   * @throws Exception */
  @Override
  public void stop() {
    // TODO: finish the MPC node
    isLaunched = false;
    process.destroy();
  }
}
