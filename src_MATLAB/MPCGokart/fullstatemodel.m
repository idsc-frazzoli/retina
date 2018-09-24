function [dX] = fullstatemodel(x, u) %evaluates the derivative of the car dynamics


% params is a structure others are vectors
% state vector [Ux Uy r Ksi x y w2L w2R]'
% input vector [delta brake leftthrottle rightthrottle]'

global params;

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



%x = []
%u,v = speed in body frame
%Ksi = orientation
%r = rotational speed

[ FORCES, forces] = tires(x,u);
[brakeTorques] = brakes(x, u, forces);
[torques] = motorTorques(u,x);

rollFric = params.m*params.g*params.muRoll;
du = 1/params.m*(deadZone(sum(FORCES(1:4)) + params.m*x(3)*x(2), -rollFric, rollFric)  - coulombFriction(x(1)));
dv = 1/params.m*(deadZone(sum(FORCES(5:8)) - params.m*x(1)*x(3), -rollFric, rollFric) -  0*coulombFriction(x(2)));
dr = 1/params.Iz * (params.lF*(sum(FORCES(5:6))) - params.lR*(sum(FORCES(7:8))) + params.lw * (FORCES(2) + FORCES(4) - FORCES(1) - FORCES(3)));
dKsi = x(3);
dx = x(1) * cos(x(4)) - x(2) * sin(x(4));
dy = x(1) * sin(x(4)) + x(2) * cos(x(4));
dw2L = 1/params.Iw * (torques(3) + brakeTorques(3) - forces(3)*params.R);
dw2R = 1/params.Iw * (torques(4) + brakeTorques(4) - forces(4)*params.R);

%dw2L = 0;
%dw2R = 0;
dX = [du dv dr dKsi dx dy dw2L dw2R]';

uPrev = u;
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



function xInt = limitIntegrators(xInt)  % to avoid singularity if the model

if xInt(7) < 0
    xInt(7) = 0;
end

if xInt(8) < 0
    xInt(8) = 0;
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

function [brakeTorques] = brakes(x, u, tireForces)

global params;

brakeCmd = u(2);

Tb1L = 0;
Tb1R = 0;
Tb2L = 0;
Tb2R = 0;

w2L = x(7);
w2R = x(8);

masterPress = params.maxPress * saturation(brakeCmd,0,1);
pressF = masterPress;

%simplified brake model

if masterPress > 0
    
    if w2L ~= 0
        Tb2L = Tb2L - pressR * params.press2torR*sign(w2L);
    end
    
    if w2R ~= 0
        Tb2R = Tb2R - pressR * params.press2torR*sign(w2R);
    end
end

brakeTorques = [Tb1L, Tb1R, Tb2L, Tb2R];
brakeTorques = brakeTorques(:);


end


function [torques] = motorTorques(u,x)

global params;

leftThrottle = saturation(u(3),0,params.maxThrottle)*0.1;
rightThrottle = saturation(u(4),0,params.maxThrottle)*0.1;

Tm2L = leftThrottle/max(x(7),1);
Tm2R = rightThrottle/max(x(8),1);

torques = [0; 0; Tm2L; Tm2R]

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
w2L = x(7);
w2R = x(8);


delta = u(1);

Ux1L = (Ux - r*params.lw)*cos(delta) + (Uy + r*params.lF)*sin(delta);
Uy1L = -(Ux - r*params.lw)*sin(delta) + (Uy + r*params.lF)*cos(delta);

w1L = Ux1L/params.R;

Ux1R = (Ux + r*params.lw)*cos(delta) + (Uy + r*params.lF)*sin(delta);
Uy1R = -(Ux + r*params.lw)*sin(delta) + (Uy + r*params.lF)*cos(delta);

w1R = Ux1R/params.R;

Ux2L = Ux - r*params.lw;
Uy2L = Uy - r*params.lR;

Ux2R = Ux + r*params.lw;
Uy2R = Uy - r*params.lR;


%Sx1L = 0;
Sy1L = robustDiv2(Uy1L, Ux1L, eps);

%Sx1R = 0;
Sy1R = robustDiv2(Uy1R, Ux1R, eps);

Sx2L = robustDiv2(Ux2L - params.R*w2L, params.R*w2L, eps);
Sy2L = (1+Sx2L) * robustDiv2(Uy2L, Ux2L, eps);

% TODO @jelavice check: change from  Ux1R  to  Ux2R
Sx2R = robustDiv2(Ux2R - params.R*w2R, params.R*w2R, eps);
Sy2R = (1+Sx2R) * robustDiv2(Uy2R, Ux2R, eps);

%S1L = sqrt( (Sx1L)^2 + (Sy1L)^2 );
%S1R = sqrt( (Sx1R)^2 + (Sy1R)^2 );
%S2L = sqrt( (Sx2L)^2 + (Sy2L)^2 );
%S2R = sqrt( (Sx2R)^2 + (Sy2R)^2 );

S1L = Sy1L;
S1R = Sy1R;
S2L = hypot(Sx2L, Sy2L);
S2R = hypot(Sx2R, Sy2R);

mu1L = params.D1*sin(params.C1*atan(params.B1*S1L));
mu1R = params.D1*sin(params.C1*atan(params.B1*S1R));
mu2L = params.D2*sin(params.C2*atan(params.B2*S2L));
mu2R = params.D2*sin(params.C2*atan(params.B2*S2R));

mux1L = 0;
muy1L = - mu1L * robustDiv(Sy1L,S1L,eps);

mux1R = 0;
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


%fx1L = 0;
fy1L = params.mu*Fz1L*muy1L;

%fx1R = 0;
fy1R = params.mu*Fz1R*muy1R;

fx2L = params.mu*Fz2L*mux2L;
fy2L = params.mu*Fz2L*muy2L;

fx2R = params.mu*Fz2R*mux2R;
fy2R = params.mu*Fz2R*muy2R;


% now change coordinate system

Fx1L = - fy1L*sin(delta);
Fy1L = fy1L*cos(delta);

Fx1R = - fy1R*sin(delta);
Fy1R = fy1R*cos(delta);

Fx2L = fx2L;
Fy2L = fy2L;

Fx2R = fx2R;
Fy2R = fy2R;

%this is in the car coordinate frame
FORCES = [Fx1L, Fx1R, Fx2L, Fx2R, Fy1L, Fy1R, Fy2L, Fy2R, Fz1L, Fz1R, Fz2L, Fz2R];
FORCES = FORCES(:);
%this are in the tire coordinate frame
forces = [0, 0, fx2L, fx2R, fy1L, fy1R, fy2L, fy2R];
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


