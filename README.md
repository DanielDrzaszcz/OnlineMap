# OnlineMap

An Java Enterprise Edition application storing and providing via SOAP a city map and an Android client application for display map fragments. <br /> <br />

Features of the server application:
- storing the city map on the server and making it available to clients
- the ability to provide to clients a miniatured version of the original map
- the ability to provide clients a map fragment with the range given by the client as pixels or coordinates <br /> <br />

Features of the client application:
- displaying data downloaded from the server
- the ability to send request to download the miniatured version of the map from the server
- the ability to send request to download a map fragment from the server with a range as coordinates entered by the user in text fields
- the ability to send a request to download a map fragment from the server by touching the map with the range corresponding to the selected range by touching
- the ability to change the server's IP address on the Settings screen <br /> <br />

Technical solutions in the server application:
- created Web Service with methods for  get a miniature of the map, get map fragment by pixels and get map fragment by coordinates
- used Base64 format to encode and send the .png map <br /> <br />

Technical solutions in the client application:
- used ksoap2 library to communicate with the server
- created Custom View (drawing on the image selected by the user range)
- used Shared Preferences to save the ip address setted by the user
- used AsyncTask to download data from the server
- created manually marshalled double type for use in ksoap2 library <br /> <br />

![Screenshot_1](https://user-images.githubusercontent.com/59321506/75929919-d4a32b00-5e71-11ea-80e0-45561ad07e86.png)
![Screenshot_2](https://user-images.githubusercontent.com/59321506/75929925-d5d45800-5e71-11ea-9c79-fd0d9525d99f.png)
 <br />
![Screenshot_3](https://user-images.githubusercontent.com/59321506/75929926-d66cee80-5e71-11ea-8887-68d3353863e1.png)
![Screenshot_4](https://user-images.githubusercontent.com/59321506/75929928-d7058500-5e71-11ea-9ce0-9cc18e9538aa.png)
