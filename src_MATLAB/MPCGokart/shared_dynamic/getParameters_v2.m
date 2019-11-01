function p = getParameters_v2(maxspeed, xmaxacc,steeringreg,specificmoi,points,stateVeh)
[np,~]=size(points);
[~,ns]=size(stateVeh);
p = zeros(3*np+4+ns,1);
p(1)=maxspeed;
p(2)=xmaxacc;
p(3)=steeringreg;
p(4)=specificmoi;
p(5:3*np+4)=points(:);
p(3*np+5:3*np+4+ns)=stateVeh';
end

