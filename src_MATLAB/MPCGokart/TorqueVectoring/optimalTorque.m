function [t1,t2] = optimalTorque(wantedpower, v,beta,mr)
rc = 1;
kcomp = 100;
kcorr = 100;
wr = tan(beta)*v*rc;
pcomp = tan(beta)*v^2*kcomp;%price for compensation
pcorr = (wr-mr)*kcorr;%price for correction
ppow = 1;%price for holding power level
obj = @(t)(t(1)-t(2))^2-(pcomp+pcorr)*(t(1)-t(2))+(t(1)+t(2)-wantedpower)^2*ppow;

A=[eye(2);-eye(2)];
b = [1000,1000,1000,1000]';
t = fmincon(obj,[0,0]',A,b);
t1 = t(1);
t2 = t(2);
end

