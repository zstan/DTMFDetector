package ru.amberdata.dtmf;

import com.tino1b2be.dtmfdecoder.DTMFDecoderException;
import com.tino1b2be.dtmfdecoder.DTMFUtil;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.amberdata.dtmf.configuration.dtmf.Channel;
import ru.amberdata.dtmf.action.Action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by zstan on 20.12.16.
 */
public class ChannelManager {

    private static final Logger logger = LogManager.getLogger(ChannelManager.class);

    private final Channel ch;
    private final DTMFContext context;
    // startSymbols -> set<stopSymbols>
    private Map<String, Pair<Integer, String>> adBlocks = new HashMap<>();
    private Function<? extends Action, Boolean> action;
    private Pair<Integer, String> stopLabel = null;

    public ChannelManager(Channel ch, DTMFContext ctx) {
        this.ch = ch;
        this.context = ctx;
        this.ch.getAdBreak().forEach(b -> {
            String start = b.getCueTone().getStartSymbols();
            String stop  = b.getCueTone().getStopSymbols();
            Integer id = b.getId();
            Pair<Integer, String> val = new Pair<>(id, stop);
            if (adBlocks.get(start) != null)
                logger.error("found start label duplicate for channel: {}-{}, check config", ch.getId(), ch.getName());
            adBlocks.put(start, val);
        });
        this.wireWithExternalChannel();
    }

    public void wireWithExternalChannel() {

        for (ru.amberdata.dtmf.configuration.external.Elemental.Channel ch: DTMFContext.getManageConfig().getChannel()) {
            if (ch.getId() == this.getChannel().getId()) {
                this.getChannel().setExternalChannel(ch);
                logger.debug("wire channels: {}", this.getChannel().getId());
                break;
            }
        }
        if (this.getChannel().getExternalChannel() == null)
            logger.error("channel id: " + this.getChannel() + " not found in external config");
    }

    public void initDtmf(DTMFUtil dtmf) throws DTMFDecoderException {
        dtmf.setMinToneDuration(ch.getSymbolLength());
        if (Double.compare(ch.getCutoffNoiseRatio(), -1.) != 0) {
            dtmf.setFftCutoffPowerNoiseRatio(ch.getCutoffNoiseRatio());
        }
        dtmf.setLabelPauseDurr(ch.getPauseLength());
        dtmf.setSymbolLength(ch.getSymbolLength());

        dtmf.setOnLabelAction(ChannelManager.this::onLabelStrong);
    }

    public boolean onLabelStrong(final String label) {

        boolean result = false;

        if (this.getChannel().getExternalChannel() == null) {
            logger.error("channel {} has no external channel in elemental config", this.getChannel().getId());
        }
        else {
            logger.info("label event: " + label);

            Pair<Integer, String> stopLbl = adBlocks.get(label);
            if (stopLbl != null) {
                stopLabel = stopLbl;
                logger.info("start label found: " + label + " post http callback, user: {}, server: {}",
                        this.getChannel().getExternalChannel().getUserName(),
                        this.getChannel().getExternalChannel().getServerAddress());
                context.getExternalAction().apply(this.getChannel(), stopLbl.getKey());
                result = true;
            } else {
                if (stopLabel != null && stopLabel.getValue().equals(label)) {
                    stopLabel = null;
                    logger.info("stop label found: " + label + " no action registered");
                    result = true;
                } else {
                    logger.info("stop without start label or unknown label found: " + label);
                }
            }
        }

        return result;
    }

    public Channel getChannel() {
        return ch;
    }
}
