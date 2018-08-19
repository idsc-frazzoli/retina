function [sx,sP] = RTSSmoother(hx,hP,hQ,hF)
    %N: number of datapoints
    %m: number of states
    %hx: history of x: Nxm
    %hP: history of P: Nxmxm
    %hQ: history of Q: Nxmxm
    %hF: history of F: Nxmxm
    %out: smoothed x and P
    [N,m]=size(hx);
    if(N<m)
        hx = hx';
        [N,m]=size(hx);
    end
    %!!!ensure right usage of dimensions in hP and hQ!!!
    sx = zeros(N,m);
    sP = zeros(N,m,m);
    sx(N,:)=hx(N,:);
    sP(N,:,:)=hP(N,:,:);
    for i = N-1:-1:1
        if i == 3
            i
        end
        F = squeeze(hF(i,:,:));
        P = squeeze(hP(i,:,:));
        x = squeeze(hx(i,:))';
        Q = squeeze(hQ(i,:,:));
        sxp = squeeze(sx(i+1,:))';
        sPp = squeeze(sP(i+1,:,:));
        tx = F*x;
        tP = F*P*F'+Q;
        G = P*F*inv(tP);
        sx(i,:)=x+G*(sxp-tx);
        sP(i,:,:)=P+G*(sPp-tP)*G';
    end
end