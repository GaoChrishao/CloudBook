package MyClass.Utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class LocalDatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private final int INITIAL_VERSION = 1 ; // 初始版本号
    public static int NEW_VERSION = 2 ;       // 最新的版本号 ,增加Focus表
    public static final String CREATE_WRITE="create table write("
            +"id integer primary key autoincrement,"
            +"userid integer,"
            + "bookid integer,"
            +"bookname varchar,"
            + "content text,"
            +"time date)";
    public static final String CREATE_FOCUS="create table focus("
            +"id integer primary key autoincrement,"
            +"userid integer,"
            + "bookid integer,"
            +"bookname varchar,"
            + "start_time date,"
            + "end_time date)";

    public static final String CREATE_HOPE="create table hope("
            +"bookid integer primary key autoincrement,"
            +"userid integer,"
            +"bookname varchar,"
            + "writer varchar,"
            +"cover varchar,"
            +"price float)";

    public static final String CREATE_HAS="create table has("
            +"bookid integer primary key autoincrement,"
            +"userid integer,"
            +"bookname varchar,"
            + "writer varchar,"
            +"cover varchar,"
            +"allpages integer,"
            +"readpages integer,"
            +"lasttime date,"
            +"price float)";

    public LocalDatabaseHelper(Context context,String name,SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WRITE);
        db.execSQL(CREATE_HOPE);
        db.execSQL(CREATE_HAS);
        db.execSQL(CREATE_FOCUS);
        Toast.makeText(context, "本地数据库创建成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //判断用户当前安装的本是不是1.0版本
        if(oldVersion == INITIAL_VERSION &&newVersion==2){
            db.execSQL(CREATE_FOCUS);
            oldVersion++;
        }
        Toast.makeText(context, "数据库升级", Toast.LENGTH_SHORT).show();
    }
}
