import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ThreadServer implements Runnable {
	
	private Socket clientSocket;

    public ThreadServer(Socket clientSocket) {
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
		try {
//			listener = new ServerSocket(9999);
//			System.out.println("Waiting to connect.....");
//			socket = listener.accept();
//			
//			
			String[][] qnaArray = getQnA();
			int cnt = 4;
			int corr = 0;
			System.out.println("Client is connected.");
			in = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			out = new BufferedWriter(
					new OutputStreamWriter(clientSocket.getOutputStream()));
			
			//transmit the number of questions
			out.write(cnt + "\n");
			out.flush();

			
			//all QnA repeat
			for (String[] qa : qnaArray) {
				//transmit question
				out.write(qa[0]);
				out.flush();
				
				//wait result
				String inputMessage = in.readLine();
				
				//compare answer and result
				if(inputMessage.replaceAll("\\s", "").equalsIgnoreCase(qa[1])) {
					
					//correct case
					out.write("correct\n");
					out.flush();
					corr++;
				}else {
					
					//incorrect case
					out.write("incorrect\n");
					out.flush();
				}
			}
			
			//return final score
			out.write(corr + "\n");
			out.flush();
			
		}catch (IOException e) {
			System.out.println(e.getMessage());
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

        //excute client connection using ExecutorService
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // receive client connection through while
        while (true) {
            Socket clientSocket = serverSocket.accept();  // waiting for client connection
            System.out.println("New client connected");

            //Submit to threadPool after generate Server object that excute client connection
            executorService.submit(new ThreadServer(clientSocket));
        }
    }
}
