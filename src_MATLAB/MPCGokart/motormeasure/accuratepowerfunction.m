function acc = accuratepowerfunction(vel,pow,p0,ppower,pvel,speedthreshold, powerthresholdlow, ptlramp, powerthresholdhigh)
    function acc = rampfun(pow,vel)
        acc = p0+ppower*pow+pvel*vel;
    end
    %sfpos,sfneg, powerthreshold
    function acc = forwardacc(pow,vel)
        if(pow<powerthresholdlow+ptlramp*vel)
           acc = rampfun(powerthresholdlow+ptlramp*vel,vel);
        elseif(pow<powerthresholdhigh)
           acc = rampfun(pow,vel);
        else
           acc = rampfun(powerthresholdhigh,vel);
        end
    end
    function acc = backwardacc(pow,vel)
        acc = -forwardacc(-pow,vel);
    end

    if(vel>speedthreshold)
        acc = forwardacc(pow,vel);
    elseif(vel<-speedthreshold)
        acc = backwardacc(pow,vel);
    else
        forval = forwardacc(pow,vel);
        bacval = backwardacc(pow,vel);
        acc = interp1([speedthreshold,-speedthreshold],[forval,bacval],vel);
    end
end

