function p = getParameters(maxspeed, xmaxacc,ymaxacc,points)
[np,~]=size(points);
p = zeros(3*np+3,1);
p(1)=maxspeed;
p(2)=xmaxacc;
p(3)=ymaxacc;
p(4:3*np+3)=points(:);
end

