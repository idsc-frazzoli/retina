function f = objective_PG(z,points,radii,points_k2,radii_k2,vmax, maxxacc, steeringreg,plag,plat,pprog,pab,pspeedcost,pslack,pslack2,ptv)
    global index

    %get the fancy spline
    [splx,sply] = casadiDynamicBSPLINE(z(index.s),points);
    [spldx, spldy] = casadiDynamicBSPLINEforward(z(index.s),points);
    [splsx, splsy] = casadiDynamicBSPLINEsidewards(z(index.s),points);
    r = casadiDynamicBSPLINERadius(z(index.s),radii);
    
    [splx_k2,sply_k2] = casadiDynamicBSPLINE(z(index.s_k2),points_k2);
    [spldx_k2, spldy_k2] = casadiDynamicBSPLINEforward(z(index.s_k2),points_k2);
    [splsx_k2, splsy_k2] = casadiDynamicBSPLINEsidewards(z(index.s_k2),points_k2);
    r_k2 = casadiDynamicBSPLINERadius(z(index.s_k2),radii_k2);
    
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
    
    forward_k2 = [spldx_k2;spldy_k2];
    sidewards_k2 = [splsx_k2;splsy_k2];

    realPos_k2 = z([index.x_k2,index.y_k2]);
    centerPos_k2 = realPos_k2;
    wantedpos_k2 = [splx_k2;sply_k2];
    error_k2 = centerPos_k2-wantedpos_k2;
    lagerror_k2 = forward_k2'*error_k2;
    laterror_k2 = sidewards_k2'*error_k2;
    speedcost_k2 = speedPunisher(z(index.v_k2),vmax)*pspeedcost; % ~max(v-vmax,0);
    slack_k2 = z(index.slack_k2);
    tv_k2 = z(index.tv_k2);
    lagcost_k2 = plag*lagerror_k2^2;
    latcost_k2 = plat*laterror_k2^2;
    prog_k2 = -pprog*z(index.ds_k2);
    reg_k2 = z(index.dotab_k2).^2*pab+z(index.dotbeta_k2).^2*steeringreg;
    
    slack2 = z(index.slack2);
    f = lagcost+latcost+reg+prog+pslack*slack+speedcost+ptv*tv^2+...
        lagcost_k2+latcost_k2+reg_k2+prog_k2+pslack*slack_k2+speedcost_k2+ptv*tv_k2^2+pslack2*slack2;
end
