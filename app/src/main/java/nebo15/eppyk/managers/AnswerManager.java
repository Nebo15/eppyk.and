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
    static public boolean isDbBlocked;

    public enum AnswerSource {
        L10N, DB
    }

    static public String[] getAnswer(Context context) {
        db = new DBManager(context);
        if (getSource() == AnswerSource.DB) {
            Log.i("ANSWER", "DB");
            return getDBAnswer();
        } else {
            Log.i("ANSWER", "Local");
            return getL10NAnswer(context);
        }
    }

    static private AnswerSource getSource() {
        if (!isDbBlocked && db.getAnswersCount() > 0)
            return AnswerSource.DB;

        return AnswerSource.L10N;
    }

    static private String[] getL10NAnswer(Context context) {
        int answersCount = Integer.parseInt(context.getString(R.string.answers_count));
        int randomIndex = new Random().nextInt(answersCount);
        int answerId = context.getResources().getIdentifier(String.format("answer_%d", randomIndex), "string", "nebo15.eppyk");
        int authorId = context.getResources().getIdentifier(String.format("author_%d", randomIndex), "string", "nebo15.eppyk");

        String answer = context.getString(answerId);
        String author = context.getString(authorId);
        Log.i("EPPYK", String.format("Answer #%d - %s", randomIndex, answer));

        String[] ar = new String[2];
        ar[0] = answer;
        ar[1] = author;
        return ar;
    }

    static private String[] getDBAnswer() {
        EppykAnswer answer = db.getRandomAnswer();
        String[] ar = new String[2];
        ar[0] = answer.text;
        ar[1] = answer.author;
        return ar;
    }

}
