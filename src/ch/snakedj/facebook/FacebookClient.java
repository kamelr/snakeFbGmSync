package ch.snakedj.facebook;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.FacebookXmlRestClient;
import com.google.code.facebookapi.ProfileField;
import com.google.code.facebookapi.schema.FriendsGetResponse;
import com.google.code.facebookapi.schema.User;
import com.google.code.facebookapi.schema.UsersGetInfoResponse;

public class FacebookClient {

	private static final String API_KEY = "403b27b1efd9dd5dff5ac1ddc2e629b1";
	private static final String SECRET = "";

	private FacebookXmlRestClient fbXmlClient;
	private String fbToken;
	private String sessionId;

	private String username;
	private String password;

	public void login() throws FacebookException, HttpException, IOException {
		HttpClient http = new HttpClient();
		http.getHostConfiguration().setHost("www.facebook.com");
		fbXmlClient = new FacebookXmlRestClient(API_KEY, SECRET, 3000);
		String token = fbXmlClient.auth_createToken();

		HttpClientParams params = new HttpClientParams();
		HttpState initialState = new HttpState();
		http.setParams(params);
		http.setState(initialState);
		GetMethod get = new GetMethod("/login.php?api_key=" + API_KEY
				+ "&v=1.0&auth_token=" + token);
		int getStatus = http.executeMethod(get);
		token = fbXmlClient.auth_createToken();

		PostMethod post = new PostMethod("/login.php?login_attempt=1");
		post.addParameter("api_key", API_KEY);
		post.addParameter("v", "1.0");
		post.addParameter("auth_token", token);
		post.addParameter("email", getUsername());
		post.addParameter("pass", getPassword());
		int postStatus = http.executeMethod(post);

		sessionId = fbXmlClient.auth_getSession(token);
	}

	public String getSession() throws FacebookException {
		return fbXmlClient.auth_getSession(fbToken);
	}

	public List<User> getFriends() throws FacebookException {
		// fbXmlClient.auth_getSession(fbToken);
		fbXmlClient.getClient().friends_get();
		FriendsGetResponse response = (FriendsGetResponse) fbXmlClient
				.getResponsePOJO();
		List<Long> friends = response.getUid();

		fbXmlClient.users_getInfo(friends, EnumSet.of(ProfileField.NAME,
				ProfileField.PIC_BIG, ProfileField.LAST_NAME,
				ProfileField.FIRST_NAME, ProfileField.PROXIED_EMAIL,
				ProfileField.BIRTHDAY));

		UsersGetInfoResponse userResponse = (UsersGetInfoResponse) fbXmlClient
				.getResponsePOJO();

		List<User> users = userResponse.getUser();
		return users;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
