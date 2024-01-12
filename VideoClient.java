import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class VideoClient extends Thread{
    @Override
    public void run() {
        VideoClient.main(null);
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        try {
            try (Socket socket = new Socket("localhost", 8080)) {
                Mat frame = new Mat();
                MatOfByte matOfByte = new MatOfByte();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                while (true) {
                    // try {
                    //     Thread.sleep(100);
                    // } catch (InterruptedException e) {
                    //     e.printStackTrace();
                    // }
                    // Receive the length of the frame
                    int frameLength = dataInputStream.readInt();

                    // Receive the encoded frame from the server
                    InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[frameLength];
                    int bytesRead = inputStream.read(buffer);

                    if (bytesRead > 0) {
                        // Convert the byte array to a Mat object
                        matOfByte.fromArray(buffer);
                        frame = Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_UNCHANGED);

                        // Check if the frame is not empty before displaying
                        if (!frame.empty()) {
                            HighGui.imshow("Video Client", frame);
                            HighGui.waitKey(10);
                        } else {
                            System.err.println("Warning: Received empty or corrupted frame from the server.");
                        }
                    } else {
                        System.err.println("Error: No data received from the server.");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("SERVER DOWN "+e.getMessage());
        }
    }
}
