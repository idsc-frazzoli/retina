points = [0,0,10,30,34,19,10,5;8,2,0,1,9,10,7,10]';
points = points + [3,3];
radii = [1,2,2,2,2,1,1,1]';
pointsX = points(:,1);
pointsY = points(:,2);

N = numel(pointsY);

%get spline points
res = 100;
splinet = 0:1/res:N;


splinex = [];
spliney = [];
spliner = [];
for t = splinet
    %close loop
    [xx,yy] = casadiDynamicBSPLINE(t,[points;points(1:2,:)]);
    rr = casadiDynamicBSPLINERadius(t, [radii;radii(1:2)]);
    %I know that this is slow but I do not care
    splinex = [splinex; xx];
    spliney = [spliney; yy];
    spliner = [spliner; rr];
end

% Create a logical image of a circle with specified
% diameter, center, and image size.
% First create the image.
Xm = 40;
Ym = 40;
imageSizeX = 2000;
imageSizeY = imageSizeX*Ym/Xm;
scale = imageSizeX/Xm;
[columnsInImage, rowsInImage] = meshgrid(1:imageSizeX, 1:imageSizeY);
% Next create the circle in the image.
innerCirclePixels = zeros(imageSizeY, imageSizeX);
outerCirclePixels = zeros(imageSizeY, imageSizeX);
outerlineWidth = 0.2*scale;
for i = 1:numel(spliner)
    centerX = splinex(i)*scale;
    centerY = spliney(i)*scale;
    radius = spliner(i)*scale;
    innerCirclePixels = innerCirclePixels | (rowsInImage - centerY).^2 ...
        + (columnsInImage - centerX).^2 <= radius.^2;
    outerCirclePixels = outerCirclePixels | (rowsInImage - centerY).^2 ...
        + (columnsInImage - centerX).^2 <= (outerlineWidth+radius).^2;
    % circlePixels is a 2D "logical" array.
    % Now, display it.
end
outerline = outerCirclePixels & ~innerCirclePixels;
%outerline = outerline+innerCirclePixels*0.5;

close all
figure
hold on
image([0,Xm],[0,Ym],outerline) ;
colormap([1,1,1; 0, 0, 0]);
plot(splinex,spliney)
scatter(pointsX,pointsY);
daspect([1 1 1])
hold off
