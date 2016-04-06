package nebo15.eppyk;

import android.graphics.Movie;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements Animation.AnimationListener {

    // UI Ainmations

    ImageView planetImageView;
    ImageView logoImageView;

    GIFView manImageView;
    GIFView dogImageView;

    GifImageView starsGifView;
    GifImageView starsGifViewBuffer;

    Movie manMovie = null;
    Movie manMovie1 = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        this.planetImageView = (ImageView) findViewById(R.id.PlanetImageView);
        this.logoImageView = (ImageView) findViewById(R.id.LogoImageView);

        this.starsGifView = (GifImageView) findViewById(R.id.StarsGifView);
        ((GifDrawable) this.starsGifView.getDrawable()).seekTo(1);
        ((GifDrawable) this.starsGifView.getDrawable()).stop();

        try {
            this.starsGifViewBuffer = (GifImageView) findViewById(R.id.StarsGifViewBuffer);
            this.starsGifViewBuffer.setImageDrawable(new GifDrawable(getResources(), R.drawable.star_drop));
            ((GifDrawable) this.starsGifViewBuffer.getDrawable()).seekTo(1);
            ((GifDrawable) this.starsGifViewBuffer.getDrawable()).stop();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // MAN
        manMovie = Movie.decodeStream(getResources().openRawResource(R.drawable.test_man));
        manMovie1 = Movie.decodeStream(getResources().openRawResource(R.drawable.test_man_1));

        this.manImageView = (GIFView) findViewById(R.id.ManImageView);
        this.manImageView.setMovie(manMovie1);

        this.dogImageView = (GIFView) findViewById(R.id.DogImageView);
        this.dogImageView.setMovieResource(R.drawable.dog_move_1);

    }


    public void testAnimation(View sender) {
        startUIAnimation();
        ((GifDrawable) this.starsGifView.getDrawable()).start();
    }


    boolean ok = true;
    public void gifTest(View sender) {
        if (ok) {
            showStartsDrop();
            this.manImageView.setMovie(manMovie1);
        } else {
            showStartsBack();
            this.manImageView.setMovie(manMovie);
        }
        ok = !ok;

    }

    void showStartsDrop() {
        this.starsGifView.setVisibility(View.GONE);
        this.starsGifViewBuffer.setVisibility(View.VISIBLE);
        ((GifDrawable) this.starsGifViewBuffer.getDrawable()).start();

        // Prepare star back
        try {
            starsGifView.setImageDrawable(new GifDrawable(getResources(), R.drawable.star_back));
            ((GifDrawable) starsGifView.getDrawable()).seekTo(1);
            ((GifDrawable) starsGifView.getDrawable()).stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void showStartsBack() {
        this.starsGifViewBuffer.setVisibility(View.GONE);
        this.starsGifView.setVisibility(View.VISIBLE);
        ((GifDrawable) this.starsGifView.getDrawable()).start();

        // Prepare star drop
        try {
            this.starsGifViewBuffer.setImageDrawable(new GifDrawable(getResources(), R.drawable.star_drop));
            ((GifDrawable) this.starsGifViewBuffer.getDrawable()).seekTo(1);
            ((GifDrawable) this.starsGifViewBuffer.getDrawable()).stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /// Animation Actions
    public void startUIAnimation() {
        // Planet animation
        Animation planetAnimation = AnimationUtils.loadAnimation(this, R.anim.planet_animation_down);
        planetAnimation.setAnimationListener(this);
        planetAnimation.setFillAfter(true);
        this.planetImageView.startAnimation(planetAnimation);
        this.manImageView.startAnimation(planetAnimation);
        this.dogImageView.startAnimation(planetAnimation);

        // Logo animation
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        logoAnimation.setFillAfter(true);
        this.logoImageView.startAnimation(logoAnimation);
    }


    /// UI Animation Listener
    public void onAnimationStart(Animation animation) {


    }

    public void onAnimationEnd(Animation animation) {
        if (this.planetImageView.getAnimation() == animation) {

        }

    }

    public void onAnimationRepeat(Animation animation) {


    }


}
