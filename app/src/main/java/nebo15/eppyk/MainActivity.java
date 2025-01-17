package nebo15.eppyk;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import nebo15.eppyk.data_api.EppykAnswer;
import nebo15.eppyk.data_api.L10N;
import nebo15.eppyk.gif.GIFObject;
import nebo15.eppyk.gif.GIFView;
import nebo15.eppyk.gif.IGIFEvent;
import nebo15.eppyk.listeners.EventManager;
import nebo15.eppyk.listeners.ShakeEventListener;
import nebo15.eppyk.managers.AnswerManager;
import nebo15.eppyk.managers.DBManager;
import nebo15.eppyk.managers.ImageManager;
import nebo15.eppyk.managers.UpdateManager;


public class MainActivity extends AppCompatActivity implements Animation.AnimationListener, IGIFEvent, View.OnClickListener, UpdateManager.APICallback {

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
    TextView authorText;
    View whiteView;


    ImageButton globusButton;
    Button saveButton;
    Button tryAgainButton;
    RelativeLayout shakeAgainHint;

    // UI Ainmations
    ImageView planetImageView;
    ImageView logoImageView;

    GIFView manImageView;
    GIFView dogImageView;

    GIFView starsGifView;
    ImageView starsGifView1;

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
    GIFObject dogMoves[] = new GIFObject[4];

    Handler dogHandler = null;
    Handler manHandler = null;

    private DBManager db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Mixpanel init
        EventManager.init(MainActivity.this);
        EventManager.trackEvent("Application start", null);

        // Networking
        UpdateManager.getInstance().callback = this;
        UpdateManager.getInstance().context = this;

        // DB
        this.db = new DBManager(this);
        AnswerManager.isDbBlocked = false;
        starInHand = false;

        // Fragment

        // Shake
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

            public void onShake() {
                if (ctrlQuestionEdit.getText().length() == 0)
                    return;

                if (shakeAction == ShakeAction.ANSWER) {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("Question", ctrlQuestionEdit.getText().toString());
                    EventManager.trackEvent("Shake for answer", params);
                } else if (shakeAction == ShakeAction.TRYAGAIN) {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("Question", ctrlQuestionEdit.getText().toString());
                    EventManager.trackEvent("Shake for another answer", params);
                }
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
        this.starsGifView1 = (ImageView) findViewById(R.id.StarsGifView1);

        this.manImageView = (GIFView) findViewById(R.id.ManImageView);
        this.dogImageView = (GIFView) findViewById(R.id.DogImageView);

        // Control UI
        this.ctrlWhatIsYourQuestion = (TextView) findViewById(R.id.CtrlWhatIsYourQuestion);
        this.ctrlWhatIsYourQuestion.setTypeface(fontGeneralBold);

        this.ctrlQuestionEdit = (EditText) findViewById(R.id.CtrlQuestionEdit);
        this.ctrlQuestionEdit.setTypeface(fontGeneralBold);

        this.ctrlQuestionEdit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (ctrlQuestionEdit.getText().length() == 0)
                        return true;

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("Question", ctrlQuestionEdit.getText().toString());
                    EventManager.trackEvent("Go press for answer", params);
                    showAnswer();
                    return true;
                }
                return false;
            }
        });


        this.handImageView = (GIFView) findViewById(R.id.handImageView);
        this.handImageView.setHandler(this);
        this.handImageView.setGif(handGif);

        this.ctrlShakeText = (TextView) findViewById(R.id.CtrlShakeText);
        this.ctrlShakeText.setTypeface(fontGeneralBold);

        // Q&A
        this.questionText = (EditText) findViewById(R.id.QuestionText);
        this.questionText.setTypeface(fontGeneral);

        this.answerText = (TextView) findViewById(R.id.AnswerText);
        this.answerText.setTypeface(fontGeneralBold);

        this.authorText = (TextView) findViewById(R.id.AuthorText);
        this.authorText.setTypeface(fontGeneralBold);

        this.saveButton = (Button) findViewById(R.id.SaveButton);
        this.saveButton.setTypeface(fontGeneralBold);
        this.saveButton.setOnClickListener(this);

        this.tryAgainButton = (Button) findViewById(R.id.TryAgainButton);
        this.tryAgainButton.setTypeface(fontGeneralBold);
        this.tryAgainButton.setOnClickListener(this);

        this.shakeAgainHint = (RelativeLayout) findViewById(R.id.ShakeAgainHint);
        ((TextView)findViewById(R.id.ShakeHint)).setTypeface(fontGeneralBold);

        this.whiteView = (View) findViewById(R.id.WhiteView);

        this.globusButton = (ImageButton) findViewById(R.id.GlobusButton);
        this.globusButton.setOnClickListener(this);

        prepareAnimation();
        startAnimation();

        dogHandler = new Handler(Looper.getMainLooper());
        manHandler = new Handler(Looper.getMainLooper());

        startTimer();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);



        // Show L10n select view on first start
        if (UpdateManager.getInstance().isFirstStart()) {
            l10nViewRequest(true);
            UpdateManager.getInstance().setFirstStart(false);
        }

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

        // Really 42 frames but should be 50. Just because!
        manGifDrop = new GIFObject(getResources().openRawResource(R.drawable.man_star_drop), 42);
        manGifDrop.loopsCount = 1;


        manMoves[0] = new GIFObject(getResources().openRawResource(R.drawable.man_move_1), 57);
        manMoves[0].loopsCount = 1;

        manMoves[1] = new GIFObject(getResources().openRawResource(R.drawable.man_move_2), 46);
        manMoves[1].loopsCount = 1;

        manMoves[2] = new GIFObject(getResources().openRawResource(R.drawable.man_move_3), 70);
        manMoves[2].loopsCount = 1;

        // Dog moves
        dogMoves[0] = new GIFObject(getResources().openRawResource(R.drawable.dog_move_1), 21);
        dogMoves[0].loopsCount = 1;

        dogMoves[1] = new GIFObject(getResources().openRawResource(R.drawable.dog_move_2), 24);
        dogMoves[1].loopsCount = 1;

        dogMoves[2] = new GIFObject(getResources().openRawResource(R.drawable.dog_move_3), 58);
        dogMoves[2].loopsCount = 1;

        dogMoves[3] = new GIFObject(getResources().openRawResource(R.drawable.dog_move_4), 88);
        dogMoves[3].loopsCount = 1;

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

    Animation showLeftButtonAnimation, showRightButtonAnimation;

    private void showButtons() {

        showLeftButtonAnimation = new TranslateAnimation((saveButton.getWidth() + 50) * -1, 0, 0, 0);
        showLeftButtonAnimation.setDuration(1000);
        showLeftButtonAnimation.setStartOffset(1000);
        showLeftButtonAnimation.setFillAfter(true);
        showLeftButtonAnimation.setAnimationListener(this);
        showLeftButtonAnimation.setInterpolator(new OvershootInterpolator());
        this.saveButton.startAnimation(showLeftButtonAnimation);

        showRightButtonAnimation = new TranslateAnimation((tryAgainButton.getWidth() + 50), 0, 0, 0);
        showRightButtonAnimation.setDuration(1000);
        showRightButtonAnimation.setStartOffset(1000);
        showRightButtonAnimation.setFillAfter(true);
        showRightButtonAnimation.setAnimationListener(this);
        showRightButtonAnimation.setInterpolator(new OvershootInterpolator());
        this.tryAgainButton.startAnimation(showRightButtonAnimation);

    }

    Animation hideLeftButtonAnimation, hideRightButtonAnimation;

    private void hideButtons() {

        hideLeftButtonAnimation = new TranslateAnimation(0, (saveButton.getWidth() + 50) * -1, 0, 0);
        hideLeftButtonAnimation.setDuration(2000);
        hideLeftButtonAnimation.setStartOffset(1500);
        hideLeftButtonAnimation.setFillAfter(true);
        hideLeftButtonAnimation.setAnimationListener(this);
        hideLeftButtonAnimation.setInterpolator(new OvershootInterpolator());
        this.saveButton.startAnimation(hideLeftButtonAnimation);

        hideRightButtonAnimation = new TranslateAnimation(0, (tryAgainButton.getWidth() + 50), 0, 0);
        hideRightButtonAnimation.setDuration(2000);
        hideRightButtonAnimation.setStartOffset(1500);
        hideRightButtonAnimation.setFillAfter(true);
        hideRightButtonAnimation.setAnimationListener(this);
        hideRightButtonAnimation.setInterpolator(new OvershootInterpolator());
        this.tryAgainButton.startAnimation(hideRightButtonAnimation);

    }


    /****
     * Actions
     ****/
    Animation showQuestionTextAnimation, hideQuestionTextAnimation, showAnswerTextAnimation, hideAnswerTextAnimation,
            hideAnswerTryAgainTextAnimation, showAuthorTextAnimation, hideAuthorTextAnimation, showShakeHintAnimation, hideShakeHintAnimation;

    public void showAnswer() {

        if (shakeAction == ShakeAction.NONE)
            return;

        if (shakeAction == ShakeAction.ANSWER) {

            shakeAction = ShakeAction.NONE;

            this.questionText.setText(this.ctrlQuestionEdit.getText());

            String[] answer = AnswerManager.getAnswer(this);
            this.answerText.setText(answer[0]);
            this.authorText.setText(answer[1]);

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

            showAuthorTextAnimation = AnimationUtils.loadAnimation(this, R.anim.show_author_text_animation);
            showAuthorTextAnimation.setAnimationListener(this);
            showAuthorTextAnimation.setFillAfter(true);
            this.authorText.startAnimation(showAuthorTextAnimation);

            showShakeHintAnimation = AnimationUtils.loadAnimation(this, R.anim.show_author_text_animation);
            showShakeHintAnimation.setAnimationListener(this);
            showShakeHintAnimation.setFillAfter(true);
            this.shakeAgainHint.startAnimation(showShakeHintAnimation);

            // Show buttons
            showButtons();
        }

        if (shakeAction == ShakeAction.TRYAGAIN) {
            // Hide Answer
            hideAnswerTryAgainTextAnimation = AnimationUtils.loadAnimation(this, R.anim.hide_answer_text_animation);
            hideAnswerTryAgainTextAnimation.setAnimationListener(this);
            hideAnswerTryAgainTextAnimation.setFillAfter(true);
            this.answerText.startAnimation(hideAnswerTryAgainTextAnimation);

            hideAuthorTextAnimation = AnimationUtils.loadAnimation(this, R.anim.hide_author_text_animation);
            hideAuthorTextAnimation.setAnimationListener(this);
            hideAuthorTextAnimation.setFillAfter(true);
            this.authorText.startAnimation(hideAuthorTextAnimation);
        }
    }


    /****
     * GIF animation delegate
     ****/
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
            starInHand = false;
        }

        if (manMoves[0] == object || manMoves[1] == object || manMoves[2] == object) {
            manImageView.setGif(manGifStatic);
            manImageView.play();
        }

    }

    private boolean starInHand;
    public void gifAnimationShowFrame(GIFObject object, int frameIndex) {
        if (object == starsGifDrop && frameIndex > 14 && !starInHand) {
            starInHand = true;
            // Man catch the star
            manImageView.setGif(manGifCatch);
            manImageView.play();
            Log.i("EPPYK", "Catch star");
        }

    }

    /****
     * UI animation delegate
     ****/
    public void onAnimationStart(Animation animation) {

        // Controls
        if (animation == whatAnimationShow)
            this.ctrlWhatIsYourQuestion.setVisibility(View.VISIBLE);
        if (animation == editAnimationShow) {
            this.ctrlQuestionEdit.setVisibility(View.VISIBLE);
            this.ctrlQuestionEdit.layout(this.ctrlQuestionEdit.getLeft(), this.ctrlQuestionEdit.getTop() - 145, this.ctrlQuestionEdit.getRight(), this.ctrlQuestionEdit.getBottom() - 145);
        }
        if (animation == handAnimationShow) {
            this.handImageView.setVisibility(View.VISIBLE);
        }
        if (animation == shakeAnimationShow) {
            this.ctrlShakeText.setVisibility(View.VISIBLE);
            shakeAction = ShakeAction.ANSWER;
        }

        if (animation == showQuestionTextAnimation)
            this.questionText.setVisibility(View.VISIBLE);

        if (animation == showAnswerTextAnimation) {
            this.answerText.setVisibility(View.VISIBLE);
        }

        if (animation == showAuthorTextAnimation) {
            this.authorText.setVisibility(View.VISIBLE);
        }

        if (animation == showShakeHintAnimation) {
            this.shakeAgainHint.setVisibility(View.VISIBLE);
        }

        if (animation == hideShakeHintAnimation) {
            this.shakeAgainHint.setVisibility(View.GONE);
        }

        if (animation == showLeftButtonAnimation) {
            this.saveButton.setAlpha(1);
            this.saveButton.setEnabled(true);
        }

        if (animation == showRightButtonAnimation) {
            this.tryAgainButton.setAlpha(1);
            this.tryAgainButton.setEnabled(true);
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
        if (animation == handAnimationHide) {
            this.handImageView.setVisibility(View.GONE);
        }
        if (animation == shakeAnimationHide)
            this.ctrlShakeText.setVisibility(View.GONE);

        if (animation == showAnswerTextAnimation) {
            shakeAction = ShakeAction.TRYAGAIN;
        }

        if (animation == hideAnswerTryAgainTextAnimation) {
            this.answerText.setVisibility(View.GONE);

            String[] answer = AnswerManager.getAnswer(this);
            this.answerText.setText(answer[0]);
            this.authorText.setText(answer[1]);

            showAnswerTextAnimation = AnimationUtils.loadAnimation(this, R.anim.show_answer_text_animation);
            showAnswerTextAnimation.setAnimationListener(this);
            showAnswerTextAnimation.setFillAfter(true);
            showAnswerTextAnimation.setStartOffset(0);
            this.answerText.startAnimation(showAnswerTextAnimation);

            showAuthorTextAnimation = AnimationUtils.loadAnimation(this, R.anim.show_author_text_animation);
            showAuthorTextAnimation.setAnimationListener(this);
            showAuthorTextAnimation.setFillAfter(true);
            showAuthorTextAnimation.setStartOffset(0);
            this.authorText.startAnimation(showAuthorTextAnimation);

        }

        if (animation == hideQuestionTextAnimation) {
            this.answerText.setVisibility(View.GONE);
            this.questionText.setVisibility(View.GONE);

            controlsUIAnimationShow();
        }

        if (animation == hideLeftButtonAnimation) {
            this.saveButton.setAlpha(0);
            this.saveButton.setEnabled(false);
        }

        if (animation == hideRightButtonAnimation) {
            this.tryAgainButton.setAlpha(0);
            this.tryAgainButton.setEnabled(false);
        }


    }

    public void onAnimationRepeat(Animation animation) {

    }


    /****
     * OnClick Listener
     ****/
    boolean globeProcessing = false;
    @Override
    public void onClick(View v) {
        if (v == tryAgainButton) {
            tryAgain();
        }

        if (v == saveButton) {
            // Make screenshot
            makeScreenshot();
        }

        if (v == globusButton) {
            if (!globeProcessing)
                l10nViewRequest(false);
        }

    }


    private void tryAgain() {
        this.tryAgainButton.setEnabled(false);

        EventManager.trackEvent("Try again pressed", null);
        shakeAction = ShakeAction.NONE;
        ctrlQuestionEdit.setText("");

        // Hide Q&A
        hideAnswerTextAnimation = AnimationUtils.loadAnimation(this, R.anim.hide_answer_text_animation);
        hideAnswerTextAnimation.setAnimationListener(this);
        hideAnswerTextAnimation.setFillAfter(true);
        this.answerText.startAnimation(hideAnswerTextAnimation);

        hideAuthorTextAnimation = AnimationUtils.loadAnimation(this, R.anim.hide_author_text_animation);
        hideAuthorTextAnimation.setAnimationListener(this);
        hideAuthorTextAnimation.setFillAfter(true);
        this.authorText.startAnimation(hideAuthorTextAnimation);

        showShakeHintAnimation = AnimationUtils.loadAnimation(this, R.anim.hide_author_text_animation);
        showShakeHintAnimation.setAnimationListener(this);
        showShakeHintAnimation.setFillAfter(true);
        showShakeHintAnimation.setStartOffset(1000);
        this.shakeAgainHint.startAnimation(showShakeHintAnimation);

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

        // Hide buttons
        hideButtons();
    }

    private void makeScreenshot() {
        EventManager.trackEvent("Screenshot pressed", null);
        whiteView.setVisibility(View.VISIBLE);

        AlphaAnimation fade = new AlphaAnimation(1, 0);
        fade.setDuration(400);
        fade.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation anim) {
                whiteView.setVisibility(View.GONE);

                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                String filename = String.format("%s %s", getString(R.string.app_name), ctrlQuestionEdit.getText());

                saveButton.setAlpha(0f);
                tryAgainButton.setAlpha(0f);
                globusButton.setAlpha(0f);
                shakeAgainHint.setAlpha(0f);

                Bitmap screenshot = ImageManager.getScreenShot(rootView, MainActivity.this);

                saveButton.setAlpha(1.0f);
                tryAgainButton.setAlpha(1.0f);
                globusButton.setAlpha(1.0f);
                shakeAgainHint.setAlpha(1.0f);

                ImageManager.insertImage(getContentResolver(), screenshot, filename, "");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        whiteView.startAnimation(fade);
    }

    boolean onFirstLoad;
    private void l10nViewRequest(boolean onFirstLoad) {
        EventManager.trackEvent("Language select show", null);
        this.onFirstLoad = onFirstLoad;
        globeProcessing = true;

        Handler mainHandler = new Handler(this.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                globusButton.setImageResource(R.mipmap.loading);
            }
        };
        mainHandler.post(myRunnable);


        UpdateManager.getInstance().loadL10ns();
    }


    /****
     * API Callback
     ****/
    L10nFragment fragmentL10n;
    @Override
    public void apiL10NsLoaded(List<L10N> items) {
        Log.i("EPPYK", "Show L10n view");
        globeProcessing = false;
        Handler mainHandler = new Handler(this.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                globusButton.setImageResource(R.mipmap.globe);
            }
        };
        mainHandler.post(myRunnable);


        if (items.size() == 1 && this.onFirstLoad) {
            this.onFirstLoad = false;
            UpdateManager.getInstance().loadAnswers(items.get(0).code);
            return;
        }

        this.onFirstLoad = false;

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // Show Frame
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

        fragmentL10n = new L10nFragment();
        fragmentL10n.data = items.toArray(new L10N[items.size()]);
        ft.add(R.id.l10nFragment, fragmentL10n).commit();
    }

    @Override
    public void apiAnswersLoaded(List items) {
        Log.i("EPPYK", "Update answers");
    }

    @Override
    public void apiFail(String error) {
        globeProcessing = false;
        Handler mainHandler = new Handler(this.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                globusButton.setImageResource(R.mipmap.globe);
            }
        };
        mainHandler.post(myRunnable);
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    /****
     * Timers
     ****/
    // Timers
    Timer dogTimer;
    TimerTask dogTimerTask;

    Timer manTimer;
    TimerTask manTimerTask;

    public void startTimer() {
        //set a new Timer
        dogTimer = new Timer();
        manTimer = new Timer();

        initTimerTask();

        dogTimer.schedule(dogTimerTask, 8000, 8000);
        manTimer.schedule(manTimerTask, 5000, 5000);
    }

    public void initTimerTask() {

        dogTimerTask = new TimerTask() {
            public void run() {
                dogHandler.post(new Runnable() {
                    public void run() {
                        if (new Random().nextInt(2) == 0) {
                            Log.i("EPPYK", "Dog move");
                            dogImageView.setGif(dogMoves[new Random().nextInt(4)]);
                            dogImageView.play();
                        }
                    }
                });
            }
        };


        manTimerTask = new TimerTask() {
            public void run() {
                manHandler.post(new Runnable() {
                    public void run() {
                        if (shakeAction != ShakeAction.ANSWER)
                            return;

                        if (new Random().nextInt(3) == 0) {
                            manImageView.setGif(manMoves[new Random().nextInt(3)]);
                            manImageView.play();
                        }
                    }
                });
            }
        };
    }



}
