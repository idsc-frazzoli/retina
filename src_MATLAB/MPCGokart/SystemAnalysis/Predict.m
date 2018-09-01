%code by mheim
% TODO state reference for equations
function [px,pP] = Predict(x,P,dotx,Fx,dt,Q)
    px = x+dotx*dt;
    DFx = eye(numel(dotx))+dt*Fx;
    pP = DFx*P*DFx'+Q;
end

