% code by mg
% script to obtain the internal & external camera calibration values as
% required by the retina repository. We require a cameraParams.mat file
% which can be obtained with the matlab camera calibrator app.
%
% The flag imageVertical allows two calibration procedures: with a
% calibration pattern lying on the floor (at correct position) or with it
% standing vertically (at correct position). The vertical position can be
% used when the camera calibrator app does not recognize the pattern when
% lying flat on the ground.

% whether image is held vertical for easier calibration
imageVertical = true;
% offset of calibration pattern origin to cardboard edge (only used when
% calibrating with vertical image)
offset = 170; % [mm]
% name of file in which parameters are saved
fileName = 'DUBItest.csv';
% imgNumber of image where extrinsics are extracted
imgNumber = 1;

transformationMatrix = cameraParams.RotationMatrices(:,:,imgNumber);
translationVector = cameraParams.TranslationVectors(imgNumber,1:3);
if imageVertical
    [translationVector, transformationMatrix] = ...
        transformToHorizontal(translationVector,transformationMatrix,offset);
end

stackedMatrix = [transformationMatrix; translationVector];
intrinsics = cameraParams.IntrinsicMatrix;
transformationMatrix = stackedMatrix*intrinsics;

% since Z=0, we can remove third row. Then invert to project from image to
% physical plane
transformationMatrix(3,:) = [];
InvTransMatrix = inv(transformationMatrix);

% write to csv and enforce using capitalized E for exponential notation.
% (eases reading the file in java)
dlmwrite(fileName,InvTransMatrix,'precision','%E');
dlmwrite(fileName,cameraParams.PrincipalPoint,'-append','precision','%E');
dlmwrite(fileName,cameraParams.RadialDistortion,'-append','precision','%E');
dlmwrite(fileName,cameraParams.FocalLength,'-append','precision','%E');