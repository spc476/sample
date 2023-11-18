
/* http://docs.oracle.com/javase/6/docs/api/overview-summary.html */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.IOException;

/**
 * @author spc
 */
 
/*************************************************************************
* The Stat class.
*
* This module implements a way to keep track of KPIs.  The intent is to send
* the KPI off to some server to actually manage, thus freeing the codebase
* that uses this class from having to maintain its own stats (and the
* complexities of handling mutithreaded stats and periodically reporting
* them, etc).
*
* Upon loading, this class will default to a fixed server address (which can
* be overriden (see open()).  Ideally, there should be nothing to
* configure---this should just work and be used immediately.
*
****************************************************************************/

public final class Stat
{
  private static DatagramSocket mStatd;
  private static InetAddress    mServer;
  private static int            mPort;
  
  /*-----------------------------------------------------------------------
  ; We pre-initialize our fields to point to a local server.  If this fails,
  ; we don't care.  Why don't we care?  Because this should have as little
  ; crashing impact on the codebase as possible.  Besides, it should be
  ; apparent to OPS that if this does fail, the component this is being used
  ; by is no longer logging stats and thus, something is horribly wrong.
  ;
  ; So, no exceptions.
  ;------------------------------------------------------------------------*/
  
  static
  {
    try
    {
      mServer = InetAddress.getByName("239.255.0.1");
      mStatd  = new DatagramSocket();
      mPort   = 20000;
    }
    catch (UnknownHostException e)
    {
    }
    catch (SocketException e)
    {
    }
  }
  
  /***********************************************************************/
  /**
   * Send our stats to another server.  Either the hostname or the IP
   * address (as a string) can be specified.
   *
   * @param host
   *    The hostname or IP address to send the stat traffic to.
   */
   
  public static void open(String host)
  throws UnknownHostException
  {
    mServer = InetAddress.getByName(host);
  }
  
  public static void open(String host,int port)
  {
    mServer = InetAddress.getByName(host);
    mport   = port;
  }
  
  /**********************************************************************/
  /**
   * Increment a KPI by 1.
   *
   * @param name
   *    The name of the KPI to increment.
   */
   
  public static void incr(String name)
  {
    try
    {
      byte[]         buffer = String.format("c %s 1",name).getBytes();
      DatagramPacket packet = new DatagramPacket(buffer,buffer.length,mServer,mPort);
      mStatd.send(packet);
    }
    
    /*-------------------------------------------------------------------
    ; Again, there should be as little impact on the codebase using this
    ; code as possible, so any IO Exceptions will be swallowed.  UDP is
    ; lossy anyway, and what'a a few lost packets in the grand scheme of
    ; things?
    ;-------------------------------------------------------------------*/
    
    catch (IOException e)
    {
    }
  }
  
  /*********************************************************************/
  /**
   * Increment a KPI by a given amount.
   *
   * @param name
   *    The name of the KPI to increase
   *
   * @param count
   *    The amount to increase the named KPI by.
   */
   
  public static void count(String name,long count)
  {
    try
    {
      byte[]         buffer = String.format("c %s %d",name,count).getBytes();
      DatagramPacket packet = new DatagramPacket(buffer,buffer.length,mServer,mPort);
      mStatd.send(packet);
    }
    catch(IOException e)
    {
    }
  }
  
  /*********************************************************************/
  /**
   * Increment a KPI by a scaled amount.
   *
   * This is meant to be called periodically (not every time), and thus,
   * the scale represents the amount of "skipped" counts.  So, if you
   * log a value every 100 items, then the scale would be 100.
   *
   * @param name
   *    The name of the KPI to increase
   *
   * @param count
   *    The amount to increase the named KPI by
   *
   * @param scale
   *    The amount to scale the count by.
   */
   
  public static void scalecount(String name,long count,long scale)
  {
    try
    {
      byte[]         buffer = String.format("c %s %d %d",name,count,scale).getBytes();
      DatagramPacket packet = new DatagramPacket(buffer,buffer.length,mServer,mPort);
      mStatd.send(packet);
    }
    catch(IOException e)
    {
    }
  }
  
  /**********************************************************************/
  /**
   * Keep track of a gauge KPI.
   *
   * This will keep track of the minimum, average and maximum values for
   * the named KPI.  This can be used, for instance, to record latency.
   *
   * @param name
   *    The name of the KPI to record
   *
   * @param value
   *    The current value to record
   */
   
  public static void gauge(String name,long value)
  {
    try
    {
      byte[]         buffer = String.format("g %s %d",name,value).getBytes();
      DatagramPacket packet = new DatagramPacket(buffer,buffer.length,mServer,mPort);
      mStatd.send(packet);
    }
    catch(IOException e)
    {
    }
  }
}
