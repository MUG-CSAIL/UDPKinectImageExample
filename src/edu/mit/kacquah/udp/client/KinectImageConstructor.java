package edu.mit.kacquah.udp.client;

import edu.mit.kacquah.udp.image.KinectImage;
import edu.mit.kacquah.udp.server.Server;

/**
 * 
 * @author Kojo
 *
 */
public class KinectImageConstructor {
  
  /**
   * Buffer for storing new Kinect image bytes.
   */
  private byte[] imageBytes;
  
  /**
   * Current image sequence number from kinect.
   */
  private int currentSequenceNumber;
  
  private boolean hasNextKinectImage;
  private KinectImage nextKinextImage;
  private int packetsReceivedCount;
  
  public KinectImageConstructor() {
    nextKinextImage = new KinectImage();
    currentSequenceNumber  = -1;
    packetsReceivedCount = -1;
    imageBytes = new byte[KinectImage.IMAGE_BYTES_LENGTH];
  }
  
  public void processPacketData(byte[] packedData) {
    int packetSequenceNumber = packedData[0];
    int packetPartNumber = packedData[1];
    System.out.println("Packet data length: " +  packedData.length);
    
    // Are we are getting a new image frame?
    if (currentSequenceNumber == -1
        || packetSequenceNumber != currentSequenceNumber) {
      // Flush the bytes
      imageBytes = new byte[KinectImage.IMAGE_BYTES_LENGTH];
      // Update the current sequence number
      currentSequenceNumber = packetSequenceNumber;
      // Reset packets received count
      packetsReceivedCount = 0;
    }
    
    // Offset into image byte buffer
    int imageBytesOffsetIndex = packetPartNumber * Server.PACKET_DATA_SIZE;

    // Determine how many bytes to copy per packet. The last packet is short.
    int numberOfBytesToCopy;
    if (imageBytesOffsetIndex + Server.PACKET_DATA_SIZE > KinectImage.IMAGE_BYTES_LENGTH) {
      numberOfBytesToCopy = KinectImage.IMAGE_BYTES_LENGTH
          - imageBytesOffsetIndex;
    } else {
      numberOfBytesToCopy = Server.PACKET_DATA_SIZE;
    }
    
    // Copy bytes into byte buffer    
    System.arraycopy(packedData, 2, imageBytes, imageBytesOffsetIndex,
        numberOfBytesToCopy);
    
    // Update number of packets received
    packetsReceivedCount++;
    
    // Do we have enough packets to make an image?
    if (packetsReceivedCount == Server.NUMBER_OF_PACKETS_PER_IMAGE) {
      nextKinextImage.bytesToImage(imageBytes);
      hasNextKinectImage = true;
    }
  }
  
  public boolean hasNextKinectImage() {
    return this.hasNextKinectImage;
  }
  
  public KinectImage getNextKinectImage() {
    this.hasNextKinectImage = false;
    return  nextKinextImage;
  }

}
