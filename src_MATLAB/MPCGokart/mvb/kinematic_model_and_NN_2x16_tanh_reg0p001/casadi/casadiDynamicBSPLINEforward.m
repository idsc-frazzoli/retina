function [xx,yy] = casadiDynamicBSPLINEforward(x,points)
    [n,~] = size(points);
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
             b(i,1)=vv;
          elseif vv<2
              b(i,1)=3-2*vv;
          elseif vv<3
              b(i,1)=-3+vv;
          else
              b(i,1)=0;
          end
       else
           b(i,1) = if_else(vv<0,0,...
               if_else(vv<1,vv,...
           if_else(vv<2,3-2*vv,...
           if_else(vv<3,-3+vv,0)))); 
       end
    end
    xx = b'*points(:,1);
    yy = b'*points(:,2);
    norm = (xx^2+yy^2)^0.5;
    xx = xx/norm;
    yy = yy/norm;
end

