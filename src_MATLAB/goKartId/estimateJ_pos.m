function Jest = estimateJ_pos(pos,idsPos, torque, idsTorque, timePos)
%estimates moment of inertia
%has many dumb arguments cause we see that time scale is different for 
% velocity measurement and for the torque measurement



if (mod(length(idsPos),2) == 1)
   warning('Length of idsVel is odd. Ain''t no good.') 
end

if (length(idsTorque) ~= length(idsPos))
   warning('Ids have different lengths. Ain''t no good.') 
end

Jsum  = 0;
N = floor(length(idsPos) / 2);

%lop over and approximate dw/dt
for i=1:2:length(idsPos)
    
    da = pos(idsPos(i+1)) - pos(idsPos(i));
    dt = timePos(idsPos(i+1)) - timePos(idsPos(i));
    
    if (da == 0)
        N = N-1;
        continue;
    end
    
    Jsum = Jsum + torque(idsTorque(i+1)) * dt^2/da/2;

    
end

Jest = Jsum / N;
end