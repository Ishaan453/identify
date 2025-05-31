🗃# Database Overview
The current Contact table contains 9 entries:

## Primary Contacts
ID	Email	Phone Number	Notes <br>
1	doc@fluxkart.com	555555	<br>
2	lorraine@hillvalley.edu	123456 <br>	
4	george@hillvalley.edu	919191	<br>
6	marty@hillvalley.edu	(null)	<br>
7	(null)	999999	<br>
8	old@hillvalley.edu	888888	Deleted (2023-02-01) <br>
9	new@fluxkart.com	new123	<br>

## Secondary Contacts
ID	Email	Phone Number	Linked To  <br>
3	mcfly@hillvalley.edu	123456	2 <br>
5	biffsucks@hillvalley.edu	717171	4 <br>

# Sample Request Body
Send a POST request to /identify with:
{
  "email": "mcfly@hillvalley.edu",
  "phoneNumber": "123456"
}

# Successful Response
{
  "contact": {
    "primaryContatctId": 2,
    "emails": [
      "lorraine@hillvalley.edu",
      "mcfly@hillvalley.edu"
    ],
    "phoneNumbers": [
      "123456"
    ],
    "secondaryContactIds": [3]
  }
}
