function y = satfun(x)
    l = 0.8;
    r = 1-l;
    if isa(x, 'double')
        if(x<l)
            y=x;
        elseif(x<1+r)
            d = (1+r-x)/r;
            y = 1-1/4*r*d^2;
        else
            y = 1;
        end
    else
        d = (1+r-x)/r;
        y=0.95*if_else(x<l,x,...
            if_else(x<1+r,1-1/4*r*d^2,1));
    end
end

