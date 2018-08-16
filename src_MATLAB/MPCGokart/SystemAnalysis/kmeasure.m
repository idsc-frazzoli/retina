function [nx,nP] = kmeasure(x,P,h,Hx,z,R)
    y = z - h(x);
    S = Hx*P*Hx'+R;
    K = P*Hx'*inv(S);
    nx = x+K*y;
    nP = (eye(numel(y))-K*Hx)*P;
end