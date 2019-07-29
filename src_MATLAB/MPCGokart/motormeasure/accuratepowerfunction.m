function acc = accuratepowerfunction(vel,pow,p0,ppower,pvel, speedthreshold, powerthresholdlow, powerthresholdhigh)
    function acc = rampfun(pow)
        acc = p0+ppower*pow;
    end
    %sfpos,sfneg, powerthreshold
    function acc = forwardacc(pow)
        if(pow<powerthresholdlow)
           acc = rampfun(powerthresholdlow);
        elseif(pow<powerthresholdhigh)
           acc = rampfun(pow);
        else
           acc = rampfun(powerthresholdhigh);
        end
    end
    function acc = backwardacc(pow)
        acc = -forwardacc(-pow);
    end

    if(vel>speedthreshold)
        acc = forwardacc(pow);
    elseif(vel<-speedthreshold)
        acc = backwardacc(pow);
    else
        forval = forwardacc(pow);
        bacval = backwardacc(pow);
        acc = interp1([speedthreshold,-speedthreshold],[forval,bacval],vel);
    end
    acc = acc +pvel*vel;
end

