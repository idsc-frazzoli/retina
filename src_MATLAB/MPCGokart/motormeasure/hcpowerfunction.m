function acc = hcpowerfunction(vel,pow,sfpos,sfneg, speedthreshold, powerthreshold)
    %sfpos,sfneg, powerthreshold
    function acc = forwardacc(vel,pow)
        if(pow>powerthreshold)
            acc = feval(sfpos,[vel,pow]);
        elseif(pow<-powerthreshold)
            acc = feval(sfneg,[vel,pow]);
        else
            posval = feval(sfpos,[vel,powerthreshold]);
            negval = feval(sfneg,[vel,-powerthreshold]);
            acc = interp1([-powerthreshold,powerthreshold],[negval,posval],pow);
        end
    end
    function acc = backwardacc(vel,pow)
        acc = -forwardacc(-vel,-pow);
    end

    if(vel>speedthreshold)
        acc = forwardacc(vel,pow);
    elseif(vel<-speedthreshold)
        acc = backwardacc(vel,pow);
    else
        forval = forwardacc(speedthreshold,pow);
        bacval = backwardacc(-speedthreshold,pow);
        acc = interp1([speedthreshold,-speedthreshold],[forval,bacval],vel);
    end
end

