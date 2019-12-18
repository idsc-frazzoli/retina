%get the fancy spline
global index
N=31;
splx=zeros(N,1);
sply=zeros(N,1);
spldx=zeros(N,1);
spldy=zeros(N,1);
splsx=zeros(N,1);
splsy=zeros(N,1);
splx_k2=zeros(N,1);
sply_k2=zeros(N,1);
spldx_k2=zeros(N,1);
spldy_k2=zeros(N,1);
splsx_k2=zeros(N,1);
splsy_k2=zeros(N,1);
cost=0;
costA=0;
costB=0;
costS=0;
for zz=1:N
    [splx(zz),sply(zz)] = casadiDynamicBSPLINE(outputM(zz,index.s),nextSplinePoints);
    [spldx(zz), spldy(zz)] = casadiDynamicBSPLINEforward(outputM(zz,index.s),nextSplinePoints);
    [splsx(zz), splsy(zz)] = casadiDynamicBSPLINEsidewards(outputM(zz,index.s),nextSplinePoints);

    [splx_k2(zz),sply_k2(zz)] = casadiDynamicBSPLINE(outputM(zz,index.s_k2),nextSplinePoints_k2);
    [spldx_k2(zz), spldy_k2(zz)] = casadiDynamicBSPLINEforward(outputM(zz,index.s_k2),nextSplinePoints_k2);
    [splsx_k2(zz), splsy_k2(zz)] = casadiDynamicBSPLINEsidewards(outputM(zz,index.s_k2),nextSplinePoints_k2);
    forward = [spldx(zz);spldy(zz)];
    sidewards = [splsx(zz);splsy(zz)];
    
    realPos =[ outputM(zz,index.x),outputM(zz,index.y)]';
    centerPos = realPos;
    wantedpos = [splx(zz);sply(zz)];
    error = centerPos-wantedpos;
    lagerror = forward'*error;
    laterror = sidewards'*error;
    speedcost = speedPunisher(outputM(zz,index.v),maxSpeed)*pspeedcost; % ~max(v-vmax,0);
    slack = outputM(zz,index.slack);
    tv = outputM(zz,index.tv);
    lagcost = plag*lagerror^2;
    latcost = plat*laterror^2;
    prog = -pprog*outputM(zz,index.ds);
    reg = outputM(zz,index.dotab).^2*pab+outputM(zz,index.dotbeta).^2*steeringreg;
    
    forward_k2 = [spldx_k2(zz);spldy_k2(zz)];
    sidewards_k2 = [splsx_k2(zz);splsy_k2(zz)];
    
    realPos_k2 = [ outputM(zz,index.x_k2),outputM(zz,index.y_k2)]';
    centerPos_k2 = realPos_k2;
    wantedpos_k2 = [splx_k2(zz);sply_k2(zz)];
    error_k2 = centerPos_k2-wantedpos_k2;
    lagerror_k2 = forward_k2'*error_k2;
    laterror_k2 = sidewards_k2'*error_k2;
    speedcost_k2 = speedPunisher(outputM(zz,index.v_k2),maxSpeed)*pspeedcost; % ~max(v-vmax,0);
    slack_k2 = outputM(zz,index.slack_k2);
    tv_k2 = outputM(zz,index.tv_k2);
    lagcost_k2 = plag*lagerror_k2^2;
    latcost_k2 = plat*laterror_k2^2;
    prog_k2 = -pprog*outputM(zz,index.ds_k2);
    reg_k2 = outputM(zz,index.dotab_k2).^2*pab+outputM(zz,index.dotbeta_k2).^2*steeringreg;
    
    slack2 = outputM(zz,index.slack2);
    cost = cost+lagcost+latcost+reg+prog+pslack*slack+speedcost+ptv*tv^2+...
        lagcost_k2+latcost_k2+reg_k2+prog_k2+pslack*slack_k2+speedcost_k2+ptv*tv_k2^2+pslack2*slack2;
    costA = costA+lagcost+latcost+reg+prog+pslack*slack+speedcost+ptv*tv^2;
    costB = costB+lagcost_k2+latcost_k2+reg_k2+prog_k2+pslack*slack_k2+speedcost_k2+ptv*tv_k2^2;
    costS = costS+pslack2*slack2;
end

