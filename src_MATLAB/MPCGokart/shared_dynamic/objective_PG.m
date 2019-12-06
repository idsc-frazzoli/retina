function f = objective_PG(z,points,radii,points2,radii2,vmax, maxxacc, steeringreg,plag,plat,pprog,pab,pspeedcost,pslack,pslack2,ptv)
    global index

    %get the fancy spline for gokart 1
    [splx,sply] = casadiDynamicBSPLINE(z(index.s),points);
    [spldx, spldy] = casadiDynamicBSPLINEforward(z(index.s),points);
    [splsx, splsy] = casadiDynamicBSPLINEsidewards(z(index.s),points);

    %get the fancy spline for gokart 2
    [splx2,sply2] = casadiDynamicBSPLINE(z(index.s_k2),points2);
    [spldx2, spldy2] = casadiDynamicBSPLINEforward(z(index.s_k2),points2);
    [splsx2, splsy2] = casadiDynamicBSPLINEsidewards(z(index.s_k2),points2);
    
    %cost function for kart 1
    
    forward = [spldx;spldy];
    sidewards = [splsx;splsy];
    realPos = z([index.x,index.y]);
    centerPos = realPos;
    wantedpos = [splx;sply];
    error = centerPos-wantedpos;
    lagerror = forward'*error;
    laterror = sidewards'*error;
    speedcost = speedPunisher(z(index.v),vmax)*pspeedcost;
    slack = z(index.slack);
    tv = z(index.tv);
    lagcost = plag*lagerror^2;
    latcost = plat*laterror^2;
    prog = -pprog*z(index.ds);
    reg = z(index.dotab).^2*pab+z(index.dotbeta).^2*steeringreg;
    
    %cost function for kart 2
    
    forward2 = [spldx2;spldy2];
    sidewards2 = [splsx2;splsy2];
    realPos2 = z([index.x_k2,index.y_k2]);
    centerPos2 = realPos2;
    wantedpos2 = [splx2;sply2];
    error2 = centerPos2-wantedpos2;
    lagerror2 = forward2'*error2;
    laterror2 = sidewards2'*error2;
    speedcost2 = speedPunisher(z(index.v_k2),vmax)*pspeedcost;
    slack_k2 = z(index.slack_k2);
    tv2 = z(index.tv_k2);
    lagcost2 = plag*lagerror2^2;
    latcost2 = plat*laterror2^2;
    prog2 = -pprog*z(index.ds_k2);
    reg2 = z(index.dotab_k2).^2*pab+z(index.dotbeta_k2).^2*steeringreg;
    
    %shared
    slack2=z(index.slack2);
    
    f = lagcost+latcost+reg+prog+pslack*slack+speedcost+ptv*tv^2+...%
        lagcost2+latcost2+reg2+prog2+pslack*slack_k2+speedcost2+ptv*tv2^2+pslack2*slack2;%-0.01*sidewardsspeed^2;
end
