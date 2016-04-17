package nebo15.eppyk.managers;

import java.util.List;

import nebo15.eppyk.data_api.EppykAPI;
import nebo15.eppyk.data_api.EppykL10Ns;
import nebo15.eppyk.data_api.EppykL10nAnswers;
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
        void apiL10NsLoaded(List items);
        void apiAnswersLoaded(List items);
    }


    public APICallback callback;

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

    public void loadAnswers(String l10n) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://128.199.50.95/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        // prepare call in Retrofit 2.0
        EppykAPI eppykAPI = retrofit.create(EppykAPI.class);
        Call<EppykL10nAnswers> call = eppykAPI.loadAnswers(l10n);
        //asynchronous call
        call.enqueue(this);

    }

    @Override
    public void onResponse(Call call, Response response) {

        if (response.body() instanceof EppykL10Ns) {
            List items = ((EppykL10Ns)response.body()).data;
            if (callback != null)
                callback.apiL10NsLoaded(items);
        } else if (response.body() instanceof EppykL10nAnswers) {
            List answers = ((EppykL10nAnswers)response.body()).data;
            if (callback != null)
                callback.apiAnswersLoaded(answers);
        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        if (callback != null)
            callback.apiFail(t.getLocalizedMessage());
    }
}
