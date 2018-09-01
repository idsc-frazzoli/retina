%code by mheim
function F = getEvolution(x)
    %get state matrix
    %states (everything is in global frame)
    %[x,y,theta,dotx, doty, dottheta, dotdotx, dotdoty,driftx,drifty]
    % TODO describe what driftx and drifty represent
    %do it for every line
    F = zeros(10);
    F(1,4) = 1; % dotx*t adds to x
    F(2,5) = 1; % doty*t adds to y
    F(3,6) = 1; % dottheta*t adds to theta
    F(4,7) = 1; % dotdotx*t adds to dotx
    F(5,8) = 1; % dotdoty*t adds to doty
end
