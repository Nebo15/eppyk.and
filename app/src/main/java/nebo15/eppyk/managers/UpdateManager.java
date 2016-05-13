package nebo15.eppyk.managers;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import nebo15.eppyk.data_api.EppykAPI;
import nebo15.eppyk.data_api.EppykAnswer;
import nebo15.eppyk.data_api.EppykL10Ns;
import nebo15.eppyk.data_api.EppykL10nAnswers;
import nebo15.eppyk.data_api.L10N;
import nebo15.eppyk.data_api.Meta;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by anton on 17/04/16.
 */


public class UpdateManager implements Callback {

    public interface APICallback {
        void apiFail(String error);
        void apiL10NsLoaded(List<L10N> items);
        void apiAnswersLoaded(List items);
    }


    public APICallback callback;
    public Context context;
    private final String dateFormat = "yyyy-MM-dd HH:mm:ss";

    private static UpdateManager mInstance = null;

    private UpdateManager() {}

    public static UpdateManager getInstance() {
        if(mInstance == null)
        {
            mInstance = new UpdateManager();
        }
        return mInstance;
    }


    public void loadL10ns() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://128.199.50.95/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        // prepare call in Retrofit 2.0
        EppykAPI eppykAPI = retrofit.create(EppykAPI.class);
        Call<EppykL10Ns> call = eppykAPI.loadL10ns();
        //asynchronous call
        call.enqueue(this);
    }

    private String lastLoadedL10n;
    public void loadAnswers(String l10n) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://128.199.50.95/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

//        Date date = UpdateManager.getInstance().getLastUpdateDate();

        // prepare call in Retrofit 2.0
        EppykAPI eppykAPI = retrofit.create(EppykAPI.class);
        Call<EppykL10nAnswers> call = eppykAPI.loadAnswers(l10n);
        lastLoadedL10n = l10n;
        //asynchronous call
        call.enqueue(this);
    }


    @Override
    public void onResponse(Call call, Response response) {

        if (response.body() instanceof EppykL10Ns) {
            List<L10N> items = ((EppykL10Ns)response.body()).data;
            if (callback != null)
                callback.apiL10NsLoaded(items);
        } else if (response.body() instanceof EppykL10nAnswers) {
            UpdateManager.getInstance().setLastUpdateDate(new Date());

            DBManager db = new DBManager(context);
            List answers = ((EppykL10nAnswers)response.body()).data;
            Meta meta = ((EppykL10nAnswers)response.body()).meta;

            if (meta.getAppend() == 1 || (!lastLoadedL10n.equalsIgnoreCase(getCurrentL10N())) )
                db.deleteAllAnswers();

            for (Object _answer : answers) {
                EppykAnswer answer = (EppykAnswer)_answer;
                db.addAnswer(answer);
            }

            setCurrentL10N(lastLoadedL10n);

            if (callback != null)
                callback.apiAnswersLoaded(answers);
        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        if (callback != null)
            callback.apiFail(t.getLocalizedMessage());
    }


    private void setCurrentL10N(String l10n) {
        SharedPreferences settings = context.getSharedPreferences("EPPYK", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("CURR_L10N", l10n);

        // Commit the edits!
        editor.commit();
    }

    public String getCurrentL10N() {
        SharedPreferences settings = context.getSharedPreferences("EPPYK", 0);
        return settings.getString("CURR_L10N", "");
    }

    public void setLastUpdateDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
        String sDate = sdf.format(date);

        SharedPreferences settings = context.getSharedPreferences("EPPYK", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("UPDATE_DATE", sDate);

        // Commit the edits!
        editor.commit();
    }

    public Date getLastUpdateDate() {
        SharedPreferences settings = context.getSharedPreferences("EPPYK", 0);
        String sDate = settings.getString("UPDATE_DATE", "");

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
        try {
            return sdf.parse(sDate);
        } catch (ParseException e) {
            return new Date(0);
        }
    }

}
