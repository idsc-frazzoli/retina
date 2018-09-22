function [beta1,beta2,betam] = ackermannangles(offset,w,l)
    if(offset>0)
        beta1 = atan2(0.5*w+offset,l)-pi/2;
        beta2 = atan2(-0.5*w+offset,l)-pi/2;
        betam = atan2(offset,l)-pi/2;
    else
        beta1 = atan2(0.5*w+offset,l)+pi/2;
        beta2 = atan2(-0.5*w+offset,l)+pi/2;
        betam = atan2(offset,l)+pi/2;
    end
end