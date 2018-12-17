function p = getParameters(maxspeed,points)
[np,~]=size(points);
p = zeros(3*np+1,1);
p(1)=maxspeed;
p(2:3*np+1)=points(:);
end

