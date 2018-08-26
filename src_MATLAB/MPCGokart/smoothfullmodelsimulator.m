%code by jelavice
function [ x] = fullmodelsimulator( x0, u, times, carParams)
% forward integrates the car model in time
% params: -x0[in] - initial state
%         -u [in] - vector of inputs, dimension is p x N where p id num
%            inputs and N is the number of samples
%         -times [in] - vector of times to integrate (over dimension N)
%         -carParams - structure contatinig all the parameters of the
%            car
%         -x [out] - trajectory dimensin is n x N where n id the number of 
%            states and N is the number od samples   
%
%  CALLER OF THIS FUNCTION MUST CLEAR THE PERSISTENT VARIABLES WITHIN 
%   THE FUNCTION BETWEEN TWO CALLS (command: clear rollout)

global params;
params = carParams;
global h;

if (size(u, 2) ~= length(times))
    error('time and input vecotr are not the right dimensions');
end

N = length(times);

h = times(2) - times(1);
for i =2:N
    if times(i) > times(i-1)
        if abs( times(i) - times(i-1) - h) > 1e-5
            error('Times are not equidistant');
        end
    else
        error('Times are not monotonically increasing');
    end
end


x = zeros(size(x0,1),N);
x(:,1) = x0;

for i=2:N
      
    x(:,i) = euler(@f, x(:,i-1), u(:,i), h);
    %x(:,i) = rungeKutta(@f, x(:,i-1), u(:,i), h);
              
end

end


function out = avoidSingularity(x) % to avoid singularity if the model

global params;

x(1) = deadZone(x(1), -params.Dz1, params.Dz1);
x(2) = deadZone(x(2), -params.Dz1, params.Dz1);
x(3) = deadZone(x(3), -params.Dz2, params.Dz2);


x(7) = deadZone(x(7), -params.Dz1/params.R, params.Dz1/params.R);
x(8) = deadZone(x(8), -params.Dz1/params.R, params.Dz1/params.R);
x(9) = deadZone(x(9), -params.Dz1/params.R, params.Dz1/params.R);
x(10) = deadZone(x(10), -params.Dz1/params.R, params.Dz1/params.R);

out = x;


end

function next = euler(f, current, input,h)  %this is implemented properly

global params;
persistent xInt;
if isempty(xInt)
    xInt = current;
end

next = limitIntegrators(xInt + h * f(current, input));
xInt = next;
next = avoidSingularity(next);

end

function xInt = limitIntegrators(xInt)  % to avoid singularity if the model

if xInt(7) < 0
    xInt(7) = 0;
end

if xInt(8) < 0
    xInt(8) = 0;
end

if xInt(9) < 0
    xInt(9) = 0;
end

if xInt(10) < 0
    xInt(10) = 0;
end
    
end

function y = deadZone(x, low, high)

    if x < low
        y = x - low;
    else if x > high
        y = x - high;
    else
        y = 0;
    end
end

end

function y = saturation(x, low,high)

if x > high
    y = high;
else if x < low
        y = low;
    else
        y = x;
    end
end

end

function [dX] = f(x, u) %evaluates the derivative of the car dynamics


% params is a structure others are vectors
% state vector [Ux Uy r Ksi x y w1L w1R w2L w2R]'
% input vector [delta brake handbrake throttle]'

global params;
persistent uPrev;

if isempty(uPrev)
    uPrev = u;
end

% x(1) = deadZone(x(1), -params.Dz1, params.Dz1);
% x(2) = deadZone(x(2), -params.Dz1, params.Dz1);
% x(3) = deadZone(x(3), -params.Dz2, params.Dz2);
%
%
% x(7) = deadZone(x(7), -params.Dz1/params.R, params.Dz1/params.R);
% x(8) = deadZone(x(8), -params.Dz1/params.R, params.Dz1/params.R);
% x(9) = deadZone(x(9), -params.Dz1/params.R, params.Dz1/params.R);
% x(10) = deadZone(x(10), -params.Dz1/params.R, params.Dz1/params.R);

%u(1) =rateLimiter(u(1), uPrev(1), params.maxDeltaRate);
%u(2) =rateLimiter(u(2), uPrev(2), params.maxBrakeRate);
%u(3) =rateLimiter(u(3), uPrev(3), params.maxHandbrakeRate);
%u(4) =rateLimiter(u(4), uPrev(4), params.maxThrottleRate);
% modification by mheim
%u(1) = DeltaRate
%u(2) = leftRearTorque
%u(3) = rightRearTorque


[ FORCES, forces] = tires(x,u);
%[brakeTorques] = brakes(x, u, forces);
[brakeTorques] = zeros(4,1);%not used
[torques] = motorTorques(u);

rollFric = params.m*params.g*params.muRoll;
du = 1/params.m*(deadZone(sum(FORCES(1:4)) + params.m*x(3)*x(2), -rollFric, rollFric)  - coulombFriction(x(1)));
dv = 1/params.m*(deadZone(sum(FORCES(5:8)) - params.m*x(1)*x(3), -rollFric, rollFric) -  0*coulombFriction(x(2)));
dr = 1/params.Iz * (params.lF*(sum(FORCES(5:6))) - params.lR*(sum(FORCES(7:8))) + params.lw * (FORCES(2) + FORCES(4) - FORCES(1) - FORCES(3)));
dKsi = x(3);
dx = x(1) * cos(x(4)) - x(2) * sin(x(4));
dy = x(1) * sin(x(4)) + x(2) * cos(x(4));
dw1L = 1/params.Iw * (torques(1) + brakeTorques(1) - forces(1)*params.R);
dw1R = 1/params.Iw * (torques(2) + brakeTorques(2) - forces(2)*params.R);
dw2L = 1/params.Iw * (torques(3) + brakeTorques(3) - forces(3)*params.R);
dw2R = 1/params.Iw * (torques(4) + brakeTorques(4) - forces(4)*params.R);

dX = [du dv dr dKsi dx dy dw1L dw1R dw2L dw2R]';

uPrev = u;
end



function [brakeTorques] = brakes(x, u, tireForces)
%edit by mheim: no brakes used at the moment
%if we want to use it again retrieve from original;
brakeTorques = [0,0,0,0]';
end


function [torques] = motorTorques(u)

global params;

%edit by mheim: back wheels have individual torque allocation
leftTorque = u(2);
rightTorque = u(3);

%reqTorque = params.maxTm * throttleCmd;

Tm1L = 0;
Tm1R = 0;
Tm2L = leftTorque;
Tm2R = rightTorque;

torques = [Tm1L; Tm1R; Tm2L; Tm2R];

end

function [friction] = coulombFriction(in)

global params;

friction = sign(in) .* (params.b .* abs(in) + params.fric);

end



function [ FORCES, forces] = tires(x,u)
%#codegen

global params;

eps = 1e-4;

Ux = x(1);
Uy = x(2);
r = x(3);
w1L = x(7);
w1R = x(8);
w2L = x(9);
w2R = x(10);

delta = u(1);


Ux1L = (Ux - r*params.lw)*cos(delta) + (Uy + r*params.lF)*sin(delta);
Uy1L = -(Ux - r*params.lw)*sin(delta) + (Uy + r*params.lF)*cos(delta);

Ux1R = (Ux + r*params.lw)*cos(delta) + (Uy + r*params.lF)*sin(delta);
Uy1R = -(Ux + r*params.lw)*sin(delta) + (Uy + r*params.lF)*cos(delta);

Ux2L = Ux - r*params.lw;
Uy2L = Uy - r*params.lR;

Ux2R = Ux + r*params.lw;
Uy2R = Uy - r*params.lR;


Sx1L = robustDiv2(Ux1L - params.R*w1L, params.R*w1L, eps);
Sy1L = (1+Sx1L) * robustDiv2(Uy1L, Ux1L, eps);

Sx1R = robustDiv2(Ux1R - params.R*w1R, params.R*w1R, eps);
Sy1R = (1+Sx1R) * robustDiv2(Uy1R, Ux1R, eps);

Sx2L = robustDiv2(Ux2L - params.R*w2L, params.R*w2L, eps);
Sy2L = (1+Sx2L) * robustDiv2(Uy2L, Ux2L, eps);

% TODO @jelavice check: change from  Ux1R  to  Ux2R
Sx2R = robustDiv2(Ux2R - params.R*w2R, params.R*w2R, eps);
Sy2R = (1+Sx2R) * robustDiv2(Uy2R, Ux2R, eps);

%S1L = sqrt( (Sx1L)^2 + (Sy1L)^2 );
%S1R = sqrt( (Sx1R)^2 + (Sy1R)^2 );
%S2L = sqrt( (Sx2L)^2 + (Sy2L)^2 );
%S2R = sqrt( (Sx2R)^2 + (Sy2R)^2 );

S1L = hypot(Sx1L, Sy1L);
S1R = hypot(Sx1R, Sy1R);
S2L = hypot(Sx2L, Sy2L);
S2R = hypot(Sx2R, Sy2R);

mu1L = params.D1*sin(params.C1*atan(params.B1*S1L));
mu1R = params.D1*sin(params.C1*atan(params.B1*S1R));
mu2L = params.D2*sin(params.C2*atan(params.B2*S2L));
mu2R = params.D2*sin(params.C2*atan(params.B2*S2R));

mux1L = - mu1L * robustDiv(Sx1L,S1L,eps);
muy1L = - mu1L * robustDiv(Sy1L,S1L,eps);

mux1R = - mu1R * robustDiv(Sx1R,S1R,eps);
muy1R = - mu1R * robustDiv(Sy1R,S1R,eps);

mux2L = - mu2L * robustDiv(Sx2L,S2L,eps);
muy2L = - mu2L * robustDiv(Sy2L,S2L,eps);

mux2R = - mu2R * robustDiv(Sx2R,S2R,eps);
muy2R = - mu2R * robustDiv(Sy2R,S2R,eps);



C1 = -params.mu*mux1L*params.h*sin(delta);
C2 = -params.mu*muy1L*params.h*cos(delta);
C3 = -params.mu*mux1R*params.h*sin(delta);
C4 = -params.mu*muy1R*params.h*cos(delta);
C5 = -params.mu*muy2L*params.h;
C6 = -params.mu*muy2R*params.h;
%
K1 = params.mu*mux1L*params.h*cos(delta);
K2 = params.mu*muy1L*params.h*sin(delta);
K3 = params.mu*mux1R*params.h*cos(delta);
K4 = params.mu*muy1R*params.h*sin(delta);
K5 = params.mu*mux2L*params.h;
K6 = params.mu*mux2R*params.h;
%
A = -params.lw - C1 - C2;
B = params.lw - C3 - C4;
C = -params.lw - C5;
D = params.lw - C6;
E = K1 - K2 - params.lF;
F = K3 - K4 - params.lF;
G = K5 + params.lR;
H = K6 + params.lR;

den = 2*(A*F - E*B - A*G + E*C + B*H - D*F - C*H + D*G);

Fz1L =   params.m*params.g*(B*G - C*F + B*H - D*F - C*H + D*G)/den;
Fz1R = - params.m*params.g*(A*G - E*C + A*H - E*D + C*H - D*G)/den;
Fz2L =   params.m*params.g*(A*F - E*B + A*H - E*D + B*H - D*F)/den;
Fz2R =   params.m*params.g*(A*F - E*B - A*G + E*C - B*G + C*F)/den;


fx1L = params.mu*Fz1L*mux1L;
fy1L = params.mu*Fz1L*muy1L;

fx1R = params.mu*Fz1R*mux1R;
fy1R = params.mu*Fz1R*muy1R;

fx2L = params.mu*Fz2L*mux2L;
fy2L = params.mu*Fz2L*muy2L;

fx2R = params.mu*Fz2R*mux2R;
fy2R = params.mu*Fz2R*muy2R;


% now change coordinate system

Fx1L = fx1L*cos(delta) - fy1L*sin(delta);
Fy1L = fx1L*sin(delta) + fy1L*cos(delta);

Fx1R = fx1R*cos(delta) - fy1R*sin(delta);
Fy1R = fx1R*sin(delta) + fy1R*cos(delta);

Fx2L = fx2L;
Fy2L = fy2L;

Fx2R = fx2R;
Fy2R = fy2R;

%this is in the car coordinate frame
FORCES = [Fx1L, Fx1R, Fx2L, Fx2R, Fy1L, Fy1R, Fy2L, Fy2R, Fz1L, Fz1R, Fz2L, Fz2R];
FORCES = FORCES(:);
%this are in the tire coordinate frame
forces = [fx1L, fx1R, fx2L, fx2R, fy1L, fy1R, fy2L, fy2R];
forces = forces(:);


end


% not sure why I have two of these robustDiv functions, but this setup worked fine
function z = robustDiv(num,den, epsilon)

if den == 0
    if num ~= 0
        z = num/epsilon;
    else
        z = 0; %clamp stuff to zero cause if the slips are zero there won't be aby force anyway
    end
else
    z = num/den;
end

end

function z = robustDiv2(num,den, epsilon)

if den == 0
    if abs(num) > epsilon
        z = num/epsilon;
    else
        z = 0; 
    end
else
    z = num/den;
end

end

