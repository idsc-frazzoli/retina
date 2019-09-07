function acc = casadiGetSmoothMaxAcc(x)
    %used for testing before using it in casadi
    cp0 = 1.9173276271;
    cp1 = -0.0113682655;
    cp2 = -0.0150793283;
    cp3 = 0.0023869979;

    cn0 = -1.4265329731;
    cn1 = -0.1612157772;
    cn2 = 0.0503284643;
    cn3 = -0.0048860339;

    cp = @(x)cp0+cp1*x+cp2*x^2+cp3*x^3;
    cn = @(x)cn0+cn1*x+cn2*x^2+cn3*x^3;
    si = @(x)0.5+1.5*x-2*x^3;
    st = 0.5;
    
    posval = cp(st);
    negval = -cn(st);

    if(isa(x,'double'))
        %this is also called with doubles by Forces
        if(x>st)
            acc = cp(x);
        elseif(x>-st)
            acc = negval*(1-si(x))+posval*si(x);
        else
            acc = -cn(-x);
        end
    else

        acc = if_else(x>st,cp(x),...
            if_else(x>-st,negval*(1-si(x))+posval*si(x),...
            -cn(-x)));
    end
end


