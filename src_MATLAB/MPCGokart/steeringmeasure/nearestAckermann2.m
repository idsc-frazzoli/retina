function [abeta1,abeta2,abetam] = nearestAckermann2(beta1,beta2,w,l)
    %find nearest ackermann angles
    function c = ackermannopt(ca)
        [obeta1,obeta2] = ackermannangles2(ca,w,l);
        c = (obeta1-beta1)^2+(obeta2-beta2)^2;
    end
    %opt = @(beta)ackermannangles()

    opt = @(offset)ackermannopt(offset);
    xl = fminsearch(opt, 0);
    [abeta1,abeta2]=ackermannangles2(xl,w,l);
    abetam = xl;
end

