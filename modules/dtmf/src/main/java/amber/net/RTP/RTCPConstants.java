package amber.net.RTP;

import java.lang.*;


/**
*   This class provides constants associated with RTCP Packets -similar to those defined
*   in rtp.h
*/

public class RTCPConstants extends Object
{

    /**
    *   Version =2.
    */
    public static final byte VERSION    =   2;
    
    /**
    *   Padding =0.
    */
    public static final byte PADDING    =   0;
   

    /*
    *                   RTCP TYPES
    */

	
	/**
	*   RTCP Sender Report Packet (SR)  = 200
	*/
	public static final int RTCP_SR    = 	(int) 200;
	
	/**
	*   RTCP Receiver Report Packet (RR)  = 201
	*/
	public static final int RTCP_RR    = 	(int) 201;
	
	/**
	*   RTCP Source Description Packet (SDES)   = 202
	*/
	public static final int RTCP_SDES  =	(int) 202;
	
	/**
	*   RTCP BYE Packet   = 203
	*/
	public static final int RTCP_BYE   = 	(int) 203;
	
	/**
	*   RTCP APP Packet   = 204
	*/
	public static final int RTCP_APP   = 	(int) 204;
	
	

    /*
    *                   SDES TYPES
    */

	/**
	*   End SDES Item =0
	*/
	public static final byte RTCP_SDES_END    = (byte) 0;
	
	/**
	*   Canonical   end - point identifier SDES Item =1
	*/
	public static final byte RTCP_SDES_CNAME  =	(byte) 1;
	
	/**
	*   User Name SDES Item = 2
	*/
	public static final byte RTCP_SDES_NAME   =	(byte) 2;
	
	/**
	*   Electronic mail address SDES Item =3
	*/
	public static final byte RTCP_SDES_EMAIL  =	(byte) 3;
	
	/**
	*   Phone number SDES Item = 4
	*/
	public static final byte RTCP_SDES_PHONE  =	(byte) 4;
	
	/**
	*   Geographic user location SDES Item = 5
	*/
	public static final byte RTCP_SDES_LOC    =	(byte) 5;
	
	/**
	*   Application or tool name SDES Item = 6
	*/
	public static final byte RTCP_SDES_TOOL   =	(byte) 6;
	
	/**
	*   Notice or status SDES Item = 7 
	*/
	public static final byte RTCP_SDES_NOTE   =	(byte) 7;
	
	/**
	*   Private extensions SDES Item = 8
	*/
	public static final byte RTCP_SDES_PRIV   =	(byte) 8;

}