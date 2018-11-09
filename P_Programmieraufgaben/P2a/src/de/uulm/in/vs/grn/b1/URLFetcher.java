package de.uulm.in.vs.grn.b1;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

public class URLFetcher {
    private static final String DEFAULTURL = "http://http.jonasotto.com/index.html";

    public static void main(String[] args) {
        try {
            URL url = new URL(args.length > 0 ? args[0] : DEFAULTURL);
            Socket socket = new Socket(url.getHost(), 80);

            Writer outputWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            outputWriter.write("GET " + url.getPath() + " HTTP/1.1\r\n");
            outputWriter.write("Host: " + url.getHost() + "\r\n");
            outputWriter.write("Connection: close\r\n");
            outputWriter.write("\r\n");
            outputWriter.flush();

            String statusLine = inputReader.readLine();

            if (statusLine.contains("200")) {

                // Ignore response header
                //noinspection StatementWithEmptyBody
                while (!inputReader.readLine().equals("")) ;

                // Write to file
                File f = new File(
                        url.getFile().equals("") ? "noFilename.html" : url.getFile().substring(1).replace('/', '_'));

                if (!f.createNewFile()) System.out.println("Overwriting file \"" + f.getName() + "\"");

                BufferedWriter fileWriter = new BufferedWriter(new FileWriter(f));
                inputReader.transferTo(fileWriter);
                fileWriter.close();

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
