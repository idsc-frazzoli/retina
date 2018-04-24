% code by mg
% transforms image coordinates to physical coordinates. For that the
% physical Z coordinate is assumed Z=0.
% we require a cameraParams.mat file which can be obtained with the matlab
% camera calibrator app

imgNumber = 1;
imgCoord = [95.8880,87.4894,1]; % in pixels

% load the cameraParams
load('cameraParams.mat');

% undistort image coordinates
imgCoordNormalized = (imgCoord(1:2)-cameraParams.PrincipalPoint)/cameraParams.FocalLength(1);
radDist = sqrt(imgCoordNormalized(1)^2+imgCoordNormalized(2)^2);
undistorted(1) = imgCoordNormalized(1)/(1+radDist^2*cameraParams.RadialDistortion(1)+radDist^4*cameraParams.RadialDistortion(2));
undistorted(2) = imgCoordNormalized(2)/(1+radDist^2*cameraParams.RadialDistortion(1)+radDist^4*cameraParams.RadialDistortion(2));

undistUnnormalized = undistorted*cameraParams.FocalLength(1)+cameraParams.PrincipalPoint;

stackedMatrix = [cameraParams.RotationMatrices(:,:,imgNumber);cameraParams.TranslationVectors(imgNumber,1:3)];
intrinsics = cameraParams.IntrinsicMatrix;
transformationMatrix = stackedMatrix*intrinsics;

% since Z=0, we can remove third row
transformationMatrix(3,:) = [];

% invert matrix
InvTransMatrix = inv(transformationMatrix);
physCoord = [undistUnnormalized,1]*InvTransMatrix;

% enforce homogenous coordinates
physCoord = physCoord/physCoord(1,3)

% write to csv and enforce using capitalized E for exponential notation.
% (eases reading the file in java)
fileName = 'test.csv';
dlmwrite(fileName,InvTransMatrix,'precision','%E');
dlmwrite(fileName,cameraParams.PrincipalPoint,'-append','precision','%E');
dlmwrite(fileName,cameraParams.RadialDistortion,'-append','precision','%E');
dlmwrite(fileName,cameraParams.FocalLength,'-append','precision','%E');