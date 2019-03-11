function p = getParameters(maxspeed, xmaxacc,steeringreg,points)
[np,~]=size(points);
p = zeros(3*np+3,1);
p(1)=maxspeed;
p(2)=xmaxacc;
p(3)=steeringreg;
p(4:3*np+3)=points(:);
end

