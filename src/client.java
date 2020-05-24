import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

/*
This program tries to connect to a specific server
After connecting to the server, a chat room appears where both can communicate
By doing this we can create a back and fourth communication between two different clients
 */

public class client {
    static JFrame frame = new JFrame("client");
    static JPanel panel = new JPanel();
    static JTextArea chatResult = new JTextArea();
    static JTextField chatWrite = new JTextField();
    static JButton button = new JButton();
    static Socket s;
    static OutputStream out;
    static InputStream in;
    static byte[] bufferIn, bufferOut;
    static Thread receiveMessage = new Thread(new listener());

    public static void main(String Args[]) {
        window();
    }

    public static void window(){ // when user presses the button it tries to find the server. When connecting to the server chatroom() gets called and both clients can now communicate
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(450, 550);
        panel.setLayout(new BorderLayout());

        frame.add(panel);
        panel.add(button, BorderLayout.CENTER);

        button.setText("Connect to server");
        button.setFont(new Font("TimesRoman", Font.PLAIN, 50));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    s = new Socket("81.227.57.155", 7777); // searches

                    in = s.getInputStream();
                    out = s.getOutputStream();

                    System.out.println("Connected to a server");
                    chatroom(); // changing panel with chat room

                    receiveMessage.start(); // starting thread that receive messages
                }

                catch(Exception q) {
                    button.setText("Server is offline");
                }
            }
        });
    }

    public static void chatroom(){ // rewrites the panel and the chatroom appears
        panel.remove(button); // removes button from window()
        panel.revalidate(); // updates panel
        panel.repaint(); // updates panel

        JScrollPane scroll = new JScrollPane(chatResult); // adds scroll function

        panel.add(chatWrite, BorderLayout.PAGE_END);
        panel.add(scroll, BorderLayout.CENTER);

        chatResult.setEditable(false);

        chatWrite.setText("write here...");
        chatWrite.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                chatWrite.setText("");
            }
        });

        chatWrite.addKeyListener(new KeyListener() { // if the use types more than 0 characters and less than 100 characters, and presses ENTER he sends a message
            public void keyTyped(KeyEvent e) {}

            public void keyPressed(KeyEvent e) {}

            public void keyReleased(KeyEvent e) {
                if(chatWrite.getText().length() > 0 && chatWrite.getText().length() < 100){ // if client types right amount of characters
                    if(e.getKeyCode() == KeyEvent.VK_ENTER){ // if client presses ENTER, he sends a message
                        try{
                            chatResult.append("Client: " + chatWrite.getText() + "\n");

                            bufferOut = new byte[1000]; // creates a buffer with a limit of 1000 bytes

                            bufferOut = chatWrite.getText().getBytes(); //checks the amount of bytes used out of the 1000 bytes
                            out.write(bufferOut); // sends right amount of bytes
                            System.out.println("signal givet");

                            chatWrite.setText(""); // reset
                        }

                        catch(Exception r){}
                    }
                }
            }
        });
    }

    public static class listener implements Runnable{ // waits for client to send a message. After having a message receieved, it appears on the chat room
        public void run() {
            try{
                while(true){
                    bufferIn = new byte[1000]; // creates a buffer with a limit of 1000 bytes

                    in.read(bufferIn); // reads the amount of bytes sent by the other client
                    System.out.println("signal taget");
                    String response = new String(bufferIn); // translates using the amount of bytes sent by the other client
                    chatResult.append("Server: " + response + "\n"); // putting message on screen
                }
            }

            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}