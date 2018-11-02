function v = nlconst(z)
%NLCONST Summary of this function goes here
%   Detailed explanation goes here
l = 1;
v1 = (tan(z(9))*z(8)^2/l)^2+z(1)^2;
v2 = z(1)-casadiGetMaxAcc(z(8));
v3 = casadiGetMaxNegAcc(z(8))-z(1);
%v2 = -1;
v = [v1;v2;v3];
end

