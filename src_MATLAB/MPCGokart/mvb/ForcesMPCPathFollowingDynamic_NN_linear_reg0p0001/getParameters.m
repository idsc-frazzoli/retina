function p = getParameters(maxspeed, xmaxacc,steeringreg,specificmoi,points)
[np,~]=size(points);
p = zeros(3*np+3,1);
p(1)=maxspeed;
p(2)=xmaxacc;
p(3)=steeringreg;
p(4)=specificmoi;
p(5:3*np+4)=points(:);
end

