%clear;

BEGIN_ACADO;
    
    acadoSet('problemname', 'kinematic_tracking');
    acadoSet('results_to_file', true);
    
    DifferentialState xp yp omega v beta;
    Control           ab dotbeta;
    %Disturbance       w;
    

    % Differential Equation
    f = acado.DifferentialEquation();
    f.linkMatlabODE('kinematicOde')
    
    f2 = acado.DifferentialEquation();      % Right know we use the same
    f2.linkMatlabODE('kinematicOde')
    

    h={xp yp omega v beta ab dotbeta};
    Q = eye(7)*0.0001;
    Q(1,1)=1;
    Q(2,2)=1;
    %Q(7,7)=1;
    r = zeros(1,7);
    
    ocp = acado.OCP(0.0, 15.0, 15);
    ocp.minimizeLSQ( Q, h, r );
    
    ocp.subjectTo( f );
    %ocp.subjectTo( -1 <= dotbeta <= 1 );
    ocp.subjectTo(  -3 <= ab <= 3);
    ocp.subjectTo(  -1 <= beta <= 1);
    ocp.subjectTo(  0 <= v);
    %ocp.subjectTo( w == 0.0 );
     
    % SETTING UP THE (SIMULATED) PROCESS:
    identity = acado.OutputFcn();
    dynamicSystem = acado.DynamicSystem(f2, identity);    
    process = acado.Process(dynamicSystem, 'INT_RK45');
    %process.set();
    
    % SETUP OF THE ALGORITHM AND THE TUNING OPTIONS:
    algo = acado.RealTimeAlgorithm(ocp, 0.5);
    algo.set('MAX_NUM_ITERATIONS', 5 );
    %algo.set('HESSIAN_APPROXIMATION','GAUSS_NEWTON');
    
    %algo.set('INTEGRATOR_TOLERANCE', 1e-2);
    %algo.set('ABSOLUTE_TOLERANCE',1e-2);
    
    %points = [1,2,2,4,2,2,1;0,0,5.7,6,6.3,10,10]';
    points = [0,10,10,5,0;0,0,10,9,10]';
    [p,steps,speed,ttpos]=getTrajectory(points,2,0.6,0.5);
    
    %scatter(ttpos(:,2),ttpos(:,3));
    %daspect([1 1 1])
    
    % SETTING UP THE NMPC CONTROLLER:
    ref = ttpos;
    %ref = [0,0,0;5,5,0;10,5,5;10,0,0];
    %ref = [0,0,0,0,0,0;5,5,4,0,0,0;10,10,4,0,0,0;20,0,0,0,0,0];
    %   TIME      X_REF      U_REF
    
    
    
    reference = acado.PeriodicReferenceTrajectory(ref);    
    controller = acado.Controller( algo,reference );
    
    
    % SETTING UP THE SIMULATION ENVIRONMENT,  RUN THE EXAMPLE..
    sim = acado.SimulationEnvironment( 0.0,30.0,process,controller );
    
    r = [5,1,0,3,0];
    sim.init( r );


    
END_ACADO;           % Always end with "END_ACADO".
                     % This will generate a file problemname_ACADO.m. 
                     % Run this file to get your results. You can
                     % run the file problemname_ACADO.m as many
                     % times as you want without having to compile again.
                     

% Run the test
out = kinematic_tracking_RUN();

draw;
