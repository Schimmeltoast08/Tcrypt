# Tcrypt
Tcrypt is a simple encryption tool that is very simple, yet very strong.
It has nearly unbreakable encryption thanks to the XOR Process, but it is slow and generates a key file!
To decrypt the message, you must safely send someone the decryption key, which is the main limiting factor. **Do not use for any important Communication**

## Install (Linux-only)
```
git clone https://github.com/Schimmeltoast08/Tcrypt.git
cd Tcrypt
sudo bash install.sh
```

## Portable (Windows)
```
git clone https://github.com/Schimmeltoast08/Tcrypt.git
cd Tcrypt
java -jar tcrypt.jar
```

## Usage
To use tcrypt, first initiate the live tcrypt application by typing
```
tcrypt
```
To encrypt a file, write
```
E [filename]
```
This generates a .tcrt file, which is the encrypted file, and a .tkey file. This file is the key to unlock the .tcrt file again.
To decrypt the File, type
```
D [.tcrt file] [.tkey file]
```
This generates a .tmsg file, which is simply the original file, but with an extra extension as to not accidentally overwrite existing files.
To quit Tcrypt, type q or exit.

## Alternatively
If you rather like graphical applications, you can run 
```
tcrypt --gui
```
or hit "g" once you are in the cli

On top of that, you can also call functions directly. Example:
```
tcrypt --encrypt [file]
tcrypt --decrypt [.tcrt] [.tkey]
tcrypt --help
```
### Contributions
Any contributor is welcome to fork this repository or to improve upond it so long as you comply with the License file.
