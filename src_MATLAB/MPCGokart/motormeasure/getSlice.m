function [p0,p1,p2,p3] = getSlice(surf,y)
    coeffvals = coeffvalues(surf);
    p00 = coeffvals(1);
    p10 = coeffvals(2);
    p01 = coeffvals(3);
    p20 = coeffvals(4);
    p11 = coeffvals(5);
    p02 = coeffvals(6);
    p30 = coeffvals(7);
    p21 = coeffvals(8);
    p12 = coeffvals(9);
    p03 = coeffvals(10);
    
    p0 = p00+p01*y+p02*y^2+p03*y^3;
    p1 = p10+p11*y+p12*y^2;
    p2 = p20+p21*y;
    p3 = p30;
end

