import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ServerProtocol implements Runnable {
	
	private Socket clientSocket;

    public ServerProtocol(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    
	//question Method
	public static String[][] getQnA() {
        String[][] qna = new String[4][2];

        qna[0][0] = "LLM은 뭐의 약자일까요?\n";
        qna[0][1] = "LargeLanguageModel";

        qna[1][0] = "JVM은 무엇을 의미할까요?\n";
        qna[1][1] = "JavaVirtualMachine";

        qna[2][0] = "API는 뭐의 약자일까요?\n";
        qna[2][1] = "ApplicationProgrammingInterface";

        qna[3][0] = "SQL은 뭐의 약자일까요?\n";
        qna[3][1] = "StructuredQueryLanguage";

        return qna;
    }
	
	@Override
    public void run() {
		// TODO Auto-generated method stub
		BufferedReader in = null;
		BufferedWriter out = null;
//		ServerSocket listener = null;
//		Socket socket = null;

//			listener = new ServerSocket(9999);
//			System.out.println("Waiting to connect.....");
//			socket = listener.accept();
//			
//			
			String[][] qnaArray = getQnA();
			int cnt = 4;
			int corr = 0;
			int flag = 0;
			System.out.println("Client is connected.");
			
			try {
				in = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));
				out = new BufferedWriter(
						new OutputStreamWriter(clientSocket.getOutputStream()));
			} catch (IOException e) {
				System.out.println("소켓 연결 중에 오류가 발생했습니다.");
			}
			
			flag = 0;
			try {
				while(true) {
						String[] inputMessage = in.readLine().split("/");
						if(inputMessage[0].equalsIgnoreCase("qgp")) {
							switch(inputMessage[1]) {
							case "0":
								out.write("qgp/0/" + cnt + "\n");
								out.flush();
								break;
							case "1":
								out.write("qgp/1/" + qnaArray[Integer.parseInt(inputMessage[2])][0]);
								out.flush();
								break;
							case "2":
								if(inputMessage[3].replaceAll("\\s", "").equalsIgnoreCase(qnaArray[Integer.parseInt(inputMessage[2])][1])) {
									
									//correct case
									out.write("qgp/2/" + "correct\n");
									out.flush();
									corr++;
								}else {
									
									//incorrect case
									out.write("qgp/2/" + "incorrect\n");
									out.flush();
								}
								
								break;
							case "3":
								out.write("qgp/3/" + corr + "\n");
								out.flush();
								flag = 1;
								break;
							default:
								System.out.println("protocol 해석 중에 오류가 발생했습니다.");
								break;
							}
							if (flag==1) {

								break;
							}
							
						}else {
							System.out.println("정상적인 프로토콜이 아닙니다.");
							break;
						}
				}
			}catch (IOException e) {
				System.out.println("protocol을 불러오는 중 오류가 발생했습니다.");
			}finally {
				try {
					if(clientSocket != null)
						clientSocket.close();
				} catch (IOException e) {
					System.out.println("Error was occured During chatting with client.");
				}
		}
	}
	
	public static void main(String[] args) throws IOException {
		//open the server socket and waiting for connecting client
        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("Server is listening on port 9999...");

        //execute client connection using ExecutorService
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // receive client connection through while
        while (true) {
            Socket clientSocket = serverSocket.accept();  // waiting for client connection
            System.out.println("New client connected");

            //Submit to threadPool after generate Server object that excute client connection
            executorService.submit(new ServerProtocol(clientSocket));
        }
    }
}
