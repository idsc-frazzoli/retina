function res = heatupfunction(x)
    %TODO: use more precise data
    
    p0 = 0.7754;
    p1 = -0.7458;
    p2 = 0.7614;
    
    hu =@(x) x.*x.*p2 + x*p1 + p0;
    dhu =@(x) 2*x*p2 + p1;
    
    %joint point
    %j = -p1/(2*p2);
    j = 0.5;
    
    %get a*x^2+b*x^3 for starting
    %a+2*j = 0
    ab = [j*j,j*j*j;2*j,3*j*j;]\[hu(j),dhu(j)]';
    
    if(isa(x,'double'))
        if(x>j)
            res = hu(x);
        elseif(x>0)
           res = ab(1)*x*x+ab(2)*x*x*x;
        else
            res = 0;
       end
    else
        res = if_else(x<0,0,...
            if_else(x<j,ab(1)*x*x+ab(2)*x*x*x,...
            hu(x)));
        %res = hu(x);
    end
end

