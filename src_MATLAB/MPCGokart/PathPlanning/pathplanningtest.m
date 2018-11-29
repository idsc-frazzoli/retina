addpath('..')  

userdir = getuserdir

A = imread(strcat(getuserdir,'/Pictures/pathplanningtest.png'));
%A = imread(strcat(getuserdir,'/Pictures/pathplanningtests.png'));
O = imbinarize(A(:,:,1));
O = O>0.5;
image(A);
sp = [20;10];
%sp = [2;1];

CC = bwconncomp(O,4);
numPixels = cellfun(@numel,CC.PixelIdxList);
[biggest,idx] = min(numPixels);
O(CC.PixelIdxList{idx}) = 0;

%heatmap
[m,n]=size(O);
H = zeros(m,n);
%only used for experimentation
nn = [-1,0;1,0;0,-1;0,1];
[nnn,~]=size(nn);
hu = 0.1;
%very slow but works
for i = 1:2000
    i
    Hn = zeros(m,n,nnn);
    Ho = zeros(m,n,nnn);
    for inn = 1:nnn
        Hn(:,:,inn)=circshift(H,nn(inn,:));
        Ho(:,:,inn)=circshift(O,nn(inn,:));
    end
    for ix = 1:m
        for iy = 1:n
            if(O(ix,iy))
                fvals =  Hn(ix,iy,:);
                fvals = fvals(:);
                sel = Ho(ix,iy,:);
                sel = sel(:);
                vals = fvals(sel>0.5);
                H(ix,iy) = mean(vals);
            end
        end
    end
    H = H + hu;
    H(sp(1),sp(2))=0;
    H(~O)=0;
end
H(sp(1),sp(2))=0;
[tx ty] = find(H == max(H(:)))
%imagesc(H)

%dirichlet
D = zeros(m,n);
for i = 1:1000
    Dn = zeros(m,n);
    for inn = 1:nnn
        Dn = Dn + circshift(D,nn(inn,:));
    end
    Dn = Dn/4;
    D = Dn;
    D(~O)=1;
    D(tx,ty)=0;
end
imagesc(D)
sobelx = [1 0 -1; 2 0 -2; 1 0 -1];
sobely = sobelx';
Dx = conv2(D,sobelx,'same');
Dy = conv2(D,sobely,'same');
%find path
[X,Y] = meshgrid(1:m,1:n);
x = sp(1);
y = sp(2);
pos = [];
for i = 1:100
    xder = D(cx,fy)-D(fx,fy);
    yder = D(fx,cy)-D(fx,fy);
    dernorm = norm([xder,yder]);
    xder =xder/dernorm;
    yder =yder/dernorm;
    xder
    yder
    x = x - xder*0.5;
    y = y - yder*0.5;
    pos = [pos;[x,y]];
end
image(A);
hold
plot(pos(:,2),pos(:,1));