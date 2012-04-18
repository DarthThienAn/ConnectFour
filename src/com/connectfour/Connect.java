package com.connectfour;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class Connect {

	public static final int SERVERPORT = 8080;

	/** input stream from server **/
	BufferedReader serverIn;
	/** output stream from server **/
	PrintWriter serverOut;
	/** input stream from client **/
	BufferedReader clientIn;
	/** output stream from client **/
	PrintWriter clientOut;
	/** server socket that will initialize other sockets **/
	String serverIP = "";
	/** server socket that will initialize other sockets **/
	ServerSocket srvSocket;
	/** whether or not the client is connected yet **/
	boolean connected;
	
//	private int testCount = 0;
	
	//for testing
	TextView text;
	Handler handler = new Handler();

	public void initServer()
	{
		serverIP = getLocalIpAddress();
		Thread serverThread = new Thread(new ServerThread());
		serverThread.start();
	}

	public void close()
	{
		try {
			srvSocket.close();
		}
		catch (Exception e)
		{
			System.exit(0);
		}
	}
	
	public String getServerIP()
	{
		return serverIP;
	}
	
	public void sendMsgFromServer(String msg)
	{
		if (serverOut != null)
			serverOut.println(msg);
	}
	
	public String getMsgToServer()
	{
		try {
			String fromClient = serverIn.readLine();
			final String sendValue = fromClient;
			
			return sendValue;
		}
		catch (Exception e) {
			return null;
		}
	}

	public boolean initClient(String ip)
	{
		if (!connected) {
			
			serverIP = ip;
			if (!ip.equals("")) {
				Thread cThread = new Thread(new ClientThread());
				cThread.start();
			}
			
			return true;
		} else
		{
			//error
		}

		return false;
	}
	
	public void sendMsgFromClient(String msg)
	{
		if (clientOut != null)
			clientOut.println(msg);
	}
	
	public String getMsgToClient()
	{
		try {
			String fromServer = clientIn.readLine();
			final String sendValue = fromServer;
			
			return sendValue;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	private String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			// Log.e("Start", ex.toString());
		}
		return null;
	}
	
	public class ServerThread implements Runnable {
		public void run() {
			try {
				if (serverIP != null) {
					
					//open server socket
					srvSocket = new ServerSocket(SERVERPORT);
					
					while (true) {
						//begin listening, accept when it comes
						Socket client = srvSocket.accept();

						//init I/O streams
						try {
							serverIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
							// set true, for auto-flushing after print statements
							serverOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
	
							break;
						} catch (Exception e) {
							Log.d("ServerInitThread", "FAIL");
							e.printStackTrace();
						}
					}
				} else {
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public class ClientThread implements Runnable {
		public void run() {
			try {
				if (!connected) {
					InetAddress serverAddr = InetAddress
							.getByName(serverIP);
					// connecting
					Socket server = new Socket(serverAddr, SERVERPORT);
					if (server != null)
						connected = true;

					while (connected) {

						try {
							clientIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
							// set true, for auto-flushing after print statements

							clientOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(server.getOutputStream())), true);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
