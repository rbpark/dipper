package dipper.webapp;

import java.io.IOException;
import java.net.Socket;

import org.mortbay.jetty.security.SslSocketConnector;

public class RedirectingSSLSockectConnector extends SslSocketConnector {
	@Override
	public void accept(int acceptorId) throws IOException, InterruptedException {
		Socket socket = _serverSocket.accept();
	}
}