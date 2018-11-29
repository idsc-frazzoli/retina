addpath('..')  

userdir = getuserdir

%A = imread(strcat(getuserdir,'/Pictures/pathplanningtest.png'));
A = imread(strcat(getuserdir,'/Pictures/pathplanningtests.png'));
O = imbinarize(A(:,:,1));
O = padarray(O,[1 1],0,'both');
O = O>0.5;
close all
image(A);
sp = [20;20];
%sp = [3;3];
[m,n]=size(O);

startindex = sp(1)+(sp(2)+1)*m;
CC = bwconncomp(O,4);
findstart = @(conn)find(conn,startindex);
startC = 0;
for i = 1:CC.NumObjects
    if(ismember(startindex,CC.PixelIdxList{i}))
        startC = 1;
    end
end
O(:) = 0;
O(CC.PixelIdxList{startC}) = 1;


%heatmaps
H = zeros(m,n);
%only used for experimentation
nn = [-1,0;1,0;0,-1;0,1];
[nnn,~]=size(nn);
hu = 0.1;
%very slow but works
oH = 1;
c = 0;
while(c < 10000)
    c = c+1;
    Hn = zeros(m,n,nnn);
    Ho = zeros(m,n,nnn);
    for inn = 1:nnn
        Hn(:,:,inn)=circshift(H,nn(inn,:));
        Ho(:,:,inn)=circshift(O,nn(inn,:));
    end
    Hc = sum(Ho,3);
    Hs = sum(Hn,3);
    H = Hs./Hc + hu;
    H(sp(1),sp(2))=0;
    H(~O)=0;
end
c
H(sp(1),sp(2))=0;
[tx ty] = find(H == max(H(:)))
figure
imagesc(H)

%dirichlet
D = zeros(m,n);
c = 0
oD = 1;
while (~isequal(oD,D) && c < 100000)
    oD = D;
    c = c+1;
    Dn = zeros(m,n);
    for inn = 1:nnn
        Dn = Dn + circshift(D,nn(inn,:));
    end
    Dn = Dn/nnn;
    D = Dn;
    D(~O)=1;
    D(tx,ty)=-1;
end
c
figure
imagesc(D)
[px,py] = gradient(D);
pnorm = (px.^2+py.^2).^0.5;
pnorm(~O)=1000;
pnx = px./pnorm;
pny = py./pnorm;
figure
xx = 1:m;
yy = 1:n;
quiver(xx,yy,-pnx,-pny)
%find path
[X,Y] = meshgrid(1:m,1:n);
x = sp(1);
y = sp(2);
pos = [x,y];
nn = [-1,0;1,0;0,-1;0,1;1,1;-1,1;1,-1;-1,-1];
nnn = 8;
tpos = [tx(1),ty(1)];
while (~isequal(pos(end,:),tpos))
    maxi = 0;
    minv = 2;
    for inn = 1:nnn
        cnn = nn(inn,:);
        nx = x+cnn(1);
        ny = y+cnn(2);
        if(nx>=1 && nx<=m && ny>=1 && ny<=n)
            newv = D(nx,ny);
            if(newv<minv)
                maxi = inn;
                minv = newv;
            end
        end
    end
    x = x + nn(maxi,1);
    y = y + nn(maxi,2);
    D(y,x);
    pos = [pos; x,y];
end
figure
imagesc(O);
hold
plot(pos(:,2),pos(:,1));