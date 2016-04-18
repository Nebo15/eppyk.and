package nebo15.eppyk.gif;

import android.graphics.Movie;
import java.io.InputStream;

/**
 * Created by anton on 10/04/16.
 */
public class GIFObject {
    public Movie movie;
    public int framesCount = 0;
    public int loopsCount = 0;

    public GIFObject(InputStream resourceStream, int framesCount) {
        this.movie = Movie.decodeStream(resourceStream);
        this.framesCount = framesCount;
    }
}
