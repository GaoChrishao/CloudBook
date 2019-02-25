package MyDatabaseUtil;

import java.sql.*;

import databasestudy.ConstValue;



public class DatabaseUtil {
	public  String DB_URL = "jdbc:mysql://"+ConstValue.serverIp+"/"+ConstValue.serverDbName+"?useSSL=false&autoReconnect=true&serverTimezone=GMT%2B8";
    public final String USER = ConstValue.sqlUername;
    public final String PASS = ConstValue.sqlPassword; 
    public Connection conn=null;
    public Statement stmt=null;
    
    
    /**
     * connet to database
     * @return
     */
    public DatabaseUtil() {
    	if(conn==null||stmt==null)
    	connectToDababase();
    }
    
    public int connectToDababase() {
    	try {
    		Class.forName("com.mysql.cj.jdbc.Driver");
    		System.out.println("Connectnig to database...");
    		conn = DriverManager.getConnection(DB_URL,USER,PASS);
    		System.out.println("Create statement...");
    		stmt = conn.createStatement();
    		System.out.println("Finish!");
    		return 1;
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	return 0;
    }
    
    /**
     *@see search
     * @param sql
     * @return ResultSet
     */
    public  ResultSet search(String sql) {
    	try {
    		if(conn==null||stmt==null) {
    			connectToDababase();
    		}
    		
			ResultSet rs=stmt.executeQuery(sql);
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     *@see close the connect
     * @return
     */
    public  int close() {
    		try {
    			if(stmt!=null)stmt.close();
    			if(conn!=null)conn.close();
    			return 1;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	return 0;
    }
    
    /**
     * @param sql
     * @see"UPDATE booklist"+"SET author = \"GCH\" WHERE id =1";
     * @return 1==finish;0==wrong
     */
    public  int update(String sql) {
    	if(stmt!=null) {
    		try {
				stmt.executeUpdate(sql);
				return 1;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return 0;
    	}else {
    		connectToDababase();
    		System.out.println("try connect to database......");
    		try {
				stmt.executeUpdate(sql);
				return 1;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
    	}
    }
    
  //添加的插入函数 使用execute
  	public boolean insert(String sql) {
  		try {
  			if (conn == null || stmt == null) {
  				connectToDababase();
  			}			
  			stmt.execute(sql);
  			return true;
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}

  		return false;
  	}
  	
  	
  	//插入数据并返回主键
  	public int insertGetKey(String sql) {
  		try {
  			if (conn == null || stmt == null) {
  				connectToDababase();
  			}
  			PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//参数2最好写上，虽然Mysql不写也能获取但是不代表别的数据库可以做到 
  	        ps.executeUpdate();
  	        ResultSet rs = ps.getGeneratedKeys(); 
  	        int id = 0; 
  	        if (rs.next()) 
  	            id = rs.getInt(1); 
  	        return id; 
  			
  			
  	
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}

  		return -1;
  	}

    
}
