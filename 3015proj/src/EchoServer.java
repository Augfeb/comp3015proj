import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class EchoServer {
	ServerSocket srvSocket;
	static ArrayList<User> userList = new ArrayList<User>();
	ArrayList<Socket> list = new ArrayList<Socket>();
	static final String path = "D:";
	

	private void doOut(DataOutputStream out2, String str) {
		try {
			out2.writeInt(str.length());
			out2.write(str.getBytes(), 0, str.length());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public EchoServer(int port) {
		try {
			User pcA = new User("pcA", "123");
			User pcB = new User("pcB", "456");
			User pcC = new User("pcC", "789");
			userList.add(pcA);
			userList.add(pcB);
			userList.add(pcC);
			srvSocket = new ServerSocket(port);

			byte[] buffer = new byte[1024];

			while (true) {
				new Thread(() -> {
					UDPreply();
				}).start();

				Socket clientSocket = srvSocket.accept();
				System.out.println("Listening at TCP port " + port + "...");
				synchronized (userList) {
					list.add(clientSocket);
					System.out.printf("Total %d clients are connected.\n", list.size());
				}

				Thread t = new Thread(() -> {
					action(clientSocket, buffer);
					synchronized (list) {
						list.remove(clientSocket);
					}
				});
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void UDPreply() {
		try {
			
			DatagramSocket socket = new DatagramSocket(9998);
			DatagramPacket receivedPacket = new DatagramPacket(new byte[1024], 1024);

			System.out.println("Listening at UDP port 9998...");

			String myIp = InetAddress.getLocalHost().getHostAddress();
			String reply = myIp + " " + new String(InetAddress.getLocalHost().getHostName());

			socket.receive(receivedPacket);
			String content = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

			if (content.equals("requesting...")) {
				InetAddress srcAddr = receivedPacket.getAddress();
				int srcPort = receivedPacket.getPort();
				DatagramPacket p = new DatagramPacket(reply.getBytes(), reply.length(), srcAddr, srcPort);
				socket.send(p);
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void action(Socket clientSocket, byte[] buffer) {
		try {
			boolean approved = false;
			boolean userFound = false;
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

			int len = in.readInt();
			in.read(buffer, 0, len);

			String str = new String(buffer, 0, len);

			String[] parts = str.split(" ");
			String part1 = parts[0];
			String part2 = parts[1];

			for (int i = 0; i < userList.size(); i++) { // loop through userlist pcA pcB
				if (userList.get(i).username.equals(part1)) { 

					userFound = true;
					if (userList.get(i).password.equals(part2)) {

						str = "Loggin you in...";
						doOut(out, str);
						approved = true;

						break;
					} else {

						str = ("Wrong password...");
						doOut(out, str);
						approved = false;
						break;
					}
				}
			}
			if (!userFound) {
				str = ("User not found...");
				doOut(out, str);
				approved = false;

			}

			while (approved) {

				len = in.readInt();
				in.read(buffer, 0, len);

				String number = new String(buffer, 0, len);
				ArrayList<String> info;

				System.out.println();
				if (number.equals("8")) {
					break;
				} else if (number.equals("1")) {

					if (dir(path) != null) {
						info = dir(path);
						for (String s : info) {
							doOut(out, s);
						}
					} else {
						str = "No file/subdirectory in this root directory";
						doOut(out, str);
					}
				} else if (number.equals("2")) {
					len = in.readInt();
					in.read(buffer, 0, len);
					str = new String(buffer, 0, len);
					String pathName = path + "\\" + str;
					str = md(pathName);
					doOut(out, str);

				} else if (number.equals("3")) {
					len = in.readInt();
					in.read(buffer, 0, len);
					str = new String(buffer, 0, len); // store 1 or 2

					if (str.equals("1")) {
						len = in.readInt();
						in.read(buffer, 0, len);
						str = new String(buffer, 0, len); // store filename

						String pathname = path + "\\" + str;

						transfer(out, pathname);
					} else {
						serve(in);
					}

				} else if (number.equals("4")) {
					len = in.readInt();
					in.read(buffer, 0, len);
					str = new String(buffer, 0, len);
					String pathName = path + "\\" + str;
					str = del(pathName);
					doOut(out, str);

				} else if (number.equals("5")) {
					len = in.readInt();
					in.read(buffer, 0, len);
					str = new String(buffer, 0, len);
					String pathName = path + "\\" + str;
					str = rd(pathName);
					doOut(out, str);

				} else if (number.equals("6")) {
					String s1, s2;
					len = in.readInt();
					in.read(buffer, 0, len);
					s1 = path + "\\" + new String(buffer, 0, len);

					len = in.readInt();
					in.read(buffer, 0, len);
					s2 = path + "\\" + new String(buffer, 0, len);

					str = rename(s1, s2);
					doOut(out, str);

				} else if (number.equals("7")) {
					len = in.readInt();
					in.read(buffer, 0, len);
					String filename = path + "\\" + new String(buffer, 0, len);
					String[] ary = getInfo(filename);

					for (String s : ary) {
						doOut(out, s);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
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
		}
		return strList;
	}

	private String md(String pathname) {
		String s;
		File dir = new File(pathname);
		if (dir.exists()) {
			s = ("File/Directory exists");
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
			s = ("To delete a directory, you should use RD command.");
		return s;
	}

	private String rd(String pathname) {
		File dir = new File(pathname);
		String s;
		if (!dir.exists()) {
			s = ("File not found");
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

	private String[] getInfo(String filename) {

		File file = new File(filename);
		String[] strAry = null;
		if (!file.exists()) {
			strAry = new String[1];
			strAry[0] = "File not found...";
			return strAry;
		}
		try {

			if (file.isFile()) {
				strAry = new String[16];
				strAry[0] = ("name : " + file.getName());
				strAry[1] = ("size (bytes) : " + file.length());
				strAry[2] = ("absolute path? : " + file.isAbsolute());
				strAry[3] = ("exists? : " + file.exists());
				strAry[4] = ("hidden? : " + file.isHidden());
				strAry[5] = ("dir? : " + file.isDirectory());
				strAry[6] = ("file? : " + file.isFile());
				strAry[7] = ("modified (timestamp) : " + file.lastModified());
				strAry[8] = ("readable? : " + file.canRead());
				strAry[9] = ("writable? : " + file.canWrite());
				strAry[10] = ("executable? : " + file.canExecute());
				strAry[11] = ("parent : " + file.getParent());
				strAry[12] = ("absolute file : " + file.getAbsoluteFile());
				strAry[13] = ("absolute path : " + file.getAbsolutePath());
				strAry[14] = ("canonical file : " + file.getCanonicalFile());
				strAry[15] = ("canonical path : " + file.getCanonicalPath());
			} else {
				strAry = new String[1];
				strAry[0] = "Not a file...";
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return strAry;
	}

	private void transfer(DataOutputStream out, String filename) {

		try {
			byte[] buffer = new byte[1024];
			File file = new File(filename);

			String s = ("Transfering...");
			doOut(out, s);

			FileInputStream in = new FileInputStream(file);
			out.writeInt(file.getName().length());
			out.write(file.getName().getBytes());

			long size = file.length();
			out.writeLong(size);

			while (size > 0) {
				int len = in.read(buffer, 0, buffer.length);
				out.write(buffer, 0, len);
				size -= len;
			}

			in.close();
		} catch (IOException e) {
			System.out.println("Fail to transfer file...");

		}

	}

	private void serve(DataInputStream in) {
		byte[] buffer = new byte[1024];
		try {
			int nameLen = in.readInt();
			in.read(buffer, 0, nameLen);
			String name = path + "\\" + new String(buffer, 0, nameLen);

			System.out.print("Downloading file %s " + name);

			long size = in.readLong();
			System.out.printf("(%d)", size);

			File file = new File(name);
			FileOutputStream out = new FileOutputStream(file);

			while (size > 0) {
				int len = in.read(buffer, 0, buffer.length);
				out.write(buffer, 0, len);
				size -= len;
			}
			System.out.println("\nDownload completed.");

			out.close();
		} catch (IOException e) {
			System.err.println("unable to download file.");
		}
	}

	public static void main(String[] args) {

		new EchoServer(9999);
	}
}