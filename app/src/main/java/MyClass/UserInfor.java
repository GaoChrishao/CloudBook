package MyClass;

import android.support.annotation.Keep;

import java.io.Serializable;
import java.util.List;
@Keep
public class UserInfor implements Serializable {
    private static final long serialVersionUID = 1L;
    public String user_id,user_name;
    public int exp,achievements,allReadTime,allBook;
    public List<Achievement> achievementList;
    public List<Write>writeList;

    public UserInfor(String user_id, String user_name, int exp, int achievements, int allReadTime, int allBook, List<Achievement> achievementList, List<Write> writeList) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.exp = exp;
        this.achievements = achievements;
        this.allReadTime = allReadTime;
        this.allBook = allBook;
        this.achievementList = achievementList;
        this.writeList = writeList;
    }


}
