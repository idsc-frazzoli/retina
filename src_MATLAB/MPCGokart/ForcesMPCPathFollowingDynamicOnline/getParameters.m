function p = getParameters(maxspeed, xmaxacc,steeringreg,specificmoi, B1, points)
%%function p = getParameters(maxspeed, xmaxacc,steeringreg,specificmoi, B1, C1, D1, B2, C2, D2, points)
[np,~]=size(points);
p = zeros(3*np+3,1);
p(1)=maxspeed;
p(2)=xmaxacc;
p(3)=steeringreg;
p(4)=specificmoi;
p(5:3*np+4)=points(:);

p(3*np+4+1)=B1;
%p(3*np+4+2)=C1;
%p(3*np+4+3)=D1;
%p(3*np+4+4)=B2;
% p(3*np+4+5)=C2;
% p(3*np+4+6)=D2;
end

