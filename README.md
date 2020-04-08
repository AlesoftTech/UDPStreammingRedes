El Servidor utiliza la librería vlcj para realizar el streaming via RTP. 
https://github.com/caprica/vlcj

## Rquerimientos de computo

*El computador debe contar con VLC de 32-bits (solo funciona en 32-bits)
* El JRE debe ser el de 32 bits ya que el de 64 bits no es compatible con vlcj.

## Pasos para ejecución 
*Ejecutar la clase Server.java, seguir interface visual 
*si por algún motivo no encuetra la librería local el compilador,  no funcionaría la autoreproducción y la autoconfiguración de los clientes, de ser así: abrir VLC -> Medio -> Abrir ubicación de red -> introducir la siguiente uri "rtp://238.0.0.1:8080" -> reproducir

