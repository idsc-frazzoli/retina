
double func() {
double val = 0;
for (int c=1;c<300;++c) {
val+=c+val*3.13+c*c*1.234;
while (val>1)
  --val;
}
}

void main() {
double val = 0;
for (int c=0;c<300;++c) {
val+=func();
val/=7;
while (val>1)
  --val;
}
}

