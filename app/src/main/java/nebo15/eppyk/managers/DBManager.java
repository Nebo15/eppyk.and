package nebo15.eppyk.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import nebo15.eppyk.data_api.EppykAnswer;

/**
 * Created by anton on 27/04/16.
 */
public class DBManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EPPYK.db";
    private static final int DATABASE_VERSION = 1;

    public static final String ANSWERS_TABLE_NAME = "answers";
    public static final String ANSWERS_COLUMN_ID = "id";
    public static final String ANSWERS_COLUMN_VALUE = "value";
    public static final String ANSWERS_COLUMN_AUTHOR = "author";


    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                String.format("CREATE TABLE %s (%s text primary key, %s text, %s text)", ANSWERS_TABLE_NAME, ANSWERS_COLUMN_ID, ANSWERS_COLUMN_VALUE, ANSWERS_COLUMN_AUTHOR)
        );
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", ANSWERS_TABLE_NAME));
        onCreate(db);
    }

    public boolean addAnswer(EppykAnswer answer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ANSWERS_COLUMN_ID, answer.id);
        contentValues.put(ANSWERS_COLUMN_VALUE, answer.text);
        contentValues.put(ANSWERS_COLUMN_AUTHOR, answer.author);

        return db.insert(ANSWERS_TABLE_NAME, null, contentValues) != -1;
    }

    public int getAnswersCount() {
        String countQuery = String.format("SELECT * FROM %s", ANSWERS_TABLE_NAME);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public EppykAnswer getAnswer(String id) {
        EppykAnswer answer = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( String.format("SELECT * FROM %s WHERE id='%s'", ANSWERS_TABLE_NAME, id), null);
        if (res.moveToFirst()) {
            String text = res.getString(1);
            String author = res.getString(2);
            answer = new EppykAnswer(id, text, author);
        }
        return answer;
    }

    public EppykAnswer getRandomAnswer() {
        EppykAnswer answer = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( String.format("SELECT * FROM %S ORDER BY RANDOM() LIMIT 1", ANSWERS_TABLE_NAME), null);
        if (res.moveToFirst()) {
            String id = res.getString(0);
            String text = res.getString(1);
            String author = res.getString(1);
            answer = new EppykAnswer(id, text, author);
        }
        return answer;
    }

    public Integer deleteAllAnswers()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ANSWERS_TABLE_NAME,
                null,
                null);
    }

}
