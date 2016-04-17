package nebo15.eppyk.managers;

import android.content.Context;
import android.util.Log;

import java.util.Random;

import nebo15.eppyk.R;

/**
 * Created by anton on 17/04/16.
 */
public class AnswerManager {

    public enum AnswerSource {
        L10N, DB
    }

    static public String getAnswer(Context context) {
        if (getSource() == AnswerSource.DB)
            return getDBAnswer();
        else
            return getL10NAnswer(context);
    }

    static private AnswerSource getSource() {
        return AnswerSource.L10N;
    }

    static private String getL10NAnswer(Context context) {
        int answersCount = Integer.parseInt(context.getString(R.string.answers_count));
        int randomIndex = new Random().nextInt(answersCount);
        int resId = context.getResources().getIdentifier(String.format("answer_%d", randomIndex), "string", "nebo15.eppyk");

        String answer = context.getString(resId);
        Log.i("EPPYK", String.format("Answer #%d - %s", randomIndex, answer));

        return answer;
    }

    static private String getDBAnswer() {
        return "DB is empty";
    }

}
