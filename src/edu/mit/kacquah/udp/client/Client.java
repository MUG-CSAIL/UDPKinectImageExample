package edu.mit.kacquah.udp.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import edu.mit.kacquah.udp.image.KinectImage;

public class Client {
  // Common globa constants
  public static int SERVER_PORT = 4445;
  public static int CLIENT_PORT = 4446;
  
  public static int MAX_UDP_BUFFER_SIZE = 65507/10;

  
	public static void main (String [] args) throws Exception  {
	  // Client Socket
	  UDPListener udpListener = new UDPListener();
	  // Start thread
	  udpListener.start();
	  System.out.println("Client thead running");
	  
	  // Create KinectImageConstructor for producing kinect images
	  KinectImageConstructor constructor = new KinectImageConstructor();
	  
	  // Recieve Messages
	  while (true) {
	    // Spin until we get a new packet.
	    while (!udpListener.hasPacket()) {
	      System.out.println("No packet yet...");
	    }
	    
	    // Debug print
	    byte[] packetData = udpListener.getNextPacket();
	    String received = new String(packetData, 0, packetData.length);
	    System.out.println(received);
	    System.out.println("Got it.");
	    
	    // Construct image
      constructor.processPacketData(packetData);
      if (constructor.hasNextKinectImage()) {
        KinectImage image = constructor.getNextKinectImage();
        // Check for image correctness then quit.
        if (!image.checkImage()) {
          System.out.println("Something went wrong");
          System.exit(-1);
        } else {
          System.out.println("Image is correct!!!");
          System.exit(0);
        }
      }
	    
	  }
		
	}

}
