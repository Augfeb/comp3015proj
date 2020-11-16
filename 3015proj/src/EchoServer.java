import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class EchoServer {
	ServerSocket srvSocket;
	static ArrayList<User> userList = new ArrayList<User>();

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

		
			while(true){

				len = in.readInt();
				in.read(buffer, 0, len);

				String number = new String(buffer, 0, len);
				System.out.println(number);
				if (number.equals("8")) {
					break;
				}
			}

			clientSocket.close();
		}
	}

	public static void main(String[] args) throws IOException {
		User pcA = new User("pcA", "123");
		userList.add(pcA);
		new EchoServer(9999);
	}

}