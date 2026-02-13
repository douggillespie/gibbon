% The mics are on the corners of an equilateral triangle with transcribing 
% circle radius of 46.2 mm (i.e., 80 mm between microphone centres). 
% The microphone channels number counter-clockwise so if channel 1 points north, 
% channel 2 points WSW (assuming that the recorder is deployed the way it's meant to be). 
% To match the heading data, angles-of-arrival should increase clockwise and be keyed 
% to mic 1, i.e., an AOA of 0 degrees means that the sound is on the line passing 
% from the centre of the recorder through mic 1

r = 46.2;
a = 90-[0 -120 120]
x = r*cosd(a)
y = r*sind(a)

% check distances are 80mm
for i = 2:3
    r = sqrt((x(i)-x(i-1)).^2 + (y(i)-y(i-1)).^2)
end