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
//						out.writeInt(str.length());
//						out.write(str.getBytes(), 0, str.length());
						doOut(out, str);
					} else {
						System.out.println("wrong pw case");
						str = ("Wrong password...");
						doOut(out, str);
						
					}
				} else {
					System.out.println("wrong username case");
					str = ("User not found...");
//					out.writeInt(str.length());
//					out.write(str.getBytes(), 0, str.length());
					doOut(out, str);
				}
			}

			while (true) {

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


	public static void main(String[] args) throws IOException {
		User pcA = new User("pcA", "123");
		userList.add(pcA);
		new EchoServer(9999);
	}

}