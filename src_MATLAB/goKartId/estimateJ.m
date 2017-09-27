function Jest = estimateJ(vel,idsVel, torque, idsTorque, timeVel)
%estimates moment of inertia
%has many dumb arguments cause we see that time scale is different for 
% velocity measurement and for the torque measurement



if (mod(length(idsVel),2) == 1)
   warning('Length of idsVel is odd. Ain''t no good.') 
end

if (length(idsTorque) ~= length(idsVel))
   warning('Ids have different lengths. Ain''t no good.') 
end

Jsum  = 0;
N = length(idsVel) / 2;

%lop over and approximate dw/dt
for i=1:2:length(idsVel)
    
    dw = vel(idsVel(i+1)) - vel(idsVel(i));
    dt = timeVel(idsVel(i+1)) - timeVel(idsVel(i));
    
    if (dw == 0)
        N = N-1;
        continue;
    end
    
    Jsum = Jsum + torque(idsTorque(i+1)) * dt/dw;

    
end

Jest = Jsum / N;
end