function b = qsplinebf(vv)
      if vv<0
          b=0;
      elseif vv<1
         b=0.5*vv^2;
      elseif vv<2
          b=0.5*(-3+6*vv-2*vv^2);
      elseif vv<3
          b=0.5*(3-vv)^2;
      else
          b=0;
      end
end

