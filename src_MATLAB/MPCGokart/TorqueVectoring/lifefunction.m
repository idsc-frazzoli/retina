close all
clear all
f = figure;
global beta power slip v;
v=0;
bv = uicontrol('Parent',f,'Style','slider','Position',[81,144,419,23],...
              'value',v, 'min',0, 'max',10);
          
beta = 0;
bbeta = uicontrol('Parent',f,'Style','slider','Position',[81,114,419,23],...
              'value',beta, 'min',-1, 'max',1);
          
power = 0;
bpower = uicontrol('Parent',f,'Style','slider','Position',[81,84,419,23],...
              'value',power, 'min',-1, 'max',1);
          
slip = 0;
bslip = uicontrol('Parent',f,'Style','slider','Position',[81,54,419,23],...
              'value',slip, 'min',-1, 'max',1);
          
bv.Callback = @savev;
bbeta.Callback = @savebeta;
bpower.Callback = @savepower;
bslip.Callback = @saveslip;

function savev(src,event)
global beta power slip v;
    v = src.Value;
    plotPower();
end
function savebeta(src,event)
global beta power slip v;
    beta = -src.Value;
    plotPower();
end
function savepower(src,event)
global beta power slip v;
     power = src.Value;
     plotPower();
end
function saveslip(src,event)
global beta power slip v;
    slip = -src.Value;
    plotPower();
end

function plotPower()
    global beta power slip v;
    rreal = tan(beta)*v*1+slip;
    %[l,r,il,ir,pv,dv]=backtorques(beta,rreal,v,power);
    [l,r,il,ir,pv,dv]=backtorquesSimplified(beta,rreal,v,power);
    bar([l,r,il,ir])
    text(0.1,0.6,num2str(pv,'power violated: %10.3f\n'))
    text(0.1,0.5,num2str(dv,'diff violated: %10.3f\n'))
    text(0.1,0.4,num2str(v,'v: %10.3f\n'))
    text(0.1,0.3,num2str(beta,'beta: %10.3f\n'))
    text(0.1,0.2,num2str(power,'power: %10.3f\n'))
    text(0.1,0.1,num2str(slip,'slip: %10.3f\n'))
    axis([0 5 -1.3 1.3])
end