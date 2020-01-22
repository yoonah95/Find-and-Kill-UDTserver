package network;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

   public static final int ServerPort = 9999;
   private int num_client = 0;

   private DataOutputStream[] dos_arr = new DataOutputStream[2];

   public void play() throws IOException {
      ServerSocket ss = null;
      Socket s = null;
      int i;

      try {
         ss = new ServerSocket(ServerPort);
         System.out.println("S: Server Opend");
         while (true) {
            System.out.println("S: Server zzz");
            s = ss.accept();
            System.out.println("S: Server zz");
            if (dos_arr[num_client] == null) {
               dos_arr[num_client] = new DataOutputStream(s.getOutputStream());
            }
            if (num_client == 1) {
               System.out.println("준비 완료");
            }
            System.out.println(num_client);
            ServerThread st = new ServerThread(s, num_client);

            if (num_client == 0) {
               num_client++;
            }

            st.start();

            System.out.println("입장입장");
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   void send(String msg) {
      try {
         String s = String.format("%-64s", msg);
         byte[] bb ;
         bb = s.getBytes();
         if (dos_arr[0] != null) {
            dos_arr[1].write(bb);
         }

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   void send2(String msg) {
      try {
         String s = String.format("%-64s", msg);
         byte[] bb ;
         bb = s.getBytes();
         if (dos_arr[1] != null)
            dos_arr[0].write(bb);

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   // Listening thread
   public class ServerThread extends Thread {
      private Socket socket;
      private DataInputStream dis;
      private int id;
      String msg;

      ServerThread(Socket s, int i) {
         
         socket = s;
         id = i;
         System.out.println("서비스 스레드까지 왔다");
         
      }

      public void run() {
         try {
            service();
         } catch (IOException e) {
            dos_arr[id] = null;
            num_client--;
         }
      }

      private void service() throws IOException {
         System.out.println("서비스 돌아간다" + "id = " + id);
         dis = new DataInputStream(socket.getInputStream());

         byte[] str = new byte[64];
         while (true) {
            dis.read(str,0,64);
            
            msg = new String(str);
                msg = msg.trim();

            if (str == null) {
               dos_arr[id] = null;
               num_client--;
               System.out.println("내용이 없다. " + str);
               break;
            }

            if (id == 0) {
               send(msg);

               System.out.println("유저 1인 경우 = " + msg);
            } else {
               send2(msg);

               System.out.println("유저 2인 경우 = " + msg);
               
            }
         }
      }
   }
   
   public static void main(String[] args) {
      TCPServer s = new TCPServer();
      try {
         s.play();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}