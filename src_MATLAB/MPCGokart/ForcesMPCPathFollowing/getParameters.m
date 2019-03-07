function p = getParameters(maxspeed, xmaxacc,ymaxacc,latacclim,rotacceffect,torqueveceffect, brakeeffect,points)
[np,~]=size(points);
p = zeros(3*np+7,1);
p(1)=maxspeed;
p(2)=xmaxacc;
p(3)=ymaxacc;
p(4)=latacclim;
p(5)=rotacceffect;
p(6)=torqueveceffect;
p(7)=brakeeffect;
p(8:3*np+7)=points(:);
end

