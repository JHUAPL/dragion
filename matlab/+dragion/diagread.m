function [phi,x,y,z] = diagread(filename)

fid = fopen(filename, 'rt');
dum = fscanf(fid, '%s', 1);
phi = str2num(fgetl(fid));
dum = fscanf(fid, '%s', 1);
x = str2num(fgetl(fid));
dum = fscanf(fid, '%s', 1);
y = str2num(fgetl(fid));
dum = fscanf(fid, '%s', 1);
z = str2num(fgetl(fid));

fclose(fid);
