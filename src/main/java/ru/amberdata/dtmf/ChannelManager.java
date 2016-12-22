package ru.amberdata.dtmf;

import com.sun.istack.internal.NotNull;
import com.tino1b2be.dtmfdecoder.DTMFDecoderException;
import com.tino1b2be.dtmfdecoder.DTMFUtil;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.amberdata.dtmf.configuration.Channel;
import ru.amberdata.dtmf.http.IAction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by zstan on 20.12.16.
 */
public class ChannelManager {

    private static final Logger logger = LogManager.getLogger(ChannelManager.class);

    private final Channel ch;
    // startSymbols -> set<stopSymbols>
    private Map<String, Set<String>> adBlocks = new HashMap<>();
    private Function<? extends IAction, Boolean> action;
    private Set<String> stopLabels = null;

    public ChannelManager(Channel ch) {
        this.ch = ch;
        this.ch.getAdBreak().forEach(b -> {
            String key = b.getCueTone().getStartSymbols();
            Set<String> val = adBlocks.get(key);
            if (val != null)
                val.add(b.getCueTone().getStopSymbols());
            else {
                Set<String> stopLbls = new HashSet<>();
                stopLbls.add(b.getCueTone().getStopSymbols());
                adBlocks.put(key, stopLbls);
            }
        });
    }

    public void initDtmf(DTMFUtil dtmf) throws DTMFDecoderException {
        DTMFUtil.setMinToneDuration(ch.getSymbolLength()); // todo: fix static method !!!
        if (Double.compare(ch.getCutoffNoiseRatio(), -1.) == 0) {
            dtmf.setFftCutoffPowerNoiseRatio(ch.getCutoffNoiseRatio());
        }
        dtmf.setLabelPauseDurr(ch.getPauseLength());

        dtmf.setOnLabelAction(
                new Consumer<String>() {
            public void accept(String label) {
                ChannelManager.this.onLabelStrong(label);
            }
        });
    }

    public boolean onLabelStrong(String label) {
        boolean result = false;
        logger.debug("label event: " + label);
        if (stopLabels != null) {
            if (stopLabels.contains(label)) {
                stopLabels = null;
                logger.info("stop label found: " + label + " call some callback...");
                result = true;
            } else {
                logger.error("expected stop label(s): " + String.join(" ", stopLabels) + " but found: " + label);
                stopLabels = null;
            }
        } else {
            Set<String> stopLbls = adBlocks.get(label);
            if (stopLbls != null) {
                stopLabels = stopLbls;
                logger.info("start label found: " + label + " call some callback...");
                result = true;
            } else {
                logger.info("expected start label(s): " + String.join(" ", adBlocks.keySet()) + " but found: " + label);
            }
        }

        return result;
    }
}
