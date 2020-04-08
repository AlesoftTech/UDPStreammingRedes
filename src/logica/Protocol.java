package logica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Protocol extends Thread 
{
	
	public static final String PREPARADO = "PREPARADO";
	
	public static final String ERROR = "ERROR";

	
	private Server servidor;
		
	private Socket cliente;
	
	private BufferedReader lectorCliente;
	
	private PrintWriter escritorCliente;
	
	public Protocol(Server s, Socket cliente, BufferedReader buf, PrintWriter pw) 
	{
		this.cliente = cliente;
		this.lectorCliente = buf;
		this.escritorCliente = pw;
		servidor = s;
	}

	
	public void procesar() throws IOException 
	{
		ArrayList<Channel> canales = servidor.obtenerCanales();
		
		String mensajeTotal = "";
		//envio al cliente de los canales prestados por el servidor
		for (Channel channel : canales) 
		{
			String hostName = channel.getMulticastingGroup().getHostName();
			String videoName = channel.getVideo().getName();
			int puerto =  Server.PORT;
			//le decimos en que host esta y el video que se muestra alli(separado por comas)
			System.out.println("Canal " + hostName + " " + " Video : " + videoName + " Puerto "  + puerto );
			//formato : 238.X.0.2:8080/mivideo.mp4
			String message = hostName + ":" +puerto +"/"+ videoName + ";";
			
			mensajeTotal += message;
			
		}
		escritorCliente.println(mensajeTotal);
				
		//empieza a escuchar al cliente por si quiere agregar un video a la lista
		RecieveVideo rv = new RecieveVideo(servidor, cliente, lectorCliente, escritorCliente);
		rv.start();
		
	}
	@Override
	public void run() 
	{
		try 
		{
			procesar();
			
		} catch (IOException e) 
		{
			System.out.println("Ocurrio un error " + e.getMessage());
		}		
	}


}
