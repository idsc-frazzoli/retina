function [xx,yy] = casadiBSPLINE(x,points)
    [n,~] = size(points);
    import casadi.*
    %position in basis function
    v = SX.zeros(n,1);
    b = SX.zeros(n,1);
    for i = 1:n
       o = i-1;
       %v(i,1)=mod(x-o,n);DOESN'T SEEM TO WORK
       %workaround
       r = floor((x-o)/n)*n;
       v(i,1)=x-o-r;
       vv = v(i,1);
       b(i,1) = if_else(vv<1,0.5*vv^2,...
           if_else(vv<2,0.5*(-3+6*vv-2*vv^2),...
           if_else(vv<3,0.5*(3-vv)^2,0)));
    end
    xx = b'*points(:,1);
    yy = b'*points(:,2);
end

