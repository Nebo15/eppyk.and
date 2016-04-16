package nebo15.eppyk;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import nebo15.eppyk.gif.GIFObject;
import nebo15.eppyk.gif.GIFView;
import nebo15.eppyk.gif.IGIFEvent;
import nebo15.eppyk.listeners.ShakeEventListener;


public class MainActivity extends AppCompatActivity implements Animation.AnimationListener, IGIFEvent, View.OnClickListener {

    public enum ShakeAction {
        ANSWER, TRYAGAIN, NONE
    }


    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;
    private ShakeAction shakeAction = ShakeAction.NONE;

    Typeface fontGeneral = null;
    Typeface fontGeneralBold = null;

    TextView ctrlWhatIsYourQuestion;
    EditText ctrlQuestionEdit;
    GIFView handImageView;
    TextView ctrlShakeText;
    EditText questionText;
    TextView answerText;

    Button saveButton;
    Button tryAgainButton;



    // UI Ainmations
    ImageView planetImageView;
    ImageView logoImageView;

    GIFView manImageView;
    GIFView dogImageView;

    GIFView starsGifView;

    // Stars
    GIFObject starsGifBegin;
    GIFObject starsGifDrop;
    GIFObject starsGifBack;

    // Man
    GIFObject manGifStatic;
    GIFObject manGifStar;
    GIFObject manGifCatch;
    GIFObject manGifDrop;
    GIFObject manMoves[] = new GIFObject[3];

    // Dog & Hand
    GIFObject handGif;
    GIFObject dogMoves[] = new GIFObject[3];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Shake
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

            public void onShake() {
                showAnswer();
            }

        });


        loadGifs();

        this.fontGeneral = Typeface.createFromAsset(getAssets(), "Geometria.otf");
        this.fontGeneralBold = Typeface.createFromAsset(getAssets(), "Geometria-Bold.otf");

        // Static UI
        this.planetImageView = (ImageView) findViewById(R.id.PlanetImageView);
        this.logoImageView = (ImageView) findViewById(R.id.LogoImageView);

        // Animation
        this.starsGifView = (GIFView) findViewById(R.id.StarsGifView);
        this.manImageView = (GIFView) findViewById(R.id.ManImageView);
        this.dogImageView = (GIFView) findViewById(R.id.DogImageView);

        // Control UI
        this.ctrlWhatIsYourQuestion = (TextView)findViewById(R.id.CtrlWhatIsYourQuestion);
        this.ctrlWhatIsYourQuestion.setTypeface(fontGeneralBold);

        this.ctrlQuestionEdit = (EditText)findViewById(R.id.CtrlQuestionEdit);
        this.ctrlQuestionEdit.setTypeface(fontGeneralBold);
        this.ctrlQuestionEdit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    showAnswer();
                    return true;
                }
                return false;
            }
        });

        this.handImageView = (GIFView)findViewById(R.id.handImageView);
        this.handImageView.setHandler(this);
        this.handImageView.setGif(handGif);

        this.ctrlShakeText = (TextView)findViewById(R.id.CtrlShakeText);
        this.ctrlShakeText.setTypeface(fontGeneralBold);

        // Q&A
        this.questionText = (EditText)findViewById(R.id.QuestionText);
        this.questionText.setTypeface(fontGeneral);

        this.answerText = (TextView)findViewById(R.id.AnswerText);
        this.answerText.setTypeface(fontGeneralBold);

        this.saveButton = (Button)findViewById(R.id.SaveButton);
        this.saveButton.setTypeface(fontGeneralBold);
        this.saveButton.setOnClickListener(this);

        this.tryAgainButton = (Button)findViewById(R.id.TryAgainButton);
        this.tryAgainButton.setTypeface(fontGeneralBold);
        this.tryAgainButton.setOnClickListener(this);

        prepareAnimation();
        startAnimation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }


    void loadGifs() {
        // Stars
        starsGifBegin = new GIFObject(getResources().openRawResource(R.drawable.star_begin), 46);
        starsGifBegin.loopsCount = 1;

        starsGifDrop = new GIFObject(getResources().openRawResource(R.drawable.star_drop), 38);
        starsGifDrop.loopsCount = 1;

        starsGifBack = new GIFObject(getResources().openRawResource(R.drawable.star_back), 58);
        starsGifBack.loopsCount = 1;

        // Man & Dog
        manGifStatic = new GIFObject(getResources().openRawResource(R.drawable.man_static), 51);
        manGifStatic.loopsCount = 0;

        manGifStar = new GIFObject(getResources().openRawResource(R.drawable.man_static_star), 51);
        manGifStar.loopsCount = 0;

        manGifCatch = new GIFObject(getResources().openRawResource(R.drawable.man_star_catch), 21);
        manGifCatch.loopsCount = 1;

        manGifDrop = new GIFObject(getResources().openRawResource(R.drawable.man_star_drop), 42);
        manGifDrop.loopsCount = 1;

        manMoves[0] = new GIFObject(getResources().openRawResource(R.drawable.man_move_1), 57);
        manMoves[1] = new GIFObject(getResources().openRawResource(R.drawable.man_move_2), 46);

        dogMoves[0] = new GIFObject(getResources().openRawResource(R.drawable.dog_move_1), 21);

        handGif = new GIFObject(getResources().openRawResource(R.drawable.hand), 31);
    }

    void prepareAnimation() {
        this.starsGifView.setHandler(this);
        this.starsGifView.setGif(starsGifBegin);

        this.manImageView.setHandler(this);
        this.manImageView.setGif(manGifStatic);

        this.dogImageView.setHandler(this);
        this.dogImageView.setGif(dogMoves[0]);
    }

    void startAnimation() {
        this.handImageView.play();
        this.manImageView.play();
        this.starsGifView.play();
        this.startUIAnimation();
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

    Animation whatAnimationShow, editAnimationShow, handAnimationShow, shakeAnimationShow;
    public void controlsUIAnimationShow() {
        // Controls animation
        whatAnimationShow = AnimationUtils.loadAnimation(this, R.anim.control_show_animation);
        whatAnimationShow.setAnimationListener(this);
        whatAnimationShow.setFillAfter(true);
        this.ctrlWhatIsYourQuestion.startAnimation(whatAnimationShow);

        editAnimationShow = AnimationUtils.loadAnimation(this, R.anim.control_show_animation);
        editAnimationShow.setAnimationListener(this);
        editAnimationShow.setFillAfter(true);
        editAnimationShow.setStartOffset(100);
        this.ctrlQuestionEdit.startAnimation(editAnimationShow);

        handAnimationShow = AnimationUtils.loadAnimation(this, R.anim.control_show_animation);
        handAnimationShow.setAnimationListener(this);
        handAnimationShow.setFillAfter(true);
        handAnimationShow.setStartOffset(200);
        this.handImageView.startAnimation(handAnimationShow);

        shakeAnimationShow = AnimationUtils.loadAnimation(this, R.anim.control_show_animation);
        shakeAnimationShow.setAnimationListener(this);
        shakeAnimationShow.setFillAfter(true);
        shakeAnimationShow.setStartOffset(300);

        this.ctrlShakeText.startAnimation(shakeAnimationShow);
    }

    Animation whatAnimationHide, editAnimationHide, handAnimationHide, shakeAnimationHide;
    private void controlsUIAnimationHide() {
        whatAnimationHide = AnimationUtils.loadAnimation(this, R.anim.control_hide_animation);
        whatAnimationHide.setAnimationListener(this);
        whatAnimationHide.setFillAfter(true);
        whatAnimationHide.setStartOffset(200);
        this.ctrlWhatIsYourQuestion.startAnimation(whatAnimationHide);

        editAnimationHide = AnimationUtils.loadAnimation(this, R.anim.control_hide_animation);
        editAnimationHide.setAnimationListener(this);
        editAnimationHide.setFillAfter(true);
        editAnimationHide.setStartOffset(250);
        this.ctrlQuestionEdit.startAnimation(editAnimationHide);

        handAnimationHide = AnimationUtils.loadAnimation(this, R.anim.control_hide_animation);
        handAnimationHide.setAnimationListener(this);
        handAnimationHide.setFillAfter(true);
        handAnimationHide.setStartOffset(300);
        this.handImageView.startAnimation(handAnimationHide);

        shakeAnimationHide = AnimationUtils.loadAnimation(this, R.anim.control_hide_animation);
        shakeAnimationHide.setAnimationListener(this);
        shakeAnimationHide.setFillAfter(true);
        shakeAnimationHide.setStartOffset(350);
        this.ctrlShakeText.startAnimation(shakeAnimationHide);
    }


    /**** Actions ****/
    Animation showQuestionTextAnimation, hideQuestionTextAnimation, showAnswerTextAnimation, hideAnswerTextAnimation, hideAnswerTryAgainTextAnimation;
    public void showAnswer() {

        if (shakeAction == ShakeAction.NONE)
            return;

        if (shakeAction == ShakeAction.ANSWER) {
            if (ctrlQuestionEdit.getText().length() == 0)
                return;

            this.questionText.setText(this.ctrlQuestionEdit.getText());
            this.answerText.setText("I got three exact words for that - go fuck yourself");

            // Hide keyboard
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            // Stars drop
            starsGifView.setGif(starsGifDrop);
            starsGifView.play();

            // Hide controls
            controlsUIAnimationHide();

            // Show Q&A
            showQuestionTextAnimation = AnimationUtils.loadAnimation(this, R.anim.show_question_text_animation);
            showQuestionTextAnimation.setAnimationListener(this);
            showQuestionTextAnimation.setFillAfter(true);
            this.questionText.startAnimation(showQuestionTextAnimation);

            showAnswerTextAnimation = AnimationUtils.loadAnimation(this, R.anim.show_answer_text_animation);
            showAnswerTextAnimation.setAnimationListener(this);
            showAnswerTextAnimation.setFillAfter(true);
            this.answerText.startAnimation(showAnswerTextAnimation);
        }

        if (shakeAction == ShakeAction.TRYAGAIN) {
            // Hide Answer
            hideAnswerTryAgainTextAnimation = AnimationUtils.loadAnimation(this, R.anim.hide_answer_text_animation);
            hideAnswerTryAgainTextAnimation.setAnimationListener(this);
            hideAnswerTryAgainTextAnimation.setFillAfter(true);
            this.answerText.startAnimation(hideAnswerTryAgainTextAnimation);

        }
    }


    /**** GIF animation delegate ****/
    public void gifAnimationDidStart(GIFObject object) {

    }

    public void gifAnimationDidStop(GIFObject object) {

    }

    public void gifAnimationDidFinishLoop(GIFObject object, int loopIndex) {

    }

    public void gifAnimationDidFinish(GIFObject object) {
        if (object == starsGifBegin) {
            controlsUIAnimationShow();
        }

        if (object == manGifCatch) {
            // Man catch the star
            manImageView.setGif(manGifStar);
            manImageView.play();
        }

        if (object == manGifDrop) {
            // Man drop the star
            manImageView.setGif(manGifStatic);
            manImageView.play();
        }

    }

    public void gifAnimationShowFrame(GIFObject object, int frameIndex) {
        if (object == starsGifDrop && frameIndex == 20) {
            // Man catch the star
            manImageView.setGif(manGifCatch);
            manImageView.play();
        }

    }

    /**** UI animation delegate ****/
    public void onAnimationStart(Animation animation) {

        // Controls
        if (animation == whatAnimationShow)
            this.ctrlWhatIsYourQuestion.setVisibility(View.VISIBLE);
        if (animation == editAnimationShow) {
            this.ctrlQuestionEdit.setVisibility(View.VISIBLE);
            this.ctrlQuestionEdit.layout(this.ctrlQuestionEdit.getLeft(), this.ctrlQuestionEdit.getTop() - 145, this.ctrlQuestionEdit.getRight(), this.ctrlQuestionEdit.getBottom() - 145);
        }
        if (animation == handAnimationShow)
            this.handImageView.setVisibility(View.VISIBLE);
        if (animation == shakeAnimationShow) {
            this.ctrlShakeText.setVisibility(View.VISIBLE);
            shakeAction = ShakeAction.ANSWER;
        }

        if (animation == showQuestionTextAnimation)
            this.questionText.setVisibility(View.VISIBLE);

        if (animation == showAnswerTextAnimation) {
            this.answerText.setVisibility(View.VISIBLE);
        }

    }

    public void onAnimationEnd(Animation animation) {
        if (this.planetImageView.getAnimation() == animation) {

        }

        // Controls
        if (animation == whatAnimationHide)
            this.ctrlWhatIsYourQuestion.setVisibility(View.GONE);
        if (animation == editAnimationHide)
            this.ctrlQuestionEdit.setVisibility(View.GONE);
        if (animation == handAnimationHide)
            this.handImageView.setVisibility(View.GONE);
        if (animation == shakeAnimationHide)
            this.ctrlShakeText.setVisibility(View.GONE);

        if (animation == showAnswerTextAnimation) {
            shakeAction = ShakeAction.TRYAGAIN;
        }

        if (animation == hideAnswerTryAgainTextAnimation) {
            this.answerText.setVisibility(View.GONE);

            showAnswerTextAnimation = AnimationUtils.loadAnimation(this, R.anim.show_answer_text_animation);
            showAnswerTextAnimation.setAnimationListener(this);
            showAnswerTextAnimation.setFillAfter(true);
            showAnswerTextAnimation.setStartOffset(0);
            this.answerText.startAnimation(showAnswerTextAnimation);
        }

        if (animation == hideQuestionTextAnimation) {
            this.answerText.setVisibility(View.GONE);
            this.questionText.setVisibility(View.GONE);

            controlsUIAnimationShow();
        }

    }

    public void onAnimationRepeat(Animation animation) {


    }


    /**** OnClick Listener ****/
    @Override
    public void onClick(View v) {
        if (v == tryAgainButton) {
            // Hide Q&A
            hideAnswerTextAnimation = AnimationUtils.loadAnimation(this, R.anim.hide_answer_text_animation);
            hideAnswerTextAnimation.setAnimationListener(this);
            hideAnswerTextAnimation.setFillAfter(true);
            this.answerText.startAnimation(hideAnswerTextAnimation);

            hideQuestionTextAnimation = AnimationUtils.loadAnimation(this, R.anim.hide_question_text_animation);
            hideQuestionTextAnimation.setAnimationListener(this);
            hideQuestionTextAnimation.setFillAfter(true);
            hideQuestionTextAnimation.setStartOffset(0);
            this.questionText.startAnimation(hideQuestionTextAnimation);

            // Man drop star
            manImageView.setGif(manGifDrop);
            manImageView.play();

            // Stars return
            starsGifView.setGif(starsGifBack);
            starsGifView.play();

        }
    }

}
