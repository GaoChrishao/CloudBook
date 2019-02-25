package cloudbook.gaoch.com;

public class ConstValue {
    public static String serverIp ="101.132.181.104";   //149.28.15.40
    public static String serverUserPic ="http://101.132.181.104/cloudbook/user/";
    public static int serverPortLogin=1196;
    public static int serverPortSignUp=1197;
    public static int serverPortBook=1198;
    public static int serverPortComment = 1199;
    public static int serverPortgetComments = 1200;
    public static int serverPortWriter = 1201;
    public static int serverPortgetWriters = 1202;
    public static int serverPorthopingbook = 1203;// 愿望书单
    public static int serverPortactivation = 1204;// 激活服务
    public static int serverPortUserInfor = 1205;// 激活服务
    public static String spAccount="spaccount";
    public static String LocalDatabaseName="cloudbook.db";

    public static String LocalDB_TABLE_WRITE="WRITE";
    public static String LocalDB_TABLE_HAS="HAS";
    public static String LocalDB_TABLE_HOPE="HOPE";
    public static String LocalDB_TABLE_FOCUS="focus";


    public static String spTime ="spTime";
    public static String focusHour="focus_hour";
    public static String focusMinute="focus_minute";
    public static String focusSecond="focus_second";

    public static String databaseUserId ="id";
    public static String databaseUserName ="userName";
    public static String databaseUserpassword="password";
    public static String databaseUserexp="exp";
    public static String databaseUserRT="readtime";
    public static String databaseUserBooks="books";
    public static String databaseUserachi="achis";

    public static final String scanCode_book="1";
    public static final String scanCode_focus="2";
    public static final String scanCode_write="3";
    public static final String scanCode_jihuo="4";

    public static final int LoginReturnCode_sucess=1;
    public static final int LoginReturnCode_wrong=-1;
    public static final int LoginReturnCode_noAccount=0;

    public static final int SignUpReturnCode_sucess=2;
    public static final int SignUpReturnCode_wrong=3;
    public static final int SignUpReturnCode_hasExistAccount=4;
    public static final int SignUpReturnCode_serverWrong=5;




    public static String isBackGroundPNG="isBackBroundPNG";
    public static String UserPic="UserPic";
    public static String isBlur="isBlur";
    //毛玻璃效果参数
    public static int RoundCorner=50;
    public static int radius=10;
    public static int scaleFactor=26;
    private static String configDataName="spdataname";
    public static String getConfigDataName(){
        return configDataName;
    }
    public static String getIsBackGroundPNG() {
        return isBackGroundPNG;
    }


    public static final int CommentReturnCode_success=1;
    public static final int CommentReturnCode_wrong=-1;
    public static final int CommentReturnCode_noAccount=0;
    public static final int CommentReturnCode_noBook=2;
    public static final int CommentReturnCode_contentTooShort=3;
    public static final int CommentReturnCode_serverWrong=5;



    public static final int userInfor_type_sendChnage=1;
    public static final int userInfor_type_getInfo=2;
    public static final int userInfor_type_changeSucess=3;
    public static final int userInfor_type_changeFail=4;
    public static final int userInfor_type_changeWrongpwd=-1;




}
