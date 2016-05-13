package nebo15.eppyk.data_api;

/**
 * Created by anton on 17/04/16.
 */
public class EppykAnswer {

    public String id;
    public String text;
    public String author;

    public EppykAnswer() {
        super();
    }

    public EppykAnswer(String id, String text, String author) {
        super();
        this.id = id;
        this.text = text;
        this.author = author;
    }
}
