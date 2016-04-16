package nebo15.eppyk.gif;

/**
 * Created by anton on 10/04/16.
 */
public interface IGIFEvent {
    void gifAnimationDidStart(GIFObject object);
    void gifAnimationDidStop(GIFObject object);
    void gifAnimationDidFinishLoop(GIFObject object, int loopIndex);
    void gifAnimationDidFinish(GIFObject object);
    void gifAnimationShowFrame(GIFObject object, int frameIndex);
}
