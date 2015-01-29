package edu.mit.kacquah.udp.client;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UDPListener extends Thread {
  
  // Concurrent data structure for passing packets on to listening thread.
  private ConcurrentLinkedQueue<byte[]> packetQueue;
  
  // Networking
  byte[] receiveBytes;
  DatagramPacket receivePacket;
  DatagramSocket receiveSocket;
  
  public UDPListener() throws SocketException {
    packetQueue = new ConcurrentLinkedQueue<byte[]>();
    receiveBytes = new byte[Client.MAX_UDP_BUFFER_SIZE];
    receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length); 
    receiveSocket = new DatagramSocket(Client.CLIENT_PORT);
  }
  
  /**
   * Receive and queue packets.
   */
  public void run()  {
    while(true) {
      // Wait for new packet
      try {
        receiveSocket.receive(receivePacket);
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(-1);
      }
      // Copy data into new byte buffer
      byte[] packetData = new byte[Client.MAX_UDP_BUFFER_SIZE];
      System.arraycopy(receivePacket.getData(), 0, packetData, 0,
          receivePacket.getLength());
      // Queue
      packetQueue.add(packetData);
    }
    
  }
  
  /**
   * Returns true when there is a packet available.
   * @return
   */
  public boolean hasPacket() {
    return !packetQueue.isEmpty();
  }
  
  /**
   * Returns the next packet available.
   * @return
   */
  public byte[] getNextPacket() {
    return packetQueue.poll();
  }

}
