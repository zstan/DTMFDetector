# DTMFDetector

AmberData research project

Based on https://github.com/tino1b2be/DTMF-Decoder

Features
================================
* Listen TS UDP or RTP incoming stream.
* Detects [DTMF](https://en.wikipedia.org/wiki/Dual-tone_multi-frequency_signaling "dtmf desc") sequence labels (setting by config).
* After detection event occur, sends POST request (setting by config).

Common config example:

```xml

<dtmfDetectorConfig>
    <channel id="100" name="vesti 24">
        <streamAddress>rtp://127.0.0.1:1234</streamAddress>
        <audioPID>69</audioPID>
        <audioFormat>MPEG</audioFormat>
        <dtmfChannel>auto</dtmfChannel>
        <cutoffNoiseRatio>0.50</cutoffNoiseRatio>
        <symbolLength>70</symbolLength>
        <pauseLength>70</pauseLength>
        <adBreak id="2">
            <cueTone>
                <startSymbols>1234</startSymbols>
                <stopSymbols>89</stopSymbols>
            </cueTone>
        </adBreak>
        <adBreak id="1">
            <cueTone>
                <startSymbols>123</startSymbols>
                <stopSymbols>78</stopSymbols>
            </cueTone>
        </adBreak>
        <adBreak id="3">
            <cueTone>
                <startSymbols>456</startSymbols>
            </cueTone>
        </adBreak>
    </channel>
    <demoMode>true</demoMode>
</dtmfDetectorConfig>
```

management config example:

```xml

<elementalConfig>
    <channel id="100" name="channel 1">
        <serverAddress>127.0.0.1</serverAddress>
        <userName>admin</userName>
        <authKey>some_auth_key1</authKey>
        <externalID>100</externalID>
        <adBreak id="1">
            <cueTone id="1" commandTimeShift="145" cueTimeShift="8000"/>
        </adBreak>
        <adBreak id="2">
            <cueTone id="1" commandTimeShift="143" cueTimeShift="4000"/>
        </adBreak>
    </channel>
    <channel id="2" name="channel 2">
        <serverAddress>127.0.0.2</serverAddress>
        <userName>admin</userName>
        <authKey>some_auth_key2</authKey>
        <externalID>200</externalID>
        <adBreak id="3">
            <cueTone id="1" commandTimeShift="141" cueTimeShift="2000"/>
        </adBreak>
    </channel>
    <moduleType>1</moduleType>
</elementalConfig>
```
