//code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MPCPathFollowingClient {
  private final MPCPathFollowingConfig mpcPathFollowingConfig;
  private InetAddress serverAddress;
  private Socket socket;
  private Scanner scanner;
  private Boolean isLaunched = true;
  public String absolutePath = "";
  
  private Process process;
 
  // for now just write it to an array
  // canAccess is set to false when array is written to.
  private ControlAndPredictionStep[] controlAndPredictionSteps = new ControlAndPredictionStep[MPCNative.PREDICTIONSIZE];

  /** gives back a deep copy of the control and predictions steps
   * Result has to be checked if not null */
  public ControlAndPredictionStep[] getControlAndPredictionStepsDeepCopy() {
    ControlAndPredictionStep[] result = null;
      result = this.controlAndPredictionSteps.clone();
    return result;
  }

  /** gives back control and prediction at time t */
  public ControlAndPredictionStep getControlAndPredictionAtTime(double t) {
    ControlAndPredictionStep result = null;
      // TODO: implement this
    return result;
  }

  // path parameters
  private MPCPathParameter pathParameters = null;
  private Boolean pathParametersUpdated = false;

  public void updatePathParameters(MPCPathParameter pathParameters) {
    this.pathParameters = pathParameters;
    this.pathParametersUpdated = true;
  }

  // optimization parameters
  private MPCOptimizationParameter optimizationParameters = null;
  private Boolean optimizationParametersUpdated = false;

  public void updateOptimizationParameters(MPCOptimizationParameter optimizationParameters) {
      this.optimizationParameters = optimizationParameters;
      this.optimizationParametersUpdated = true;
  }

  // set current state
  private GokartState currentState = null;
  private ReadWriteLock currentStateLock = new ReentrantReadWriteLock();

  public void updateCurrentState(GokartState currentState) {
      this.currentState = currentState;
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
          
          //only for testing
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
  public void first() throws Exception {
    System.out.println(System.getProperty("user.dir"));
    absolutePath = System.getProperty("user.dir");
    String fullPath = absolutePath+MPCNative.RELATIVEPATH+MPCNative.BINARY;
    //start server
    List<String> list = Arrays.asList( //
        fullPath//,
        //String.valueOf(MPCNative.TCP_SERVER_PORT)
        );
    ProcessBuilder processBuilder = new ProcessBuilder(list);
    process = processBuilder.start();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println(new Date() + " mpc-server: isAlive=" + process.isAlive());
      process.destroy();
    }));
    System.out.println(new Date() + " mpc-server: started");
    
    // TODO: check were the runtime is started
    clientThread = new Thread(clientRunnable);
    clientThread.start();
  }

  /** finish MPC node
   * @throws Exception */
  public void last() {
    // TODO: finish the MPC node
    isLaunched = false;
    process.destroy();
  }
}
