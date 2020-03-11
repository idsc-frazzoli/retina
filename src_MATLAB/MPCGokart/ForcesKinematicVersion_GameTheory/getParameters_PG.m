function p = getParameters_PG(maxspeed, xmaxacc,ymaxacc,latacclim,...
                         rotacceffect,torqueveceffect, brakeeffect,...
                         plagerror, platerror, pprog,...
                         pab, pdotbeta, pspeedcost,pslack,pslack2,dist,points,points2)
[np,~]=size(points);
[np2,~]=size(points2);
p = zeros(3*np+3*np2+16,1);
p(1)=maxspeed;
p(2)=xmaxacc;
p(3)=ymaxacc;
p(4)=latacclim;
p(5)=rotacceffect;
p(6)=torqueveceffect;
p(7)=brakeeffect;
p(8)=plagerror;
p(9)=platerror;
p(10)=pprog;
p(11)=pab;
p(12)=pdotbeta;
p(13)=pspeedcost;
p(14)=pslack;
p(15)=pslack2;
p(16)=dist;
p(17:3*np+17-1)=points(:);
p(3*np+17:3*np+3*np2+17-1)=points2(:);
end

