<h1 align="center">
  <img src="./lo.png" alt="Locaman">
  <br />
  Locaman
  <br />
</h1>
<h4 align="center">
Android service application for WebSocket-based user data transmission by request from a client
</h4>
<h1></h1>

### How to install and compile
##### Clonning
```bash
git clone https://github.com/opaldone/locaman.git
```
##### Go to the root "locaman" directory
```bash
cd locaman
```
##### Set the ANDROID_HOME constant
> You can put it in the ~/.profile \
> /mnt/terik/a_my/java/android_sdk - it is my path to the "android sdk" change it to yours
```bash
export ANDROID_HOME="/mnt/terik/a_my/java/android_sdk"
```
##### Compiling by the "r" bash script
> r - means "run", bd - means "build"
```bash
./r bd
```
if all are ok you can see the apk file
```bash
ls app/build/outputs/apk/debug/app-debug.apk
```
##### Install the application to emulator or phone by the "r" bash script
> r - means "run", in - means "install"
```bash
./r in
```
##### Stop the application on emulator or phone by the "r" bash script
> r - means "run", st - means "stop"
```bash
./r st
```
##### Remove the application from emulator or phone by the "r" bash script
> r - means "run", re - means "remove"
```bash
./r re
```
##### Show logcat by the "r" bash script
> without parameters
```bash
./r
```
##### Clear logcat by the "r" bash script
```bash
./r j
```

### License
MIT License - see [LICENSE](LICENSE) for full text
