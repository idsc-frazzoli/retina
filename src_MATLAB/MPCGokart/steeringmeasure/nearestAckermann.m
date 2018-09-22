function [abeta1,abeta2,abetam] = nearestAckermann(beta1,beta2,w,l)
    %find nearest ackermann angles
    function c = ackermannopt(offset)
        [obeta1,obeta2,~] = ackermannangles(offset,w,l);
        c = (obeta1-beta1)^2+(obeta2-beta2)^2;
    end
    %opt = @(beta)ackermannangles()

    opt = @(offset)ackermannopt(offset);
    xl = fminsearch(opt, -1);
    xr = fminsearch(opt, 1);
    if(ackermannopt(xl)>ackermannopt(xr))
        [abeta1,abeta2,abetam]=ackermannangles(xr,w,l);
    else
        [abeta1,abeta2,abetam]=ackermannangles(xl,w,l);
    end
end

