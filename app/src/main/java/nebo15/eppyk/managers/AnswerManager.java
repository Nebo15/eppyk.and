package nebo15.eppyk.managers;

import android.content.Context;
import android.util.Log;

import java.util.Random;

import nebo15.eppyk.R;
import nebo15.eppyk.data_api.EppykAnswer;

/**
 * Created by anton on 17/04/16.
 */
public class AnswerManager {

    static private DBManager db;

    public enum AnswerSource {
        L10N, DB
    }

    static public String getAnswer(Context context) {
        db = new DBManager(context);
        if (getSource() == AnswerSource.DB)
            return getDBAnswer();
        else
            return getL10NAnswer(context);
    }

    static private AnswerSource getSource() {
        if (db.getAnswersCount() > 0)
            return AnswerSource.DB;

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
        EppykAnswer answer = db.getRandomAnswer();
        return answer.text;
    }

}
