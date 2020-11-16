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
	static final String path = "D:\\Download";

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
						out.writeInt(str.length());
						out.write(str.getBytes(), 0, str.length());
					} else {
						System.out.println("wrong pw case");
						str = ("Wrong password...");
						out.writeInt(str.length());
						out.write(str.getBytes(), 0, str.length());

					}
				} else {
					System.out.println("wrong username case");
					str = ("User not found...");
					out.writeInt(str.length());
					out.write(str.getBytes(), 0, str.length());

				}

			}

			while (true) {

				len = in.readInt();
				in.read(buffer, 0, len);

				String number = new String(buffer, 0, len);
				String info;
				System.out.println(number);
				if (number.equals("8")) {
					break;
				}
				if (number.equals("1")) {
					info = dir(path);
					System.out.println(info);
					out.writeInt(info.length());
					out.write(info.getBytes(), 0, info.length());
				}
			}

			clientSocket.close();
		}
	}

	private String dir(String pathName) {
		File dir = new File(pathName);
		String str1 = null, str2 = null;
		if (!dir.exists()) {
			System.out.println("File not found");
			return null;
		}
		File[] files = dir.listFiles();
		if (files.length == 0) {
			System.out.println("Empty");
			return null;
		}
		for (File f : files) {
			if (f.isFile()) {
				str1 = String.format("%s %10d %s\n", new Date(f.lastModified()), f.length(), f.getName());
			
			} else {
				str2 = String.format("%s %10s %s\n", new Date(f.lastModified()), "<DIR>", f.getName());
				
			}
		}
		return str1 + " "+ str2;
	}

	public static void main(String[] args) throws IOException {
		User pcA = new User("pcA", "123");
		userList.add(pcA);
		new EchoServer(9999);
	}

}