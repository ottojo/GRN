package de.uulm.in.vs.grn.b2;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

public class URLFetcher {
    private static final String DEFAULTURL = "http://http.jonasotto.com/png.png";

    public static void main(String[] args) {
        try {
            URL url = new URL(args.length > 0 ? args[0] : DEFAULTURL);
            Socket socket = new Socket(url.getHost(), 80);

            Writer outputWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            InputStream inputStream = socket.getInputStream();

            outputWriter.write("GET " + url.getPath() + " HTTP/1.1\r\n");
            outputWriter.write("Host: " + url.getHost() + "\r\n");
            outputWriter.write("Connection: close\r\n");
            outputWriter.write("\r\n");
            outputWriter.flush();

            System.out.println("Reading Status Line");
            StringBuilder statusLine = new StringBuilder();
            while (!statusLine.toString().endsWith("\n")) {
                int read = inputStream.read();
                if (read == -1) {
                    System.out.println("EOF while reading status line");
                    System.exit(1);
                }
                statusLine.append((char) read);
            }

            System.out.println("Done");
            if (statusLine.toString().contains("200")) {

                // Ignore response header (read until empty line)
                boolean gotEmptyLine = false;
                while (!gotEmptyLine) {
                    //noinspection StatementWithEmptyBody
                    while ((char) inputStream.read() != '\n') ;
                    if (inputStream.read() == '\r') {
                        //noinspection ResultOfMethodCallIgnored
                        inputStream.skip(1);    // Skip '\n'
                        gotEmptyLine = true;
                    }
                }

                System.out.println("Reading Data");
                // Write to file
                File f = new File(
                        url.getFile().equals("") ? "noFilename.html" : url.getFile().substring(1).replace('/', '_'));

                if (!f.createNewFile()) System.out.println("Overwriting file \"" + f.getName() + "\"");

                FileOutputStream fileOutputStream = new FileOutputStream(f);
                socket.getInputStream().transferTo(fileOutputStream);
                fileOutputStream.close();

                System.out.printf("%s has been saved to %s\n", url.toString(), f.getName());
            } else {
                System.out.println(statusLine);
            }
            socket.close();
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL");
            e.printStackTrace();
        } catch (UnknownHostException e) {
            System.out.println("Unknown Host");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
