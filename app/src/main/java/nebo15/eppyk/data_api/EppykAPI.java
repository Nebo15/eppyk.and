package nebo15.eppyk.data_api;


import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.Call;

/**
 * Created by anton on 17/04/16.
 */
public interface EppykAPI {

    @GET("api/v1/locales")
    Call<EppykL10Ns> loadL10ns();

    @GET("api/v1/locales/{l10nCode}/answers")
    Call<EppykL10nAnswers> loadAnswers(@Path(value="l10nCode", encoded=false) String l10nCode);

}
