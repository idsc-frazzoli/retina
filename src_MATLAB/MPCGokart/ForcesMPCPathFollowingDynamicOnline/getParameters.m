function p = getParameters(maxspeed, xmaxacc,steeringreg,specificmoi, B1, C1, D1, B2, C2, D2, points)
[np,~]=size(points);
p = zeros(3*np+3,1);
p(1)=maxspeed;
p(2)=xmaxacc;
p(3)=steeringreg;
p(4)=specificmoi;
p(5)=B1;
p(6)=C1;
p(7)=D1;
p(8)=B2;
p(9)=C2;
p(10)=D2;
p(11:3*np+10)=points(:);
end

