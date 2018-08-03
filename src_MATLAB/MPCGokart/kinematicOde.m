function [ dx ] = kinematicOde( t, x, u, p, w)
    %simplest form: input is acceleration at back axle
    %assuming no slip
    %Position
    %xP = x(1);
    %yP = x(2);
    %Orientation
    omega = x(3);
    %forward speed
    v = x(4);
    %steering angle
    beta = x(5);
    %constant for axle difference
    l = 1;
    %acceleration at rear axle
    ab = u(1);
    %steering input (1st derivative)
    dotbeta = u(2)
    %evolution:
    dx(1)=v*cos(omega);
    dx(2)=v*sin(omega);
    dx(3)=v/l*tan(beta);
    dx(4)=ab;
    dx(5)=dotbeta;
end