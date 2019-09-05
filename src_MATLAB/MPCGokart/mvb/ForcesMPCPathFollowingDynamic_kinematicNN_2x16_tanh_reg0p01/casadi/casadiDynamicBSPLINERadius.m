function rr = casadiDynamicBSPLINERadius(x,radii)
    [n,~] = size(radii);
    x = max(x,0);
    x = min(x,n-2);
    import casadi.*
    %position in basis function
    if isa(x, 'double')
        v = zeros(n,1);
        b = zeros(n,1);
    else
        v = SX.zeros(n,1);
        b = SX.zeros(n,1);
    end
    for i = 1:n
       v(i,1)=x-i+3;
       vv = v(i,1);
       if isa(vv, 'double')
          if vv<0
              b(i,1)=0;
          elseif vv<1
             b(i,1)=0.5*vv^2;
          elseif vv<2
              b(i,1)=0.5*(-3+6*vv-2*vv^2);
          elseif vv<3
              b(i,1)=0.5*(3-vv)^2;
          else
              b(i,1)=0;
          end
       else
           b(i,1) = if_else(vv<0,0,...
               if_else(vv<1,0.5*vv^2,...
           if_else(vv<2,0.5*(-3+6*vv-2*vv^2),...
           if_else(vv<3,0.5*(3-vv)^2,0)))); 
       end
    end
    rr = b'*radii;
end

