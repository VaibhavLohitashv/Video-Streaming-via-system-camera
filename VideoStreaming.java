public class VideoStreaming extends Thread{
      public static void main(String[] args) {
        VideoServer s1 = new VideoServer();
        
        VideoClient c1 = new VideoClient();
        

        s1.start();
        c1.start();
    }
}
