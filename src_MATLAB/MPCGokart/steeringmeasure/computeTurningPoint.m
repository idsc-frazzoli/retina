function [xturn,yturn] = computeTurningPoint(alpha1,alpha2,lw)
%alpha1: left wheel angle
%alpha2: right wheel angle
%lw: distance between front wheels
%get turning point based on front wheel angles
    y1 = @(x)-tan(alpha1).*x;
    %y2 = @(x)-tan(alpha2).*(x-lw);
    xturn = (-tan(alpha2).*lw)./(tan(alpha1)-tan(alpha2));
    yturn = y1(xturn);
end

