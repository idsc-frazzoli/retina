function p = getParameters(maxspeed, xmaxacc,points)
[np,~]=size(points);
p = zeros(3*np+2,1);
p(1)=maxspeed;
p(2)=xmaxacc;
p(3:3*np+2)=points(:);
end

