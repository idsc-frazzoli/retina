function fy = reartyremodel(vy,vx,nf,paras,ab,tv)
    %nf = normal force
    alpha=atan2(vy,vx);
    c = paras.D2*sin(paras.C2*atan(paras.B2*alpha));
    fy = c*nf;
end

