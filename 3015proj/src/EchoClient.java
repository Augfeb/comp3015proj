import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

class Server {
	String ipAddr;
	String hostName;

	public Server(String ip, String name) {

		ipAddr = ip;
		hostName = name;

	}
}

public class EchoClient {
	Socket clientSocket;
	static ArrayList<Server> hostList = new ArrayList<>();

	public EchoClient() {
		System.out.println("Getting response...");
		Thread t2 = new Thread(() -> {
			try {
				String msg = "requesting...";
				DatagramSocket socket = new DatagramSocket(5555);
				DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(),
						InetAddress.getByName("255.255.255.255"), 9998);
				socket.send(packet);

				DatagramPacket receivedPacket = new DatagramPacket(new byte[1024], 1024);
				while (true) {
					socket.receive(receivedPacket);
					String content = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
					String[] parts = content.split(" ");
					String part1 = parts[0];
					String part2 = parts[1];
					Server host = new Server(part1, part2);
					hostList.add(host);
					System.out.println("List of host: ");
					for (Server s : hostList) {
						System.out.println(s.hostName);

					}
				}

			} catch (IOException e) {
				System.err.println(e.getMessage());
			}

		});
		t2.start();

		Thread t = new Thread(() -> {
			Scanner scanner = new Scanner(System.in);
			try {
				String hostIP = "";
				System.out.print("Please input the host name: ");
				String server = scanner.nextLine();
				for (Server s : hostList) {
					if (server.equals(s.hostName)) {
						hostIP = s.ipAddr;
					}
				}

				clientSocket = new Socket(hostIP, 9999);

				DataInputStream in = new DataInputStream(clientSocket.getInputStream());
				DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

				byte[] buffer = new byte[1024];

				System.out.print("Please input username and password: "); // ask for input username and password
				String str = scanner.nextLine();

				out.writeInt(str.length());
				out.write(str.getBytes(), 0, str.length()); // send inputted data to server

				int len = in.readInt();
				in.read(buffer, 0, len);
				String reMsg = (new String(buffer, 0, len)); // receive message (welcome or not) from server
				System.out.println(reMsg);

				if (reMsg.equals("Wrong password...") || reMsg.equals("User not found...")) { // if cannot log in then
																								// exit
					clientSocket.close();
					scanner.close();
					return;
				}

				System.out.println();

				while (true) { // loop event until user choose to exit

					System.out.println();
					System.out.print(
							"1. Read file list\n2. Create subdirectories\n3. Upload and download files\n4. Delete files\n5. Delete subdirectories\n6. Change file/target name\n7. Read the file¡¦s detail information\n8. Exit");
					System.out.println();
					System.out.print("\nType number to perform activities: ");
					str = scanner.nextLine();
					if (str.equals("8")) { // if user choose to exit then exit
						doOut(out, str);
						System.out.println("Exit successfully...");
						break;
					} else if (str.equals("1")) { // user want to read file list then provide file list
						doOut(out, str);
						do {
							len = in.readInt();
							in.read(buffer, 0, len);
							System.out.println(new String(buffer, 0, len));
						} while (in.available() > 0);
					} else if (str.equals("2")) { // create new subdirectories
						doOut(out, str);

						System.out.println("Enter the subdirectory path: ");
						str = scanner.nextLine();
						doOut(out, str);
						len = in.readInt();
						in.read(buffer, 0, len);
						System.out.println(new String(buffer, 0, len));

					} else if (str.equals("3")) {
						doOut(out, str); // send out 3
						System.out.println("Input 1 to download; 2 to upload: ");
						str = scanner.nextLine();
						doOut(out, str); // send out 1 or 2

						if (str.equals("1")) { // choose download
							System.out.println("Enter the file name to download: ");
							String filename = scanner.nextLine().trim();
							doOut(out, filename);// send out pathname
							System.out.println("Enter the folder to store file: ");
							str = scanner.nextLine().trim();

							len = in.readInt();
							in.read(buffer, 0, len);
							String s = new String(buffer, 0, len);

							if (s.equals("Transfering..."))
								serve(in, str);
							else {
								System.out.println("hi" + s);
							}

						} else if (str.equals("2")) {
							System.out.println("Enter the file path to upload: ");
							String filename = scanner.nextLine().trim();
							upload(out, filename);
							System.out.println("Uploaded...");
						}

					} else if (str.equals("4")) {
						doOut(out, str);

						System.out.println("Enter the file path to be delected: ");
						str = scanner.nextLine();
						doOut(out, str);
						len = in.readInt();
						in.read(buffer, 0, len);
						System.out.println(new String(buffer, 0, len));

					} else if (str.equals("5")) {
						doOut(out, str);

						System.out.println("Enter the subdirectory path to be delected: ");
						str = scanner.nextLine();
						doOut(out, str);
						len = in.readInt();
						in.read(buffer, 0, len);
						System.out.println(new String(buffer, 0, len));

					} else if (str.equals("6")) {
						doOut(out, str);
						String s1, s2;

						System.out.println("Enter the file/dir path with original name: ");
						str = scanner.nextLine();
						doOut(out, str);
						System.out.println("Enter the file/dir path with new name: ");
						str = scanner.nextLine();
						doOut(out, str);
						len = in.readInt();
						in.read(buffer, 0, len);
						System.out.println(new String(buffer, 0, len));

					} else if (str.equals("7")) {
						doOut(out, str);
						System.out.println("Enter the file path: ");
						str = scanner.nextLine();
						doOut(out, str);
						do {
							len = in.readInt();
							in.read(buffer, 0, len);
							System.out.println(new String(buffer, 0, len));
						} while (in.available() > 0);
					} else {
						System.out.println();
						System.out.println("Incorrect value...");
						System.out.println();
					}
				}

				System.out.println();
				scanner.close();

			} catch (IOException ex) {
				System.err.println("Connection dropped!");
				System.exit(-1);
			}
		});
		t.start();

	}

	private void serve(DataInputStream in, String path) {
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

	private void upload(DataOutputStream out, String filename) {
		try {

			byte[] buffer = new byte[1024];
			File file = new File(filename);

			if (!file.exists() || file.isDirectory()) {
				System.err.println("File not found!");
				return;
			}

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
		} catch (UnknownHostException u) {
			System.out.println("Could not determine the IP address of the host!");
			return;
		} catch (IOException e) {
			System.out.println("Fail to upload file!");
			return;
		}
	}

	private void doOut(DataOutputStream out2, String str) throws IOException {
		out2.writeInt(str.length());
		out2.write(str.getBytes(), 0, str.length());
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		new EchoClient();
	}

}