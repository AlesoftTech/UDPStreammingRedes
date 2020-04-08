package logica;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Server extends JPanel implements ActionListener
{
	public static final int PORT = 8080;
	public  String DIR = "./data";
	private ServerSocket serverSocket;
	private  String actualHost;
	private ArrayList<Channel> canales;
	private static final int PUERTO_SERVIDOR = 8787;
	private static final int MAX_CHANNELS = 3825;
	private static final String PASSWORD = "1234";
	private static final String AUTENTICADO = "AUTENTICADO";

	//* FRONT 
	JButton go;
	Server c; 
	JFileChooser chooser;
	String choosertitle;
	
	//*BACK
	public Server() 
	{
		//*FRONT
		go = new JButton("Seleccionar archivo");
		go.addActionListener(this);
		JLabel lblNewLabel = new JLabel("Seleccione el archivo a transmitir");
		add(lblNewLabel);
		add(go);
		
		//*BACK
		actualHost = "238.0.0.0";
		canales = new ArrayList<Channel>();
	}

	public void asignacionDeCanales() throws IOException
	{
		File directorio = new File(DIR);
		File [] archivosMultimedia = directorio.listFiles();

		for (int i = 0; i < archivosMultimedia.length && canales.size() < MAX_CHANNELS ; i++) 
		{
			obtenerSiguienteCanal();
			System.out.println("Next host for multicasting : "+ actualHost);

			Channel canalNuevo = new Channel(actualHost, PORT, archivosMultimedia[i]);
			canales.add(canalNuevo);
			canalNuevo.start();

		}

	}
	public void obtenerSiguienteCanal()
	{
		String[] splitted = actualHost.split("\\.");
		String actualCh = splitted[3];
		String actualNet =  splitted[0];

		if(Integer.parseInt(actualCh) == 255)
		{
			actualNet = (Integer.parseInt(actualNet) + 1 ) + "";
			actualCh = "1";
		}
		else 
		{
			actualCh = (Integer.parseInt(actualCh) + 1) + "";
		}
		String nextHost = actualNet + ".0.0." + actualCh;

		actualHost = nextHost;
	}
	public void aniadirCanal(File nuevoVideo) throws UnknownHostException
	{
		obtenerSiguienteCanal();
		Channel canalNuevo = new Channel(actualHost, PORT, nuevoVideo);
		canalNuevo.start();
		canales.add(canalNuevo);
	}

	public void servidor() throws Exception
	{

		System.out.println("Empezando servidor maestro en puerto " + PUERTO_SERVIDOR);

		System.out.println("Esperando solicitudes.");

		//creamos los canales
		asignacionDeCanales();
		
		Socket client = null;
		BufferedReader bf;
		PrintWriter pw = null;
		
		
		serverSocket = new ServerSocket(PUERTO_SERVIDOR);
		System.out.println("Socket servidor creado.");


		while (true) 
		{
			try 
			{ 

				// Recibe el paquete de listo del cliente
				client = serverSocket.accept();

				bf = new BufferedReader(new InputStreamReader(client.getInputStream()));
				pw = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
				String recibida = bf.readLine();

				String[] userPss = recibida.split(";");
				String contra = userPss[1];
				if(contra.equals(PASSWORD)) 
				{
					pw.println(AUTENTICADO);
					//para cada cliente ejecuta un protocolo
					Protocol pro = new Protocol(this,client, bf, pw);
					pro.start();
				}
				else 
				{
					pw.println(Protocol.ERROR);
					System.err.println("Algo ocurrió y llegó un paquete que no decía PREPARADO (ya existe una referencia al cliente de donde llegó)");
				}

			} 
			catch (IOException e) 
			{
				pw.println(Protocol.ERROR);
				System.err.println("Error creando el socket cliente.");
				client.close();
				e.printStackTrace();
			}
		}
	}


	public ArrayList<Channel> obtenerCanales()
	{
		return canales;
	}
	
	//* FRONT
	public void actionPerformed(ActionEvent e) {
		int result;

		chooser = new JFileChooser(); 
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle(choosertitle);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		//
		// disable the "All files" option.
		//
		chooser.setAcceptAllFileFilterUsed(false);
		//    
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
			System.out.println("getCurrentDirectory(): " 
					+  chooser.getCurrentDirectory());
			System.out.println("getSelectedFile() : " 
					+  chooser.getSelectedFile());
			String path = chooser.getSelectedFile().getAbsolutePath().toString();
			DIR = path; 

			try {
				Server pool = new Server();
				pool.servidor();
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else {
			System.out.println("No Selection ");
		}
	}

	public Dimension getPreferredSize(){
		return new Dimension(400, 200);
	}

	public static void main(String s[]) {
		JFrame frame = new JFrame("");
		Server panel = new Server();
		frame.addWindowListener(
				new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						System.exit(0);
					}
				}
				);
		frame.getContentPane().add(panel,"Center");
		frame.setSize(panel.getPreferredSize());
		frame.setVisible(true);
	}
	
	
	
	
}