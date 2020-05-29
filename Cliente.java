import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.util.Scanner;

public class Cliente {
	static {
	    disableSslVerification();
	}

	private static void disableSslVerification() {
	    try
	    {
	        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	            public void checkClientTrusted(X509Certificate[] certs, String authType) {
	            }
	            public void checkServerTrusted(X509Certificate[] certs, String authType) {
	            }
	        }
	        };

	        // Install the all-trusting trust manager
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	        // Create all-trusting host name verifier
	        HostnameVerifier allHostsValid = new HostnameVerifier() {
	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        };

	        // Install the all-trusting host verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } catch (KeyManagementException e) {
	        e.printStackTrace();
	    }
	}
	public static void main(String[] args) {		
		try {
			String [] urls = {"https://192.168.0.24/componentes/musica","https://localhost:3000/"};
			URL url = new URL(urls[1]);
			listing(url,true);
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
	public static String elements(String ln) {
		int a = ln.indexOf("href=\"",0);
		int b  = ln.indexOf(">",a);
		String s = ln.substring(a,b);
		a = s.indexOf("\"",0);
		s= s.substring(a+1, s.length()-1);
		return s;	
	}
	public static String getimages(String ln) {
		int a = ln.indexOf("=\"");
		int b = ln.indexOf("\">");
		String s = ln.substring(a+2,b);
		return s;
	}
	public static void listing(URL url, boolean is_nj) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			ArrayList<String> obj =new ArrayList<>();
			String ln;
			System.out.println("Seleciones cual archivo que  desea descargar:");
			int k =1;
			while((ln= br.readLine())!=null) 
				if(ln.contains(".jpg") || ln.contains(".mp3")) {
					String el = "";
					if(is_nj) 
						el = getimages(ln);
					else
						el = elements(ln);
					obj.add(el);
					System.out.println(k+") "+el);
					k++;
				}
			System.out.println(k+") Todo");
			Scanner sc = new Scanner(System.in);
			int op = sc.nextInt();
			if(op!=k) 
				descargar(new URL(url.toString()+"/"+obj.get(op-1)),obj.get(op-1));
			else 
				for (int i = 0; i < k-1; i++) 
					descargar(new URL(url.toString()+"/"+obj.get(i)),obj.get(i));	
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
	public static ArrayList<String> preprocesar(String s) {
		ArrayList<String> r = new ArrayList<String>();;
		String ln = "";
		for (int i = 0; i < s.length(); i++) {
			if(s.charAt(i)=='\t') {
				r.add(ln);
				ln = "";
			}
			else
				ln+= s.charAt(i);
			
		}
		return r;
	}
	public static void descargar(URL url,String name) {
		try {
			
			InputStream in = new BufferedInputStream(url.openStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			while (-1!=(n=in.read(buf)))
				out.write(buf, 0, n);
			out.close();
			in.close();
			byte[] response = out.toByteArray();
			FileOutputStream fos = new FileOutputStream("E:/Downloads/"+name);
			System.out.println(name+" archivo descargado");
			fos.write(response);
			fos.close();;
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
}
