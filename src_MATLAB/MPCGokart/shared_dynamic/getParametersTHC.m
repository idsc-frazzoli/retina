function p = getParametersTHC(maxspeed, xmaxacc,steeringreg,specificmoi,FB,FC,FD,RB,RC,RD,b_steer,k_steer,J_steer,plag,plat,pprog,pab,pspeedcost,pslack,ptv,ptau,points)
[np,~]=size(points);
p = zeros(3*np+21,1);
p(1)=maxspeed;
p(2)=xmaxacc;
p(3)=steeringreg;
p(4)=specificmoi;
p(5)=FB;
p(6)=FC;
p(7)=FD;
p(8)=RB;
p(9)=RC;
p(10)=RD;
p(11)=b_steer;
p(12)=k_steer;
p(13)=J_steer;
p(14)=plag;
p(15)=plat;
p(16)=pprog;
p(17)=pab;
p(18)=pspeedcost;
p(19)=pslack;
p(20)=ptv;
p(21)=ptau;
p(22:3*np+21)=points(:);
end

