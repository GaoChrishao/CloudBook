package databasestudy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.crypto.Data;

import MyClass.ActivationMessage;
import MyClass.Book;
import MyClass.CommentList;
import MyClass.CommentMessage;
import MyClass.CommentPermission;
import MyClass.HopebookMessage;
import MyClass.LoginPermission;
import MyClass.SignUpMessage;
import MyClass.SignUpPermission;
import MyClass.SigninMessage;
import MyClass.User;
import MyClass.UserMessage;
import MyClass.Write;
import MyClass.WriteList;
import MyDatabaseUtil.DatabaseUtil;

public class ServerAll {
	static String id=null;
	static private DatabaseUtil databaseUtil=null;
	
	private final static Logger logger = Logger.getLogger(ServerAll.class.getName());

	public static void main(String[] args) throws IOException {
		//登入
		login();
		
		//获取书籍信息
		book();
		
		//注册
		signup();
		
		//发表评论
		comment();
		
		//获取评论
		getComments();
		
		//上传读书笔记
		write();
		
		//得到读书笔记
		getWrite();
		
		//激活书籍
		activation();
		
		//获取用户信息
		user();
	}
	public static void book() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int users=0;
				ServerSocket server =null;
				if(databaseUtil == null) {
					databaseUtil=new DatabaseUtil();
				}
				
				System.out.println("Server for Book start");
				try {
					server = new ServerSocket(ConstValue.serverPortBook);
				} catch (IOException e) {
					System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
					System.out.println("无法开启Book服务");
					e.printStackTrace();
				}
				while(true) {
					Socket socket;
					try {
						socket = server.accept();
						System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
						System.out.println("IP地址:"+socket.getInetAddress());
						runBook(socket);
						if((users++)>10000) {
							System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
							System.out.println("stop server");
							server.close();
							break;
						}
					} catch (IOException e) {
						System.out.println(new Timestamp(System.currentTimeMillis()).toString()+"  发生异常！");
						e.printStackTrace();
					}
					
				}	
			}
		}).start();
	}
	public static void login() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int users=0;
				ServerSocket server =null;
				if(databaseUtil == null) {
					databaseUtil=new DatabaseUtil();
				}
				
				System.out.println("Server for login start");
				try {
					server = new ServerSocket(ConstValue.serverPortLogin);
				} catch (IOException e) {
					System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
					System.out.println("无法开启登入服务");
					e.printStackTrace();
				}
				while(true) {
					Socket socket;
					try {
						socket = server.accept();
						System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
						System.out.println("IP地址:"+socket.getInetAddress());
						runLogin(socket);
						if((users++)>10000) {
							System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
							System.out.println("stop server");
							server.close();
							break;
						}
					} catch (IOException e) {
						System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
						e.printStackTrace();
					}
					
				}	
			}
		}).start();
	}
	private static void runLogin(final Socket socket)throws IOException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ObjectInputStream in = null;
				ObjectOutputStream out = null;
				try {
					in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
					Object object = in.readObject();
					SigninMessage msg = (SigninMessage) object;
					System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
					System.out.println("id:"+msg.getAccount()+" password:"+msg.getPassword());
					
					
					
					LoginPermission permission=new LoginPermission(checkAccountInDataBase(msg.getAccount(), msg.getPassword()),id);
					if(permission.permissionCode==1) {
						String sql="select * from userInfo where id="+msg.getAccount();
						ResultSet set=databaseUtil.search(sql);
						
						if(set!=null) {
							permission.userInfor=new UserMessage(msg.getAccount());
							try {
								while(set.next()) {
									permission.userInfor.setComments_num(Integer.valueOf(set.getString("comments_num")));
									permission.userInfor.setUsername(set.getString("username"));
									permission.userInfor.setExp(set.getInt("exp"));
									permission.userInfor.setBooks(set.getInt("books"));
									permission.userInfor.setAchieve(set.getInt("achieve"));
									
								}
							} catch (SQLException e) {
								System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
								System.out.println(new Timestamp(System.currentTimeMillis()).toString()+"  发生异常！");
								e.printStackTrace();
								
							}
						}
					}
					out=new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
					out.writeObject(permission);
				
					out.close();
					in.close();
					
					
				}catch(IOException e) {
					System.out.println(new Timestamp(System.currentTimeMillis()).toString()+"  发生异常！");
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					System.out.println(new Timestamp(System.currentTimeMillis()).toString()+"  发生异常！");
					e.printStackTrace();
				}finally {
				}
			}
		}).start();
	}
	private static void runBook(final Socket socket)throws IOException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ObjectInputStream in = null;
				ObjectOutputStream out = null;
				try {
					in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
					Object object = in.readObject();
					Book book = (Book) object;
					System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
					System.out.println("ip地址:"+socket.getInetAddress()+" 正在请求Book:"+book.getId());
					
					String sql="select * from books where id="+book.getId();
					ResultSet set=databaseUtil.search(sql);
					Book returnBook=null;
					if(set==null) {
						
					}
					String paw = null;
					try {
						while(set.next()) {
							returnBook=new Book(set.getInt("id")+"",set.getString("writer"),set.getInt("likes"),set.getString("title"),set.getString("content"),set.getInt("comments_num"),set.getString("pic"),set.getFloat("price"));
							returnBook.buynumber=set.getInt("buynumber");
						}
					} catch (SQLException e) {
						System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
						System.out.println(new Timestamp(System.currentTimeMillis()).toString()+"  发生异常！");
						e.printStackTrace();
						
					}
						
					if(returnBook==null) {
						returnBook=new Book("0");
					}
					out=new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
					out.writeObject(returnBook);
				
					out.close();
					in.close();
					
					
				}catch(IOException e) {
					System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
					e.printStackTrace();
					
				}finally {
				}
			}
		}).start();
	}
	public static int checkAccountInDataBase(String account,String pwd) {
		String sql="select * from userInfo where id="+account;
		ResultSet set=databaseUtil.search(sql);
		if(set==null) {
			return ConstValue.LoginReturnCode_noAccount;		//不存在改账号
		}
		String paw = null;
		try {
			while(set.next()) {
				paw=set.getString("password");
				id=set.getString("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(paw!=null&&!paw.equals(pwd)) {
			return ConstValue.LoginReturnCode_wrong;		//密码错误
		}else if(paw!=null&&paw.equals(pwd)){
			return ConstValue.LoginReturnCode_sucess;		//密码正确
		}else {
			return ConstValue.LoginReturnCode_wrong;		//密码错误
		}
		
	}
	
	
	
	public static void signup() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int users = 0;
				ServerSocket server = null;
				if(databaseUtil == null) {
					databaseUtil=new DatabaseUtil();
				}
				
				System.out.println("Server for sign up start");
				try {
					server = new ServerSocket(ConstValue.serverPortSignUp);
				} catch (IOException e) {
					System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
					System.out.println("无法开启注册服务");
					e.printStackTrace();
				}
				while (true) {
					Socket socket;
					try {
						socket = server.accept();
						System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
						System.out.println("IP地址:" + socket.getInetAddress());
						runSignup(socket);
						if ((users++) > 10000) {
							System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
							System.out.println("stop sign up server");
							server.close();
							break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}).start();
	}

	private static void runSignup(final Socket socket) throws IOException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ObjectInputStream in = null;
				ObjectOutputStream out = null;
				try {
					in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
					Object object = in.readObject();
					SignUpMessage msg = (SignUpMessage) object;// 注册信息SignUp对象接收
					System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" 注册账号：");
					System.out.println("id:" + msg.getAccount() + " password:" + msg.getPassword());
					SignUpPermission permission = new SignUpPermission(id,insertAccountInDataBase(msg.getAccount(), msg.getPassword()));
					out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
					out.writeObject(permission);

					out.close();
					in.close();

				} catch (IOException e) {
					System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
					e.printStackTrace();
				} finally {
				}
			}
		}).start();
	}

	public static int insertAccountInDataBase(String account, String pwd) {
		String sql = "select * from userInfo where id=" + account;
		ResultSet set = databaseUtil.search(sql);
		if (set != null) {
			try {
				while(set.next()) {
					return ConstValue.SignUpReturnCode_hasExistAccount;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 if (account.equals("") || pwd.equals("")||account.length()>15||account.length()<6||pwd.length()<6||pwd.length()>20) {
				return ConstValue.SignUpReturnCode_wrong; // Account or password cannot be empty
			}
		sql = "insert into userInfo (id,username,password,exp,readtime,achieve,comments_num,books) values('"+account+"','user"+account+"','"+pwd+"',0,0,0,0,0)";// 新加的
		boolean flag = databaseUtil.insert(sql);
		if (flag) {
			System.out.println("insert success");
		} else {
			System.out.println("insert have error");
			return ConstValue.SignUpReturnCode_serverWrong;
		}
		
		return ConstValue.SignUpReturnCode_sucess;// can sign up

	}
	
	public static int insertCommentInDataBase(String book_id, String account_id ,String comment) {
        String sql = "select * from books where id=" + book_id;
        ResultSet set = databaseUtil.search(sql);
        int has=0;
        if (set != null) {
            try {
                while(set.next()) {
                    has++;
                    //判断是否数据库中存在此bookid的书籍
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
      //没有相关书籍，则返回信息
        if(has==0)return ConstValue.CommentReturnCode_noBook;

     // comment cannot be shorter than 6
        if (comment.length() < 6) {
            return ConstValue.CommentReturnCode_contentTooShort; 
        }

     // sql插入语句格式
        sql = "insert into comments (belong,speaker,content,time) values("+book_id+","+account_id+",'"+comment+"',now())";// 新加的
        boolean flag = databaseUtil.insert(sql);
       
        //sql 更新语句格式，更新book表中相关书籍的评论数
        sql="update books set comments_num=comments_num+1 where id = "+book_id;
        databaseUtil.update(sql);
        if (flag) {
            System.out.println("insert success");
            String sql_1 = "UPDATE userInfo SET comments_num = comments_num+1 " +" WHERE id = " + account_id;
            databaseUtil.update(sql_1);
            String sql_2 = "UPDATE userInfo SET exp = exp+"+ConstValue.exp_comments +" WHERE id = " + account_id;
            databaseUtil.update(sql_2);
            return ConstValue.CommentReturnCode_success;
        } else {
            System.out.println("insert have error");
            return ConstValue.CommentReturnCode_wrong;
        }
        }
	
	
	
	public static void comment() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int users=0;
                ServerSocket server =null;
                if(databaseUtil == null) {
                    databaseUtil=new DatabaseUtil();
                }
                System.out.println("Server for Comment start");
                try {
                    server = new ServerSocket(ConstValue.serverPortComment);
                } catch (IOException e) {
                	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                    System.out.println("无法开启Comment服务");
                    e.printStackTrace();
                }
                while(true) {
                    Socket socket;
                    try {
                        socket = server.accept();
                        System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                        System.out.println("IP地址:"+socket.getInetAddress());
                        runComment(socket);
                        if((users++)>10000) {
                        	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                            System.out.println("stop server");
                            server.close();
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }
	private static void runComment(final Socket socket) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectInputStream in = null;
                ObjectOutputStream out = null;
                try {
                    in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    Object object = in.readObject();
                    CommentMessage msg = (CommentMessage) object;
                    System.out.println("id:"+msg.getAccount()+" comment:"+msg.getComment());

                    CommentPermission permission = null;
                    if (checkAccountInDataBase(msg.getAccount(), msg.getPassword()) == ConstValue.LoginReturnCode_sucess) {
                    	//如果数据库中存在该账号信息，则将评论插入评论表中
                        permission = new CommentPermission(insertCommentInDataBase(msg.getBook_id(), msg.getAccount(), msg.getComment()));
                    } else {
                    	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                    	System.out.println("评论账号不存在");
                    	permission = new CommentPermission(ConstValue.CommentReturnCode_noAccount);
                    }

                    out=new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    out.writeObject(permission);

                    out.close();
                    in.close();


                }catch(IOException e) {
                	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                    e.printStackTrace();
                }finally {
                }
            }
        }).start();
    }
	
	
	public static void getComments() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int users=0;
                ServerSocket server =null;
                if(databaseUtil == null) {
                    databaseUtil=new DatabaseUtil();
                }

               
                System.out.println("Server for getComments start");
                try {
                    server = new ServerSocket(ConstValue.serverPortgetComments);
                } catch (IOException e) {
                	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                    System.out.println("无法开启getComments服务");
                    e.printStackTrace();
                }
                while(true) {
                    Socket socket;
                    try {
                        socket = server.accept();
                        System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                        System.out.println("IP地址:"+socket.getInetAddress());
                        rungetComments(socket);
                        if((users++)>10000) {
                        	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                            System.out.println("stop server");
                            server.close();
                            break;
                        }
                    } catch (IOException e) {
                    	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }
	private static void rungetComments(final Socket socket) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectInputStream in = null;
                ObjectOutputStream out = null;
                try {
                    in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    Object object = in.readObject();
                    CommentMessage msg = (CommentMessage) object;
                   
                    List<CommentMessage>list=new ArrayList<>();
                 
                    if (checkAccountInDataBase(msg.getAccount(), msg.getPassword()) == ConstValue.LoginReturnCode_sucess) {
                    	String sql = "select * from comments where belong=" + msg.getBook_id();
                		ResultSet set = databaseUtil.search(sql);
                		if (set != null) {
                			try {
                				while(set.next()) {
                					CommentMessage msg1 = new CommentMessage(set.getInt("speaker")+"", set.getString("content"), "",set.getInt("belong")+"",set.getDate("time"));
                					list.add(msg1);
                				}
                				
                			} catch (SQLException e) {  		
                				e.printStackTrace();
                			}
                		}             		
                    } else {
                    	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                    	System.out.println("账号不存在");
                  
                    }
                    CommentList cl=new CommentList(list);
                    out=new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    out.writeObject(cl);
                    out.close();
                    in.close();


                }catch(IOException e) {
                	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                    e.printStackTrace();
                }finally {
                }
            }
        }).start();
    }

	public static void write() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int users=0;
                ServerSocket server =null;
                if(databaseUtil == null) {
                    databaseUtil=new DatabaseUtil();
                }
                System.out.println("Server for write start");
                try {
                    server = new ServerSocket(ConstValue.serverPortWriter);
                } catch (IOException e) {
                	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                    System.out.println("无法开启write服务");
                    e.printStackTrace();
                }
                while(true) {
                    Socket socket;
                    try {
                        socket = server.accept();
                        System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                        System.out.println("IP地址:"+socket.getInetAddress());
                        runWrite(socket);
                        if((users++)>10000) {
                        	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                            System.out.println("stop server");
                            server.close();
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }
	private static void runWrite(final Socket socket) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectInputStream in = null;
                ObjectOutputStream out = null;
                try {
                    in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    Object object = in.readObject();
                    Write w = (Write) object;  
              
                  if(w.id==null||w.id.equals("")) {
                	  String sql;
                	  if(w.book_id==null||w.book_id.equals("")) {
                	
                		  sql = "insert into mywrite (userid,bookname,content,time) values("+w.user_id +",'"+w.book_name+  "','"  +w.content+     "',now())";// 新加的
                	  }else {
                		  sql = "insert into mywrite (userid,bookid,bookname,content,time) values("+w.user_id + ","  +w.book_id+    ",'"    +w.book_name+  "','"  +w.content+     "',now())";// 新加的
                	  }
          
                	
                	  String sql_1 = "update userInfo set exp=exp+"+ConstValue.exp_write+" WHERE id="+w.user_id;
                	  databaseUtil.update(sql_1);
                      System.out.println(sql);
                	  w.id = String.valueOf(databaseUtil.insertGetKey(sql));
                	
                  }else {
                	  if (checkWriteInDataBase(w.id)) {
                      	//如果数据库中存在该条记录，则更新
                    	  String sql="update mywrite set bookname='"+w.book_name+"',content='"+w.content+"',time=now() where id="+w.id;
                    	  System.out.println(sql);
                          databaseUtil.update(sql);
                          String sql_1 = "update userInfo set exp=exp+"+ConstValue.exp_write+" WHERE id="+w.user_id;
                    	  databaseUtil.update(sql_1);
                      }
                	 
                  }
                   

                    out=new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    out.writeObject(w);
                    out.close();
                    in.close();
                }catch(IOException e) {
                	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                    e.printStackTrace();
                }finally {
                }
            }
        }).start();
    }
	public static boolean checkWriteInDataBase(String id) {
		String sql="select * from mywrite where id="+id;
		ResultSet set=databaseUtil.search(sql);
		if(set==null) {
			return false;		//不存在
		}else {
			return true;
		}
		
		
	}
	
	public static void getWrite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int users=0;
                ServerSocket server =null;
                if(databaseUtil == null) {
                    databaseUtil=new DatabaseUtil();
                }
                System.out.println("Server for getwrite start");
                try {
                    server = new ServerSocket(ConstValue.serverPortgetWriters);
                } catch (IOException e) {
                	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                    System.out.println("无法开启get write服务");
                    e.printStackTrace();
                }
                while(true) {
                    Socket socket;
                    try {
                        socket = server.accept();
                        System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                        System.out.println("IP地址:"+socket.getInetAddress());
                        rungetWrite(socket);
                        if((users++)>10000) {
                        	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                            System.out.println("stop server");
                            server.close();
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }
	private static void rungetWrite(final Socket socket) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectInputStream in = null;
                ObjectOutputStream out = null;
                try {
                    in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    Object object = in.readObject();
                    Write w = (Write) object;               
                    String sql="select * from mywrite where userid="+w.user_id;
            		ResultSet set=databaseUtil.search(sql);
            		List<Write>wl=new ArrayList<>();
            		if (set != null) {
            			while(set.next()) {
        					Write w1 = new Write(set.getString("bookid"),set.getString("bookname"),set.getDate("time"),set.getString("content"));
        					w1.id=set.getString("id");
        					w1.user_id=set.getString("userid");
        					wl.add(w1);
        				}
        				
            		}           
            		System.out.println("成功发送:"+wl.size()+"条记录给"+socket.getInetAddress());
                   
            		WriteList wlst = new WriteList(wl);
                    out=new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    out.writeObject(wlst);
                    out.close();
                    in.close();
                }catch(IOException e) {
                	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                	System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
                    e.printStackTrace();
                } catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
                }
            }
        }).start();
    }
	
	public static void hopingbook() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int users = 0;
				ServerSocket server = null;
				if (databaseUtil == null) {
					databaseUtil = new DatabaseUtil();
				}
				System.out.println("Server for  hoping book  start");
				try {
					server = new ServerSocket(ConstValue.serverPorthopingbook);
				} catch (IOException e) {
					System.out.print(new Timestamp(System.currentTimeMillis()).toString() + " ");
					System.out.println("无法开启心愿单服务");
					e.printStackTrace();
				}
				while (true) {
					Socket socket;
					try {
						socket = server.accept();
						System.out.print(new Timestamp(System.currentTimeMillis()).toString() + " ");
						System.out.println("IP地址:" + socket.getInetAddress());
						runHopingbook(socket);
						if ((users++) > 10000) {
							System.out.print(new Timestamp(System.currentTimeMillis()).toString() + " ");
							System.out.println("stop  hoping book server");
							server.close();
							break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}).start();
	}

	private static void runHopingbook(final Socket socket) throws IOException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ObjectInputStream in = null;
				ObjectOutputStream out = null;
				try {
					in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
					Object object = in.readObject();
					HopebookMessage msg = (HopebookMessage) object;
					System.out.print(new Timestamp(System.currentTimeMillis()).toString() + " ");
					int choose = msg.getChoose();// 获取选择
					switch (choose) {
					case 1: {
						List<HopebookMessage> list = addlikebook(msg);// 执行添加操作,返回当前用户的全部心愿
						out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
						out.writeObject(list);
						out.close();
						in.close();
						System.out.println("加入心愿单成功");
					}
						break;
					case 2: {
						List<HopebookMessage> list = deletelikebook(msg);// 执行删除操作，返回当前用户的全部心愿
						out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
						out.writeObject(list);
						out.close();
						in.close();
						System.out.println("更新心愿单成功");
					}
						break;
					case 3: {
						String sql3 = "select * from hopelist where belong=" + msg.getUserID();// 输出心愿单
						List<HopebookMessage> list = new ArrayList<>();
						ResultSet set = databaseUtil.search(sql3);
						if (set != null) {
							try {
								while (set.next()) {
									HopebookMessage msg1 = new HopebookMessage(set.getString("userID"),
											set.getString("bookID"));
									list.add(msg1);
								}

							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
						out.writeObject(list);
						out.close();
						in.close();
						System.out.println("输出心愿单成功");
					}
						break;
					case 4: {// 查看其他用户的心愿单
						String table = "hopelist";
						String sql3 = "select * from " + table;
						List<HopebookMessage> list = new ArrayList<>();
						ResultSet set = databaseUtil.search(sql3);
						if (set != null) {
							try {
								while (set.next()) {
									HopebookMessage msg1 = new HopebookMessage(set.getString("userID"),
											set.getString("bookID"));
									if (!set.getString("userID").equals(msg.getUserID())) {
										list.add(msg1);
									}
								}

							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
						out.writeObject(list);
						out.close();
						in.close();
						System.out.println("查看其他人的心愿单成功");
					}
						break;
					case 5: {
					}
						break;

					}
				} catch (IOException e) {
					System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "  发生异常！");
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "  发生异常！");
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
				}
			}
		}).start();
	}

	public static List<HopebookMessage> addlikebook(HopebookMessage msg) throws SQLException {
		String table1 = "books";
		String bookID = msg.getBookID();
		int likesnumber = 0;
		String sql = "select * from " + table1+" where id="+bookID;
		ResultSet rs = databaseUtil.search(sql);
		while (rs.next()) {
			if (rs.getString(1).equals(bookID)) {
				likesnumber = rs.getInt(7);// 获取书籍中喜欢的个数
			}
		}
		likesnumber++;
		String sql1 = "update " + table1 + " set likes=" + likesnumber + " where id=" + bookID;// 更新书籍表
		databaseUtil.search(sql1);
		String userID = msg.getUserID();
		String table2 = "hopelist";
		String sql2 = "insert into " + table2 + " (userID, bookID) value('" + userID + "','" + bookID + "')";// 添加信息到我的心愿单
		databaseUtil.insert(sql2);
		String sql3 = "select * from hopelist where belong=" + msg.getUserID();
		List<HopebookMessage> list = new ArrayList<>();// 获取所有该用户心愿单书籍
		ResultSet set = databaseUtil.search(sql3);
		if (set != null) {
			try {
				while (set.next()) {
					HopebookMessage msg1 = new HopebookMessage(set.getString("userID"), set.getString("bookID"));
					list.add(msg1);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public static List<HopebookMessage> deletelikebook(HopebookMessage msg) throws SQLException {
		String table1 = "books";
		String bookID = msg.getBookID();
		String likesnumber = null;
		int likenum;
		String sql = "select * from " + table1;
		ResultSet rs = databaseUtil.search(sql);
		while (rs.next()) {
			if (rs.getString(1).equals(bookID)) {
				likesnumber = rs.getString(7);// 获取书籍中喜欢的个数
			}
		}
		likenum = Integer.parseInt(likesnumber);
		likenum--;
		likesnumber = String.valueOf(likenum);
		String sql1 = "update " + table1 + " set likes='" + likesnumber + "' where id='" + bookID + "' ";// 更新books表
		databaseUtil.search(sql1);
		String ID = msg.getID();
		String table2 = "hope";
		String sql2 = "delete from " + table2 + " where id='" + ID + "'";// 从心愿单中删掉对应记录
		databaseUtil.update(sql2);
		String sql3 = "select * from hopelist where belong=" + msg.getID();
		List<HopebookMessage> list = new ArrayList<>();
		ResultSet set = databaseUtil.search(sql3);// 获取所有该用户心愿单书籍
		if (set != null) {
			try {
				while (set.next()) {
					HopebookMessage msg1 = new HopebookMessage(set.getString("userID"), set.getString("bookID"));
					list.add(msg1);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public static void activation() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int users = 0;
				ServerSocket server = null;
				if (databaseUtil == null) {
					databaseUtil = new DatabaseUtil();
				}

				System.out.println("Server for activation start");
				try {
					server = new ServerSocket(ConstValue.serverPortactivation);
				} catch (IOException e) {
					System.out.print(new Timestamp(System.currentTimeMillis()).toString() + " ");
					System.out.println("无法开启激活服务");
					e.printStackTrace();
				}
				while (true) {
					Socket socket;
					try {
						socket = server.accept();
						System.out.print(new Timestamp(System.currentTimeMillis()).toString() + " ");
						System.out.println("IP地址:" + socket.getInetAddress());
						runActivation(socket);
						if ((users++) > 10000) {
							System.out.print(new Timestamp(System.currentTimeMillis()).toString() + " ");
							System.out.println("stop server");
							server.close();
							break;
						}
					} catch (IOException e) {
						System.out.print(new Timestamp(System.currentTimeMillis()).toString() + " ");
						e.printStackTrace();
					}

				}
			}
		}).start();
	}

	private static void runActivation(final Socket socket) throws IOException {
		new Thread(new Runnable() {
			private ActivationMessage msg1;

			@Override
			public void run() {
				ObjectInputStream in = null;
				ObjectOutputStream out = null;
				try {
					in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
					Object object = in.readObject();
					ActivationMessage msg = (ActivationMessage) object;
					System.out.print(new Timestamp(System.currentTimeMillis()).toString() + " ");
					out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
					String table = "activationpermission";
					int flag = checkAccountInDataBase(msg.getActivationUserID(), msg.getActivationUserPassword());// 用户账号密码正确
					int flag1 = 0;
					int flag2 = 0;
					String sql = "select * from " + table+" where activationcode="+msg.getActivationCode();
					ResultSet rs = databaseUtil.search(sql);
					String bookid=null;
					while (rs.next()) {
						if (rs.getString(1).equals(msg.getActivationCode()) && rs.getInt("status") == 0) {
							flag1 = 1;// 判断激活码正确且未使用
							bookid=rs.getString("bookid");
						}
						if (rs.getString(1).equals(msg.getActivationCode()) && rs.getInt("status") == 1) {
							flag2 = 1;// 判断激活码正确且使用
							bookid=rs.getString("bookid");
						}

					}
					String table2 = "books";
					msg.setActivationBookID(bookid);
					msg1 = new ActivationMessage(null, null, null);
					if (flag == 1 && flag1 == 1) {// 用户密码账号以及激活码存在且未激活
						String sql3 = "update " + table + " set status=1 where activationcode="+ msg.getActivationCode();
						//buynumber++;
						//System.out.println(buynumber);
						String sql4 = "update " + table2 + " set buynumber=buynumber+1 where id="+ msg.getActivationBookID();
						String sql5 = "update userInfo set exp=exp+"+ConstValue.exp_jihuobook+" where id="+ msg.getActivationUserID();
						databaseUtil.update(sql5);
						databaseUtil.update(sql3);
						databaseUtil.update(sql4);
						msg1.setActivationCondition(1);
						msg1.setActivationBookID(bookid);
						msg1.setActivationCode(msg.getActivationCode());
						msg1.setActivationUserID(msg.getActivationUserID());
						//msg1.setActivationUserPassword(msg.getActivationUserPassword());
						System.out.println("可以激活");
					} else if (flag == 1 && flag2 == 1) {// 用户密码账号以及激活码存在已激活
						msg1.setActivationCondition(0);
						msg1.setActivationBookID(bookid);
						msg1.setActivationCode(msg.getActivationCode());
						msg1.setActivationUserID(msg.getActivationUserID());
						//msg1.setActivationUserPassword(msg.getActivationUserPassword());
						System.out.println("该激活码已经使用");
					} else if (flag == 0) {// 用户没有登录
						msg1.setActivationCondition(-1);
						msg1.setActivationBookID(bookid);
						System.out.println("用户没有登陆");
					}
					out.writeObject(msg1);
					out.close();
					in.close();

				} catch (IOException e) {
					System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "  发生异常！");
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "  发生异常！");
					e.printStackTrace();
				} catch (SQLException e) {
					System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "  发生异常！");
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	
	public static void user() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int users=0;
				ServerSocket server =null;
				if(databaseUtil == null) {
					databaseUtil=new DatabaseUtil();
				}
				
				System.out.println("Server for Book start");
				try {
					server = new ServerSocket(ConstValue.serverPortUserInfo);
				} catch (IOException e) {
					System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
					System.out.println("无法开启userinfo服务");
					e.printStackTrace();
				}
				while(true) {
					Socket socket;
					try {
						socket = server.accept();
						System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
						System.out.println("IP地址:"+socket.getInetAddress());
						runUser(socket);
						if((users++)>10000) {
							System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
							System.out.println("stop server");
							server.close();
							break;
						}
					} catch (IOException e) {
						System.out.println(new Timestamp(System.currentTimeMillis()).toString()+"  发生异常！");
						e.printStackTrace();
					}
					
				}	
			}
		}).start();
	}
	private static void runUser(final Socket socket)throws IOException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ObjectInputStream in = null;
				ObjectOutputStream out = null;
				try {
					in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
					Object object = in.readObject();
					UserMessage userMessage =  (UserMessage) object;
					System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
					System.out.println("ip地址:"+socket.getInetAddress()+" 正在请求用户基本信息:"+userMessage.getId());
					
					String sql="select * from userInfo where id="+userMessage.getId();
					ResultSet set=databaseUtil.search(sql);
					String oldpwd = "";
					String newName=userMessage.getUsername();
					if(set!=null) {
						try {
							while(set.next()) {
								userMessage.setComments_num(Integer.valueOf(set.getString("comments_num")));
								userMessage.setUsername(set.getString("username"));
								userMessage.setExp(set.getInt("exp"));
								userMessage.setBooks(set.getInt("books"));
								userMessage.setAchieve(set.getInt("achieve"));
								oldpwd=set.getString("password");
								
							}
						} catch (SQLException e) {
							System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
							System.out.println(new Timestamp(System.currentTimeMillis()).toString()+"  发生异常！");
							e.printStackTrace();
							
						}
					}
					
					if(userMessage.type==1) {
						//更新用户信息
						if(userMessage.oldpassword.equals(oldpwd)) {
							String sql_1="update userInfo set password='"+userMessage.getPassword()+"' where id="+userMessage.getId();
							String sql_2="update userInfo set username='"+newName+"' where id="+userMessage.getId();
							System.out.println(sql_1);
							System.out.println(sql_2);
							if(databaseUtil.update(sql_1)==1&&databaseUtil.update(sql_2)==1) {
								userMessage.type=3;
							}else {
								userMessage.type=4;
							}
								
						}else {
							userMessage.type=-1;
						}
						
					}else if(userMessage.type==2){
						
					}
					//获取用户信息
					out=new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
					out.writeObject(userMessage);
				
					out.close();
					in.close();
					
						
					
				
					
					
				}catch(IOException e) {
					System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					System.out.print(new Timestamp(System.currentTimeMillis()).toString()+" ");
					e.printStackTrace();
					
				}finally {
				}
			}
		}).start();
	}
	
	  public User userInfoUpdate(String userid, String oldPassword, String newPassword) {
	        User result = new User();
	        try {
	         
	            String sql;
	            sql = "select * from userInfo where userid=" + userid;
	            ResultSet rs =databaseUtil.search(sql);

	            while (rs.next()) {
	                result.setId(rs.getString("id"));
	                result.setUsername(rs.getString("username"));
	                result.setPassword("");
	                result.setExp(rs.getInt("exp"));
	                result.setReadtime(rs.getInt("readtime"));
	                result.setAchieve(rs.getInt("achieve"));
	                result.setComments_num(rs.getInt("comments_num"));
	                result.setBooks(rs.getInt("books"));
	            }
	            

	            if (!oldPassword.equals(result.getPassword())) {
	                return null;
	            } else if (result.getUsername().equals("")) {
	                return null;
	            }

	            sql = "UPDATE userinfo SET password = " + newPassword + " WHERE id = " + result.getId();
	            if(databaseUtil.update(sql)==1) {
	            	 result.setPassword(newPassword);
	            }else {
	            	 result.setPassword(oldPassword);
	            }

	           

	            return result;
	        } catch (SQLException e) {
	            System.out.println(e);
	            return null;
	        }
	    }
	
	

  
 

        
	
	
}
