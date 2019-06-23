function acc = finalpower(vel,pow)
    rampTop = 1300;
    rampBot = -660;
    p0 = -0.3223;
    ppower = 0.001855;
    pvel = -0.0107;
    ptlramp = -10;
    st = 0.5;
	acc = accuratepowerfunction(vel,pow,p0,ppower,pvel,st, rampBot, ptlramp, rampTop);
end

