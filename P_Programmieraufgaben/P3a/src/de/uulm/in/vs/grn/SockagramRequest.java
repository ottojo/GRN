package de.uulm.in.vs.grn;

import java.nio.ByteBuffer;

public class SockagramRequest {
    private byte filterType;
    private byte[] payload;

    /**
     * Creates a Sockagram request for a specified image with a specified filter.
     *
     * @param filterType The number of the filter to apply
     * @param payload    The image to use
     * @see <a href="https://gitlab-vs.informatik.uni-ulm.de/grn/grn-sockagram-filters#Übersicht-der-verfügbaren-filter">List of filters</a>
     */
    public SockagramRequest(byte filterType, byte[] payload) {
        this.filterType = filterType;
        this.payload = payload;
    }

    /**
     * Builds the request
     *
     * @return The request
     */
    public ByteBuffer makeRequest() {
        ByteBuffer b = ByteBuffer.allocate(1 + 4 + payload.length);

        b.put(filterType);
        b.putInt(payload.length);
        b.put(payload);

        return b;
    }
}
