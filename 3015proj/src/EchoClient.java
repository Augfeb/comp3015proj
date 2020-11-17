import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class EchoClient {
	Socket clientSocket;

	public EchoClient() throws IOException {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Please input the IP address of Echo Server: ");
		String server = scanner.nextLine();

		System.out.print("Please input the port number of Echo Server: ");
		int port = Integer.parseInt(scanner.nextLine());

		clientSocket = new Socket(server, port);

		DataInputStream in = new DataInputStream(clientSocket.getInputStream());
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

		byte[] buffer = new byte[1024];

		System.out.print("Please input username and password: "); //ask for input username and password
		String str = scanner.nextLine();

		out.writeInt(str.length()); 
		out.write(str.getBytes(), 0, str.length()); //send inputted data to server

		int len = in.readInt();
		in.read(buffer, 0, len);
		String reMsg = (new String(buffer, 0, len)); //receive message (welcome or not) from server
		System.out.println(reMsg);

		if (reMsg.equals("Wrong password...") || reMsg.equals("User not found...")) { //if cannot log in then exit
			System.exit(-1);
		}

		System.out.println();

		while (true) { // loop event until user choose to exit

			System.out.println();
			System.out.print(
					"1. Read file list\n2. Create subdirectories\n3. Upload and download files\n4. Delete files\n5. Delete subdirectories\n6. Change file/target name\n7. Read the file¡¦s detail information\n8. Exit");
			System.out.println();
			System.out.print("\nType number to perform activities: ");
			str = scanner.nextLine();
			if (str.equals("8")) { //if user choose to exit then exit
//				out.writeInt(str.length());
//				out.write(str.getBytes(), 0, str.length());
				doWork(out, str);
				System.out.println("Exit successfully...");
				break;
			} else if (str.equals("1")) { //user want to read file list then provide file list
				doWork(out, str);
				do {
					len = in.readInt();
					in.read(buffer, 0, len);
					System.out.println(new String(buffer, 0, len));
				} while (in.available() > 0);
			} else if (str.equals("2")) { //create new subdirectories
				doWork(out,str);
				
				System.out.println("Enter the subdirectory path: ");
				str = scanner.nextLine();
				doWork(out,str);
				len = in.readInt();
				in.read(buffer, 0, len);
				System.out.println(new String(buffer, 0, len));

			} else if (str.equals("3")) {

			} else if (str.equals("4")) {

			} else if (str.equals("5")) {

			} else if (str.equals("6")) {

			} else if (str.equals("7")) {

			} else {
				System.out.println();
				System.out.println("Incorrect value...");
				System.out.println();
			}
		}

		System.out.println();

		clientSocket.close();
		scanner.close();
	}

	private void doWork(DataOutputStream out2, String str) throws IOException {
		out2.writeInt(str.length());
		out2.write(str.getBytes(), 0, str.length());
	}
	
	private void doWork(DataInputStream in2, DataOutputStream out2, Socket soc, String s) {
		
	}
	public static void main(String[] args) throws IOException {
		new EchoClient();
	}

}