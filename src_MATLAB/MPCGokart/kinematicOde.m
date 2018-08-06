%code by mheim
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
    %leadingbeta = x(6);
    %spline positions
    %st = x(6);
    %constant for axle difference
    l = 1;
    %acceleration at rear axle
    ab = u(1);
    %steering input (1st derivative)
    dotbeta = u(2);
    k=1;
    %evolution:
    dx = zeros(6,1);
    dx(1)=v*cos(omega);
    dx(2)=v*sin(omega);
    dx(3)=v/l*tan(beta);
    dx(4)=ab;
    dx(5)=dotbeta;
    %dx(5)=k*(leadingbeta-beta);
    %dx(6)=dotbeta;
    %for spline
    %[n,~]=size(p);
    %lstep = interp1([steps(end);steps],0:n,t);
    %ls = interp1([speed(end);speed],0:n,nt);
    %dx(6)=1/lstep;
end