package ch.snakedj.facebook;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Test {

	public static void main(String[] args) throws IOException
	{
		URL url = new URL("http://www.snakedj.ch/wp-content/jmeter_socket_exception.jpg");
		BufferedInputStream in = new BufferedInputStream(url.openStream());
		FileOutputStream fos = new FileOutputStream("c:/tmp/test.jpg");
		BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
		byte data[] = new byte[1];
		while (in.read(data, 0, 1) >= 0) {
			bout.write(data);
		}
		bout.close();
		in.close();
	}
	
}
