import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class EchoServer {
	ServerSocket srvSocket;
	static ArrayList<User> userList = new ArrayList<User>();
	static final String path = "D:\\proj";

	private void doOut(DataOutputStream out2, String str) throws IOException {
		out2.writeInt(str.length());
		out2.write(str.getBytes(), 0, str.length());
	}
	
	private void doIn(DataOutputStream in2, String str) throws IOException {

	}
	
	public EchoServer(int port) throws IOException {
		srvSocket = new ServerSocket(port);

		byte[] buffer = new byte[1024];
		boolean approved = false;
		while (true) {
			System.out.printf("Listening at port %d...\n", port);

			Socket clientSocket = srvSocket.accept();

			System.out.printf("Established a connection to host %s:%d\n\n", clientSocket.getInetAddress(),
					clientSocket.getPort());

			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

			int len = in.readInt();
			in.read(buffer, 0, len);

			String str = new String(buffer, 0, len);

			String[] parts = str.split(" ");
			String part1 = parts[0];
			String part2 = parts[1];

			for (int i = 0; i < userList.size(); i++) {
				if (userList.get(i).username.equals(part1)) {
					System.out.println("gd username case");
					if (userList.get(i).password.equals(part2)) {
						System.out.println("gd pw case");
						str = "Loggin you in...";
						doOut(out, str);
						approved = true;
					} else {
						System.out.println("wrong pw case");
						str = ("Wrong password...");
						doOut(out, str);
						approved = false;
					}
				} else {
					System.out.println("wrong username case");
					str = ("User not found...");
					doOut(out, str);
					approved = false;
				}
			}

			while (approved) {

				len = in.readInt();
				in.read(buffer, 0, len);

				String number = new String(buffer, 0, len);
				ArrayList<String> info;
				System.out.println(number);
				System.out.println();
				if (number.equals("8")) {
					break;
				} else if (number.equals("1")) {

					if (dir(path) != null) {
						info = dir(path);
						for (String s : info) {
							doOut(out,s);
						}
					} else {
						str = "No file/subdirectory in this root directory";
						doOut(out, str);
					}
				} else if (number.equals("2")) {
					len = in.readInt();
					in.read(buffer, 0, len);
					str = new String(buffer, 0, len);
					String pathName = path+str;					
					str = md(pathName);
					doOut(out, str);
				}else if(number.equals("3")) {
					
				}else if(number.equals("4")) {
					len = in.readInt();
					in.read(buffer, 0, len);
					str = new String(buffer, 0, len);
					String pathName = path+str;					
					str = del(pathName);
					doOut(out, str);
				}else if(number.equals("5")) {
					len = in.readInt();
					in.read(buffer, 0, len);
					str = new String(buffer, 0, len);
					String pathName = path+str;					
					str = rd(pathName);
					doOut(out, str);
				}else if(number.equals("6")) {
					String s1, s2;
					len = in.readInt();
					in.read(buffer, 0, len);
					s1 = path + new String(buffer, 0, len);
								
					
					len = in.readInt();
					in.read(buffer, 0, len);
					s2 = path + new String(buffer, 0, len);
					
					str = rename(s1,s2);
					doOut(out, str);
				}else if(number.equals("7")) {
					
				}
			}

			clientSocket.close();
		}
	}

	private ArrayList<String> dir(String pathName) {
		File dir = new File(pathName);
		String str1, str2;

		if (!dir.exists()) {
			System.out.println("File not found");
			return null;
		}
		File[] files = dir.listFiles();
		if (files.length == 0) {
			System.out.println("Empty");
			return null;
		}
		ArrayList<String> strList = new ArrayList<String>(files.length);

		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				str1 = String.format("%s %10d %s\n", new Date(files[i].lastModified()), files[i].length(),
						files[i].getName());
				strList.add(str1);
			} else {
				str2 = String.format("%s %10s %s\n", new Date(files[i].lastModified()), "<DIR>", files[i].getName());
				strList.add(str2);
			}
			System.out.println(strList.get(i));
		}
		return strList;
	}
	
	private String md(String pathname) {
		String s;
		File dir = new File(pathname);
		if (dir.exists()) {
			 s =("File/Directory exists");
			 return s;
		}
		dir.mkdirs();
		s = ("File/Directory created");
		return s;
	}
	
	private String del(String path) {
		String s;
		File dir = new File(path);
		if (!dir.exists()) {
			s = ("File not found");
			return s;
		}
		if (dir.isFile()) {
			dir.delete();
			s = ("Deleted");
		} else
			s =("To delete a directory, you should use RD command.");
		return s;
	}
	
	private String rd(String pathname) {
		File dir = new File(pathname);
		String s;
		if (!dir.exists()) {
			s =("File not found");
			return s;
		}
		if (dir.isDirectory()) {
			String[] list = dir.list();
			if (list == null || list.length == 0) {
				dir.delete();
				s = ("Deleted");
			} else {
				s = ("The directory " + dir + " is not empty!");
			}
		} else
			s = ("To delete a file, you should use DEL command.");
		return s;
	}
	
	private String rename(String ori, String newName) {
		String s;
		File oriFile = new File(ori);
		File newFile = new File(newName);
		
		 
		if (oriFile.renameTo(newFile)) {
		    s = ("File renamed successfully");
		} else {
		    s = ("Failed to rename");
		}
		System.out.println(s);
		return s;
	}


	public static void main(String[] args) throws IOException {
		User pcA = new User("pcA", "123");
		userList.add(pcA);
		new EchoServer(9999);
	}

}