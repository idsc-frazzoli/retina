function p = getParameters(maxspeed,points)
[np,~]=size(points);
p = zeros(2*np+1,1);
p(1)=maxspeed;
p(2:2*np+1)=points(:);
end

