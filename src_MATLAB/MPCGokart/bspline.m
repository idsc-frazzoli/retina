%code by mheim
% TODO state reference for equations
function r = bspline(points, u, order, der, circ)
    %equally spaced bspline
    %delivers also derivativs
    function nn = basis(u,i,p)
        [su, ~]=size(u);
        [si, ~]=size(i);
        nn = zeros(su,si);
        cu=1;
        for ui=u'
            ci=1;
            for ii = i'
                if(p<0)
                    n=0;
                elseif(p==0)
                    if(ii-1<=ui & ui<ii)
                        n = 1;
                    else
                        n = 0;
                    end
                else
                    n = basis(ui,ii,p-1)*(ui-ii+1)+ basis(ui,ii+1,p-1)*(ii+p-ui);
                    n = n/p;
                end
                nn(cu,ci)=n;
                ci = ci + 1;
            end
            cu = cu + 1;
        end
    end

    %x = 0:0.1:10
    %y = basis(x,1,order,der)

    %circularize
    if(circ)
        points = [points;points(1:order,:)];
    end
    
    [m, ~] = size(points);
    if der == 0
       i = 1:m;
       i = i';
       [mm,~]=size(u);
       u = u+ones(mm,1)*order;
       nn = basis(u,i, order);
       lastu = m;
       uu = find(u>lastu,1,'first');
       r = nn*points;
       if(~isempty(uu))
            r = [r(1:uu-1,:);zeros(mm-uu+1,1)];
       end
    else
       q = points(2:m)-points(1:m-1);
       r = bspline(q, u,order -1, der-1, 0);
    end
end
