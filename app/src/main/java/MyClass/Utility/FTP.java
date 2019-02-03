package MyClass.Utility;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import cloudbook.gaoch.com.MainActivity;

public class FTP {
	/**
	 * 服务器名.
	 */
	private String hostName;

	/**
	 * 端口号
	 */
	private int serverPort;

	/**
	 * 用户名.
	 */
	private String userName;

	/**
	 * 密码.
	 */
	private String password;

	/**
	 * FTP连接.
	 */
	private FTPClient ftpClient;

	private static FTP ftp;

	private static final String TAG = "FTP";

	public static final String FTP_CONNECT_SUCCESSS = "ftp连接成功";
	public static final String FTP_CONNECT_FAIL = "ftp连接失败";
	public static final String FTP_DISCONNECT_SUCCESS = "ftp断开连接";
	public static final String FTP_FILE_NOTEXISTS = "ftp上文件不存在";

	public static final String FTP_UPLOAD_SUCCESS = "ftp文件上传成功";
	public static final String FTP_UPLOAD_FAIL = "ftp文件上传失败";
	public static final String FTP_UPLOAD_LOADING = "ftp文件正在上传";

	public static final String FTP_DOWN_LOADING = "ftp文件正在下载";
	public static final String FTP_DOWN_SUCCESS = "ftp文件下载成功";
	public static final String FTP_DOWN_FAIL = "ftp文件下载失败";

	public static final String FTP_DELETEFILE_SUCCESS = "ftp文件删除成功";
	public static final String FTP_DELETEFILE_FAIL = "ftp文件删除失败";
	public static String userAccount;

	public static FTP getInstance() {
		if (ftp == null) {
			ftp = new FTP();
		}
		return ftp;
	}

	public FTP() {
		this.hostName = "149.28.15.40";
		this.serverPort = 21;
		this.userName = "userpic";
		this.password = "1195593460";
		this.ftpClient = new FTPClient();
	}

	public void setIp(String ip){
		this.hostName = ip;
	}
	// -------------------------------------------------------文件上传方法------------------------------------------------

	/**
	 * 上传单个文件.
	 *
	 *            本地文件
	 * @param remotePath
	 *            FTP目录
	 * @param listener
	 *            监听器
	 * @throws IOException
	 */
	public void uploadSingleFile(File singleFile, String remotePath, String Useraccount,UploadProgressListener listener) throws IOException {
		this.userAccount=Useraccount;
		// 上传之前初始化
		this.uploadBeforeOperate(remotePath, listener);

		boolean flag;
		flag = uploadingSingle(singleFile, userAccount,listener);
		if (flag) {
			listener.onUploadProgress(FTP_UPLOAD_SUCCESS, 0, singleFile);
		} else {
			listener.onUploadProgress(FTP_UPLOAD_FAIL, 0, singleFile);
		}

		// 上传完成之后关闭连接
		this.uploadAfterOperate(listener);
	}

	/**
	 * 上传多个文件.
	 *
	 *            本地文件
	 * @param remotePath
	 *            FTP目录
	 * @param listener
	 *            监听器
	 * @throws IOException
	 */
	public void uploadMultiFile(LinkedList<File> fileList, String remotePath, UploadProgressListener listener) throws IOException {

		// 上传之前初始化
		this.uploadBeforeOperate(remotePath, listener);

		boolean flag;

		for (File singleFile : fileList) {
			flag = uploadingSingle(singleFile,userAccount ,listener);
			if (flag) {
				listener.onUploadProgress(FTP_UPLOAD_SUCCESS, 0, singleFile);
			} else {
				listener.onUploadProgress(FTP_UPLOAD_FAIL, 0, singleFile);
			}
		}

		// 上传完成之后关闭连接
		this.uploadAfterOperate(listener);
	}

	/**
	 * 上传单个文件.
	 * 
	 * @param localFile
	 *            本地文件
	 * @return true上传成功, false上传失败
	 * @throws IOException
	 */
	private boolean uploadingSingle(File localFile, String Useraccount,UploadProgressListener listener) throws IOException {
		boolean flag = true;
		// 不带进度的方式
		// // 创建输入流
		// InputStream inputStream = new FileInputStream(localFile);
		// // 上传单个文件
		// flag = ftpClient.storeFile(localFile.getName(), inputStream);
		// // 关闭文件流
		// inputStream.close();

		// 带有进度的方式
		BufferedInputStream buffIn = new BufferedInputStream(new FileInputStream(localFile));
		ProgressInputStream progressInput = new  ProgressInputStream(buffIn, listener, localFile);
		flag = ftpClient.storeFile(Useraccount+".png", progressInput);
		buffIn.close();

		return flag;
	}

	/**
	 * 上传文件之前初始化相关参数
	 * 
	 * @param remotePath
	 *            FTP目录
	 * @param listener
	 *            监听器
	 * @throws IOException
	 */
	private void uploadBeforeOperate(String remotePath, UploadProgressListener listener) throws IOException {

		// 打开FTP服务
		try {
			this.openConnect();
			listener.onUploadProgress(FTP_CONNECT_SUCCESSS, 0, null);
		} catch (IOException e1) {
			e1.printStackTrace();
			listener.onUploadProgress(FTP_CONNECT_FAIL, 0, null);
			return;
		}

		// 设置模式
		ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
		// FTP下创建文件夹
		ftpClient.makeDirectory(remotePath);
		// 改变FTP目录
		ftpClient.changeWorkingDirectory(remotePath);
		// 上传单个文件

	}

	/**
	 * 上传完成之后关闭连接
	 * 
	 * @param listener
	 * @throws IOException
	 */
	private void uploadAfterOperate(UploadProgressListener listener) throws IOException {
		this.closeConnect();
		listener.onUploadProgress(FTP_DISCONNECT_SUCCESS, 0, null);
	}

	// -------------------------------------------------------文件下载方法------------------------------------------------

	/**
	 * 下载单个文件，可实现断点下载.
	 * 
	 * @param serverPath
	 *            Ftp目录及文件路径
	 * @param localPath
	 *            本地目录
	 * @param fileName
	 *            下载之后的文件名称
	 * @param listener
	 *            监听器
	 * @throws IOException
	 */
	public void downloadSingleFile(String serverPath, String localPath, String fileName, DownLoadProgressListener listener) throws Exception {

		// 打开FTP服务
		try {
			this.openConnect();
			listener.onDownLoadProgress(FTP_CONNECT_SUCCESSS, 0, null);
		} catch (IOException e1) {
			e1.printStackTrace();
			listener.onDownLoadProgress(FTP_CONNECT_FAIL, 0, null);
			return;
		}

		// 先判断服务器文件是否存在
		// ftpClient.enterLocalPassiveMode();
		FTPFile[] files = ftpClient.listFiles(serverPath);
		if (files.length == 0) {
			listener.onDownLoadProgress(FTP_FILE_NOTEXISTS, 0, null);
			return;
		}

		// 创建本地文件夹
		File mkFile = new File(localPath);
		if (!mkFile.exists()) {
			mkFile.mkdirs();
		}

		localPath = localPath + fileName;
		// 接着判断下载的文件是否能断点下载
		boolean down_success = false;
		long serverSize = files[0].getSize(); // 获取远程文件的长度
		File localFile = new File(localPath);
		long localSize = 0;
		if (localFile.exists()) {
			localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
			if (localSize == serverSize) {
				listener.onDownLoadProgress(FTP_DOWN_LOADING, 100, null);
				down_success = true;
			} else if (localSize >= serverSize) {
				File file = new File(localPath);
				file.delete();
			}
		}
		if (!down_success) {
			// 进度
			long step = serverSize / 100;
			long process = 0;
			long currentSize = localSize;
			// 开始准备下载文件
			OutputStream out = new FileOutputStream(localFile, true);
			ftpClient.setRestartOffset(localSize);
			InputStream input = ftpClient.retrieveFileStream(serverPath);
			byte[] b = new byte[1024];
			int length = 0;
			while ((length = input.read(b)) != -1) {
				out.write(b, 0, length);
				currentSize = currentSize + length;
				if (currentSize / step != process) {
					process = currentSize / step;
					if (process % 1 == 0) { // 每隔%1的进度返回一次
						listener.onDownLoadProgress(FTP_DOWN_LOADING, process, null);
					}
				}
			}
			out.flush();
			out.close();
			input.close();
		}

		// 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
		if (ftpClient.completePendingCommand()) {
			listener.onDownLoadProgress(FTP_DOWN_SUCCESS, 100, new File(localPath));
		} else {
			if (!down_success) {
				listener.onDownLoadProgress(FTP_DOWN_FAIL, 0, null);
			} else {
				listener.onDownLoadProgress(FTP_DOWN_SUCCESS, 0, new File(localPath));
			}

		}

		// 下载完成之后关闭连接
		this.closeConnect();
		listener.onDownLoadProgress(FTP_DISCONNECT_SUCCESS, 0, null);

		return;
	}

	// -------------------------------------------------------文件删除方法------------------------------------------------

	/**
	 * 删除Ftp下的文件.
	 * 
	 * @param serverPath
	 *            Ftp目录及文件路径
	 * @param listener
	 *            监听器
	 * @throws IOException
	 */
	public void deleteSingleFile(String serverPath, DeleteFileProgressListener listener) throws Exception {

		// 打开FTP服务
		try {
			this.openConnect();
			listener.onDeleteProgress(FTP_CONNECT_SUCCESSS);
		} catch (IOException e1) {
			e1.printStackTrace();
			listener.onDeleteProgress(FTP_CONNECT_FAIL);
			return;
		}

		// 先判断服务器文件是否存在
		FTPFile[] files = ftpClient.listFiles(serverPath);
		if (files.length == 0) {
			listener.onDeleteProgress(FTP_FILE_NOTEXISTS);
			return;
		}

		// 进行删除操作
		boolean flag = true;
		flag = ftpClient.deleteFile(serverPath);
		// 删除完成之后关闭连接
		this.closeConnect();
		
		if (flag) {
			listener.onDeleteProgress(FTP_DELETEFILE_SUCCESS);
		} else {
			listener.onDeleteProgress(FTP_DELETEFILE_FAIL);
		}
		listener.onDeleteProgress(FTP_DISCONNECT_SUCCESS);
		return;
	}

	// -------------------------------------------------------打开关闭连接------------------------------------------------

	/**
	 * 打开FTP服务.
	 * 
	 * @throws IOException
	 */
	public void openConnect() throws IOException {
		// 中文转码
		ftpClient.setControlEncoding("UTF-8");
		int reply; // 服务器响应值

//		ftpClient.setDataTimeout(20*1000);
//		ftpClient.setConnectTimeout(20*1000);
//		ftpClient.setDefaultTimeout(20*1000);
		ftpClient.setRemoteVerificationEnabled(false);

		ftpClient.enterLocalPassiveMode(); // 被动mos
		// ftpClient.enterLocalActiveMode(); //主动

		// 连接至服务器
		ftpClient.connect(hostName, serverPort);
		// 获取响应值
		reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			// 断开连接
			ftpClient.disconnect();
			throw new IOException("connect fail: " + reply);
		}
		// 登录到服务器
		ftpClient.login(userName, password);
		// 获取响应值
		reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			// 断开连接
			ftpClient.disconnect();
			throw new IOException("connect fail: " + reply);
		} else {
			// 获取登录信息
			FTPClientConfig config = new FTPClientConfig(ftpClient.getSystemType().split(" ")[0]);
			config.setServerLanguageCode("zh");
			ftpClient.configure(config);

			// 使用被动模式设为默认
			ftpClient.enterLocalPassiveMode();
			// ftpClient.enterLocalActiveMode(); //主动
			// 二进制文件支持
			ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
		}
	}

	/**
	 * 关闭FTP服务.
	 * 
	 * @throws IOException
	 */
	public void closeConnect() throws IOException {
		if (ftpClient != null) {
			// 退出FTP
			ftpClient.logout();
			// 断开连接
			ftpClient.disconnect();
		}
	}

	// ---------------------------------------------------上传、下载、删除监听---------------------------------------------

	/*
	 * 上传进度监听
	 */
	public interface UploadProgressListener {
		public void onUploadProgress(String currentStep, long uploadSize, File file);
	}

	/*
	 * 下载进度监听
	 */
	public interface DownLoadProgressListener {
		public void onDownLoadProgress(String currentStep, long downProcess, File file);
	}

	/*
	 * 文件删除监听
	 */
	public interface DeleteFileProgressListener {
		public void onDeleteProgress(String currentStep);
	}

	/*
	 * 文件遍历监听
	 */
	public interface FileCatalogListener {
		public void onCatalogProgress();
	}
	
	/**
	 * 列出服务器上文件和目录
	 *
	 * @param regStr
	 *            --匹配的正则表达式
	 */
	public void listRemoteFiles(String regStr , FileCatalogListener listener ) {
		try {
			openConnect();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			String files[] = ftpClient.listNames(regStr);
			if (files == null || files.length == 0){
				System.out.println("没有任何文件!");
				//SendFileFragment.File = null;
			}else {
//				for (int i = 0; i < files.length; i++) {
//					System.out.println(files[i]);
//				}				
				//SendFileFragment.File = files;
			}
			listener.onCatalogProgress();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 列出Ftp服务器上的所有文件和目录
	 */
	public void listRemoteAllFiles() {
		try {
			openConnect();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			String[] names = ftpClient.listNames();
			for (int i = 0; i < names.length; i++) {
				System.out.println(names[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * 删除一个文件
     */
    public boolean deleteFile(String filename) {
            boolean flag = true;
            try {
            	openConnect();
                    flag = ftpClient.deleteFile(filename);
                    if (flag) {
                            System.out.println("删除文件成功！");
                    } else {
                            System.out.println("删除文件失败！");
                    }
            } catch (IOException ioe) {
                    ioe.printStackTrace();
            }
            return flag;
    } 
    
}
