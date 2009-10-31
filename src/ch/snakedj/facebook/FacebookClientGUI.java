package ch.snakedj.facebook;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.schema.User;
import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;

public class FacebookClientGUI {
	
	Text txtUsernameFacebook;
	Text txtPasswordFacebook;
	Text txtUsernameGmail;
	Text txtPasswordGmail;
	
	public static void main(String[] args)
	{
		FacebookClientGUI fcg = new FacebookClientGUI();
		fcg.create();
	}
	
	public FacebookClientGUI()
	{

	}
	
	public void create()
	{
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(1024, 768);
		
		GridLayout layMain = new GridLayout();
		layMain.numColumns = 2;
		shell.setLayout(layMain);					
		
		GridLayout layFacebookGmail = new GridLayout();
		layFacebookGmail.numColumns = 2;
		
		//Facebook Group		
		Group grpFacebook = new Group(shell, SWT.NONE);
		grpFacebook.setText("Facebook");
		grpFacebook.setLayout(layFacebookGmail);
		
		Label lblUsernameFacebook = new Label(grpFacebook, SWT.NONE);
		lblUsernameFacebook.setText("Username: ");
		txtUsernameFacebook = new Text(grpFacebook, SWT.BORDER);
		Label lblPasswordFacebook = new Label(grpFacebook, SWT.NONE);
		lblPasswordFacebook.setText("Password:");
		txtPasswordFacebook = new Text(grpFacebook, SWT.BORDER | SWT.PASSWORD);		
		//Facebook Group
		
		//Gmail Group		
		Group grpGmail = new Group(shell, SWT.NONE);
		grpGmail.setText("GMail");
		grpGmail.setLayout(layFacebookGmail);
		
		Label lblUsernameGmail = new Label(grpGmail, SWT.NONE);
		lblUsernameGmail.setText("Username: ");
		txtUsernameGmail = new Text(grpGmail, SWT.BORDER);
		Label lblPasswordGmail = new Label(grpGmail, SWT.NONE);
		lblPasswordGmail.setText("Password:");
		txtPasswordGmail = new Text(grpGmail, SWT.BORDER | SWT.PASSWORD);		
		//Gmail Group
		
		Button btnNext = new Button(shell, SWT.NONE);
		btnNext.setText("Synchronize");
		btnNext.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int countNotMatched = 0;
				int countMatched = 0;
				try {
					ContactsService myService = new ContactsService("ch.snakedj.facebook");
					GmailClient gmailClient = new GmailClient();
					gmailClient.setUsername(txtUsernameGmail.getText());
					try {
						myService.setUserCredentials(txtUsernameGmail.getText(), txtPasswordGmail.getText());
						gmailClient.loadAllContacts(myService);
					} catch (AuthenticationException e1) {
						e1.printStackTrace();
					} catch (ServiceException e1) {
						e1.printStackTrace();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					
					System.out.println("-----------------------------------------------------------");
					
					
					FacebookClient facebookClient;								
					facebookClient = new FacebookClient();
					facebookClient.setUsername(txtUsernameFacebook.getText());
					facebookClient.setPassword(txtPasswordFacebook.getText());
					try {
						facebookClient.login();
					} catch (HttpException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					List<User> friends = facebookClient.getFriends();
					
					for(User friend:friends)
					{
						ContactEntry gmailContact = gmailClient.getContact(friend.getLastName()+"_"+friend.getFirstName());
						
						if(gmailContact == null)
						{
							gmailContact = gmailClient.getContact(friend.getFirstName()+"_"+friend.getLastName());
						}						
						
						if(gmailContact != null)
						{
							countMatched++;
							
							if(gmailContact.getContactPhotoLink().getEtag() == null && friend.getPicBig() != null)
							{
								updateProfilePhoto(myService, friend,
										gmailContact);

							    System.out.print("PHOTO ");
							}
							
//							Birthday birthday = gmailContact.getBirthday();
//							System.out.println(birthday.getWhen());
							
							
							System.out.println(friend.getLastName()+"_"+friend.getFirstName());
						}
						else
						{
							countNotMatched++;
							System.err.println(friend.getLastName()+"_"+friend.getFirstName());
						}											
					}
					
				} catch (FacebookException e1) {
					e1.printStackTrace();
				}
				System.out.println("not matched" + countNotMatched + " matched " + countMatched);
			}

			private void updateProfilePhoto(ContactsService myService,
					User friend, ContactEntry gmailContact) {
				Link photoLink = gmailContact.getContactPhotoLink();
				URL photoUrl;
				try {
					photoUrl = new URL(photoLink.getHref());
					GDataRequest request = myService.createRequest(GDataRequest.RequestType.UPDATE,
						    photoUrl, new ContentType("image/jpeg"));

						    request.setEtag(photoLink.getEtag());

						    OutputStream requestStream = request.getRequestStream();
						    
						    URL urlPic = new URL(friend.getPicBig());

							BufferedInputStream in = new BufferedInputStream(urlPic
									.openStream());
							
							byte data[] = new byte[1];
							while (in.read(data, 0, 1) >= 0) {
								requestStream.write(data);
							}																					    
							
						    request.execute();
						    in.close();
				
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e5) {
					e5.printStackTrace();
				} catch (ServiceException e6) {
					e6.printStackTrace();
				}
			}
		});

		
		shell.open();		
		while(!shell.isDisposed())
		{
			if(!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		display.close();
	}
	
}
