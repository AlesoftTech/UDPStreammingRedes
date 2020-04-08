# UDPsockets-stream-server
El siguiente es un servidor en Java que realiza Streaming de archivos de vídeo a través de RTP y haciendo uso de la librería de [vlcj](https://github.com/caprica/vlcj).

## Requisitos Previos
* Es requisito que el servidor se inicie únicamente con 1 vídeo, debido a que cuando hay más de 1 tiende a existir un error. Este error es porque las librerías
de Vlcj no contienen algunos de las instanciaciones de forma sincrónica y por ende puede llegar a fallar.
* Es imperativo que el dispositivo donde se corra el servidor tenga instalado [VLC](https://www.videolan.org/vlc/download-windows.es.html) la versión de 32-bits, debido 
a que si este no es instalado, no podrá funcionar el programa a pesar de que las librerías estén referenciadas y en local.
* Es necesario que el jre con el que se esté corriendo el proyecto sea el jre de java de 32-bits. Sin esto, las librerías de vlcj no se cargarán correctamente y saldrán errores.

## Funcionamiento
El servidor inicia a funcionar, simplemente con ejecutarlo en Eclipse o bien sea generando un .jar y corriendolo. Este lee los vídeos de la carpeta 
`./data` **SE DEBE CREAR DICHA CARPETA Y COLOCAR 1 VÍDEO EN ELLA** y con esto comienza a realizar su streaming cada uno en un canal de multicast distinto. Los canales se envían al cliente y este elige a 
cuál conectarse para recibir el streaming en tiempo real.
