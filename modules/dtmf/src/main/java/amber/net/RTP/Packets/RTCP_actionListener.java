package amber.net.RTP.Packets;

/**
*   Interface which is implemented by classes which are interested in receiving RTCP Packets.
*   Implementation for handleRTPEvent methods must be provided by such classes.  The method
*   <b> addRTCP_actionListener() </b> in Session is responsible for registerring the listener.
*   ( Only one RTCP listener class per session. )
*   RTCP packets will be posted only after the class implementing this interface registers itself
*   with the Session.
*
*   Note:
*   It is assumed that ONE and only one class in an application is implementing this interface is
*   registered with the Session.
*
*   RTCP LoopBack:
*   RTCP packets originated from this session are not posted.  Self RTCP packets are filtered out
*   and RTCP packets originated by other sources cause the RTCP events.
*
*   Future releases might add multiple listener registeration.
*   
*   @see RTCPReceiverReportPacket
*   @see RTCPSenderReportPacket
*   @see RTCPSDESPacket
*/

public interface RTCP_actionListener
{
    /**
    *   Implementation of this method makes use of the received RTCP Receiver Report Packet.
    *
    *   @param  RRpkt The received RTCP Receiver report packet.
    */
    public void handleRTCPEvent ( RTCPReceiverReportPacket RRpkt);

    /**
    *   Implementation of this method makes use of the received RTCPReceiverReportPacket.
    *
    *   @param  SRpkt The received RTCP Sender report packet.
    */
    public void handleRTCPEvent ( RTCPSenderReportPacket SRpkt);

    /**
    *   Implementation of this method makes use of the received RTCPReceiverReportPacket.
    *
    *   @param  sdespkt The received RTCP SDES racket packet.
    */
    public void handleRTCPEvent ( RTCPSDESPacket sdespkt);

    /**
    *   Implementation of this method makes use of the received RTCPReceiverReportPacket.
    *
    *   @param  byepkt The received BYE packet.
    */
    public void handleRTCPEvent ( RTCPBYEPacket byepkt);
}