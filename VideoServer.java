import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class VideoServer extends Thread{
    @Override
    public void run() {
        VideoServer.main(null);
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        try {
            try (ServerSocket serverSocket = new ServerSocket(8080)) {
                System.out.println("Server is running. Waiting for a client...");

                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected.");

                VideoCapture videoCapture = new VideoCapture(0);

                if (!videoCapture.isOpened()) {
                    System.err.println("Error: Could not open camera.");
                    return;
                }

                Mat frame = new Mat();
                MatOfByte matOfByte = new MatOfByte();
                DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

                while (true) {
                    videoCapture.read(frame);

                    if (!frame.empty()) {
                        Imgcodecs.imencode(".jpg", frame, matOfByte);
                        byte[] encodedFrame = matOfByte.toArray();

                        // Send the length of the frame
                        dataOutputStream.writeInt(encodedFrame.length);

                        // Send the encoded frame to the client
                        OutputStream outputStream = clientSocket.getOutputStream();
                        outputStream.write(encodedFrame);

                        matOfByte.release();
                        outputStream.flush();
                    } else {
                        System.err.println("Error: Unable to capture frame from the camera.");
                        break;
                    }
                }

                videoCapture.release();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
