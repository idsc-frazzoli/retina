function f = objectiveHC(z,points,radii,vmax, maxxacc, steeringreg,plag,plat,pprog,pab,pspeedcost,pslack,ptv)
    global index

    %get the fancy spline
    l = 1.19;
    [splx,sply] = casadiDynamicBSPLINE(z(index.s),points);
    [spldx, spldy] = casadiDynamicBSPLINEforward(z(index.s),points);
    [splsx, splsy] = casadiDynamicBSPLINEsidewards(z(index.s),points);
    r = casadiDynamicBSPLINERadius(z(index.s),radii);
    forward = [spldx;spldy];
    sidewards = [splsx;splsy];

    realPos = z([index.x,index.y]);
    centerPos = realPos;
    wantedpos = [splx;sply];
    error = centerPos-wantedpos;
    lagerror = forward'*error;
    laterror = sidewards'*error;
    speedcost = speedPunisher(z(index.v),vmax)*pspeedcost; % ~max(v-vmax,0);
    slack = z(index.slack);
    tv = z(index.tv);
    lagcost = plag*lagerror^2;
    latcost = plat*laterror^2;
    prog = -pprog*z(index.ds);
    reg = z(index.dotab).^2*pab+z(index.dotbeta).^2*steeringreg;
    f = lagcost+latcost+reg+prog+pslack*slack+speedcost+ptv*tv^2;
end
