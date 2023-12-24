# Practice on API call, Room Database, Geocoding, Get Device Location

**Description:**

A two screen android application that displays weather reports for a given city and save the reports to a local room database.
1. Current Weather Screen: Displays weather information at the device’s current location.
2. History Screen: Displays a list of previously searched weather information (from the room database)

**Screen 1: Current Weather Screen**

The screen contains:
- A textbox that lets the user enter a city (example: Paris, France)
  - When the screen initially loads, the textbox is populated with the the device’s current city
- A button labelled “GET WEATHER REPORT”.
  - When the button is pressed, the current weather in the specified city is shown
-  A lable that displays the weather report in the specified city. Including:
   - The current temperature, in Celcius
   - The humidity
   - The current weather conditions (sunny, overcast, etc)
   - The time of the weather report
- A SAVE WEATHER REPORT button:
  - Clicking on this button saves the currently displayed weather information to the Room database
- Weather data is retrieved from VisualCrossing.com
  - In the API response, the currentConditions property is used to retrieve the required weather data.
  - API data is retrieved using **Retrofit** + **Moshi**
    
**Screen 2: Weather History**

This screen shows the saved weather reports from the Room database, which are displayed in a RecyclerView.
- Each row shows a city name and all its related weather data
