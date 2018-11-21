package de.uulm.in.vs.grn;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class SockagramResponse {

    /**
     * This method reads a Sockagram Response from the specified {@link InputStream} and returns the resulting
     * image file if filter application was successful.
     * If the Sockagram server indicates an error, {@link SockagramException} gets thrown containing the status code
     * and error message from the server.
     *
     * @param inputStream The stream to read the Sockagram response from
     * @return The resulting image
     * @throws SockagramException If the server indicates an error
     * @throws IOException        If reading from the {@link InputStream} fails
     */
    public static byte[] parse(InputStream inputStream) throws SockagramException, IOException {
        ByteBuffer header = ByteBuffer.wrap(inputStream.readNBytes(5));
        byte status = header.get();
        int payloadLength = header.getInt();
        byte[] payload = inputStream.readNBytes(payloadLength);
        if (status != 0) {
            throw new SockagramException("Status " + status + ": " + new String(payload));
        }
        return payload;
    }
}
