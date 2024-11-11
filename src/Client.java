import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Client {
	static JButton button;
	static String text1;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedReader in = null;
		BufferedWriter out = null;
		Socket socket = null;
		Scanner scanner = new Scanner(System.in);
		int q_num = 0;
		
		// JFrame generation (GUI)
        JFrame frame = new JFrame("Quiz Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);  // 창 크기 설정

        // Layout setup
        frame.setLayout(new FlowLayout());

        // components generation
        JLabel label1 = new JLabel();
        JTextField textField = new JTextField(15);
        button = new JButton("Submit");
        JLabel label2 = new JLabel("결과");
        JLabel label3 = new JLabel();
        
        //button action
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(false);
            }
        });
        
        frame.add(label1);
        frame.add(textField);
        frame.add(button);
        frame.add(label2);
        frame.add(label3);

        // visualize on screen
        frame.setVisible(true);
        
        
        //Connect to socket
        try {
			socket = new Socket ("localhost", 9999);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
        	System.out.println("소켓 연결 중 오류가 발생했습니다.");
        }
        
		//receive the number of questions
		try {
			q_num = Integer.parseInt(in.readLine());
		}catch (IOException e) {
			System.out.println("질문의 개수를 받아오는 동안 오류가 발생했습니다.");
		}
		
		//repeat
		for(int i=0; i<q_num; i++) {
			
			//receive question
			try {
				String inputMessage = in.readLine();
				label1.setText(inputMessage);
				System.out.println("Qusetion: " + inputMessage);
			} catch (IOException e) {
				System.out.println("서버에서 문제를 받아오는 중에 문제가 발생했습니다.");
			}
			
			//wait button click
			button.setEnabled(true);
			waitForAnswer();

			//write answer
			try {
			String outputMessage = textField.getText();
			out.write(outputMessage + '\n');
			out.flush();
			} catch (IOException e) {
				System.out.println("서버에 답을 제출하는 중에 문제가 발생했습니다.");
			}
			
			//receive result
			try {
				String resultMessage = in.readLine();
				label2.setText("<html>결과<br>" + resultMessage + "</html>");
				System.out.println("Result: " + resultMessage);
			}catch (IOException e) {
				System.out.println("서버에서 결과를 불러오는 중에 문제가 발생했습니다.");
			}
		}
		
		//receive result score
		try {
			q_num = Integer.parseInt(in.readLine());
		}catch (IOException e) {
			System.out.println("서버에서 점수를 불러오는 중에 문제가 발생했습니다..");
		}
		
		label3.setText("\n\n\n\n\n\n\n\n\nFinal Score : " + q_num);
		System.out.println("Final Score : " + q_num);
		

		try {
			scanner.close();
			if (socket != null)
				socket.close();
			
		}catch (IOException e) {
			System.out.println("서버와 채팅중 오류발생");
		}
	}
	private static void waitForAnswer() {
        while (button.isEnabled()) {
            try {
                Thread.sleep(100); // 버튼이 활성화될 때까지 대기
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
