function fy = fronttyremodel(vy,vx,nf,paras)
    %nf = normal force
    alpha=atan2(vy,vy);
    c = paras.D1*sin(paras.C1*atan(paras.B1*alpha));
    fy = c*nf;
end

