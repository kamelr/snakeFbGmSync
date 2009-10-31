package ch.snakedj.facebook;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.ServiceException;

public class GmailClient {

		
	HashMap<String, ContactEntry> contactsByName;
	
	public GmailClient()
	{
		contactsByName = new HashMap<String, ContactEntry>();
	}
	
	public void loadAllContacts(ContactsService myService)
			throws ServiceException, IOException {
		URL feedUrl = new URL(
				"http://www.google.com/m8/feeds/contacts/corsin.capol@gmail.com/full");
		Query myQuery = new Query(feedUrl);
		  myQuery.setMaxResults(500);

		ContactFeed resultFeed = myService.getFeed(myQuery, ContactFeed.class);

		System.out.println(resultFeed.getTitle().getPlainText());
		for (int i = 0; i < resultFeed.getEntries().size(); i++) {
			ContactEntry entry = resultFeed.getEntries().get(i);

			if(entry.getName() != null && entry.getName().getFamilyName() != null && entry.getName().getGivenName() != null && entry.getName().getFamilyName().getValue() != null && entry.getName().getGivenName().getValue() != null)
			{				
				contactsByName.put(entry.getName().getFamilyName().getValue() +"_"+entry.getName().getGivenName().getValue(), entry);
				System.out.println(entry.getName().getFamilyName().getValue() +"_"+entry.getName().getGivenName().getValue());
			}
			else if(entry.getName() != null && entry.getName().getFullName() != null && entry.getName().getFullName().getValue() != null)
			{
				contactsByName.put(entry.getName().getFullName().getValue().replace(" ","_"), entry);
				System.out.println(entry.getName().getFullName().getValue().replace(" ","_"));
			}
		}

	}
	
	public ContactEntry getContact(String name)
	{
		return contactsByName.get(name);
	}

}
