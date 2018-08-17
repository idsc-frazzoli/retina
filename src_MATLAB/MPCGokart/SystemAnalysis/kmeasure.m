function [nx,nP] = kmeasure(x,P,h,Hx,z,R)
    y = z - h;
    S = Hx*P*Hx'+R;
    K = P*Hx'*inv(S);
    nx = x+K*y;
    nP = (eye(numel(x))-K*Hx)*P;
end