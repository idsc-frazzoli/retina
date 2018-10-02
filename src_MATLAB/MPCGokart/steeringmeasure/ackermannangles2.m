function [beta1,beta2] = ackermannangles2(betam,w,l)
    offsetsteering = @(betam,wo)atan(tan(betam)/(1+tan(betam)*wo));
    beta1 = offsetsteering(betam,-w/(2*l));
    beta2 = offsetsteering(betam,w/(2*l));
end