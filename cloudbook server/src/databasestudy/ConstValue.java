package databasestudy;

public class ConstValue {
	public static final String serverIp="127.0.0.1:3306";
	public static final String serverDbName="cloudbook";  //yijibin
	
	public static final String sqlPassword="1195593460";  //123456789
	public static final String sqlUername="admin";
	
	public static final int serverPortLogin=1196;
	public static int serverPortSignUp=1197;
    public static int serverPortBook=1198;
    public static int serverPortComment = 1199;
    public static int serverPortgetComments = 1200;
    public static int serverPortWriter = 1201;
    public static int serverPortgetWriters = 1202;
	public static int serverPorthopingbook = 1203;// Ը���鵥
	public static int serverPortactivation = 1204;// �������
	public static int serverPortUserInfo = 1205;// �������
   
    
    public static final int LoginReturnCode_sucess=1;
    public static final int LoginReturnCode_wrong=-1;
    public static final int LoginReturnCode_noAccount=0;

    public static final int SignUpReturnCode_sucess=2;
    public static final int SignUpReturnCode_wrong=3;
    public static final int SignUpReturnCode_hasExistAccount=4;
    public static final int SignUpReturnCode_serverWrong=5;
    
    
    public static final int CommentReturnCode_success=1;
    public static final int CommentReturnCode_wrong=-1;
    public static final int CommentReturnCode_noAccount=0;
    public static final int CommentReturnCode_noBook=2;
    public static final int CommentReturnCode_contentTooShort=3;
    public static final int CommentReturnCode_serverWrong=5;
    
    
    public static final int exp_comments=10;
    public static final int exp_jihuobook=50;
    public static final int exp_write=20;

}
