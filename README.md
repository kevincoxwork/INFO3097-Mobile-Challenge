# INFO3097 - Fanshawe College 2019

This application was developed as part of the of the TD Challenge where we were encouraged to incorporate the use of ML Kit into a financial application. This project was also the cumulative project from the Fanshawe College course INFO3097.

# Application Screenshots
![hamburger menu of the application](https://i.imgur.com/c2XN2PG.png)
![Nearby locations in downtown toronto](https://i.imgur.com/Znbep5u.png)
![Information on a vendor](https://i.imgur.com/GBG6g5k.png)

![Nearby Search](https://i.imgur.com/CLtWY6o.png)

![Found location](https://i.imgur.com/VFMIrzy.png)

![Ability to track from image or camera](https://i.imgur.com/Z6LUqaj.png)



# Application Functionality

Something to note for location-based services in this application. Since all endpoint data is only reflected in GTA (Greater Toronto Area) location we are forcing the user’s location to be the Toronto Dominion Center (66 Wellington St W Suite 3800, Toronto, ON M5K 1A1).

## Home

The user is initially prompted with the home page. This is a static image taken from the TD Application. It has no functionality and is only used to provide a simulation that this is a TD application.

Track A Purchase

This window of the application allows the user to track past purchases using ML Kit. The application will open up the camera and the user will be asked to take a photo of their purchase receipt. ML Kit will scan the image provided and look for the total on the receipt (commonly the highest number value in the image).

This information, along with nearby locations, will be provided to the user in a pop-up modal. The user will have the ability to select the nearby location and make adjustments to their total (if it was reported incorrectly by ML Kit). Upon confirmation, the record is added and the user can view their past purchases. The total is also provided to the purchase location and a more accurate average is calculated with this value.  
  
Budget Based Search

This window of the application allows customers to select different search criteria (business type, distance from you, total spending) and retrieve a set of records that match. These are displayed by nearest location and the user has the ability to parse between locations. Lastly, just because something is within your budget doesn’t mean you should spend the entirety of your budget. This is why a “budget used” bar is presented on the bottom where it will alert the customer if their spending reaches of 80% of the allotted amount.

Explore Businesses Near Me  
This window of the application allows customers to search all the tracked locations on a full-screen version of google maps. It presents each categorized location in a different palette and allows users to tap on each location to get the business name, address and expected spending.  
About

This window of the application contains project credits.

# Application Flow Process

## TD Davinci Endpoint Scraper

The TD Davinci API endpoint provides simulated data on the following topics.

Raw Data - /api/raw-customer-data
Customer - /api/customers/{custId}
Account - /api/accounts/{acctId}
Transaction - /api/transactions/{tranId}

The data that I required for application functionality was not available as an endpoint as I was required to complete some work on my end. My initial thoughts were that I required complete location data and consolidated purchase data. Ideally, I wanted to retrieve this data at runtime from the DaVinci endpoint. Upon further investigation, I found that it would be a quite time consuming and intensive process to consolidate this data at runtime considering that the number of customer transactions alone was over 2.5 million and well exceeded 2 GB.

I decided to consolidate the data locally in a database. To complete this and to not exceed time restraints, I developed a small Node.js & Mongo DB application which I had named “td-scraper”.  TD-Scraper was used to pull a majority of the data down from the endpoint and consolidate this data into my Mongo DB application.

In order to consolidate the endpoint, I first had to fetch from the Raw Data Endpoint which returned customer ids by sets of 1000. Next, I took single customer ID’s and was able to fetch full customer information which included each of their transactions from the Customer Endpoint. Lastly, I was able to take each transaction ID and fetch from the Transaction Endpoint. In summary, this allowed for consolidated average price spent at a location and each physical location of each transaction.  
  
Overall, this data had a much smaller footprint and only totaled 6.4 MB in size and consisted of around 1300 unique locations. With each fresh installation of my application this data is located into the SQLite database from a .csv file.  
  
# Relevance to Finance

While in Toronto, Ontario I came across an issue with the Google Maps recorded expense field. This scale was weighted by the number of “$” (dollar signs) and would range from location to location. I’ve used this feature of google maps in locations other than Toronto and I’ve noticed a discrepancy between these arbitrary figures.  A “$$” in my home down is **not** equal to a “$$” in Toronto.

## Proper Budgeting and Expected Spending

Since a financial institution has the ability to track billions of purchases a year, it should be able to create an accurate spending model for each transaction location. At the start of the product, I thought about developing my own spending model in TensorFlow but this developed into something that was beyond my means.  
Ideally, we could improve upon my “average calculation” by building a model that would be able to better infer the estimated spending of an individual at a location. But as it stands, providing the customer with a concrete spending value instead of an arbitrary “$” values would allow customers to better estimate their spending’s. Customer’s are currently able to search by range and criteria or in the “Explore” header, free to explore all locations.

## Purchase History Tracking

Purchase history tracking factors another scenario into the system. The track a purchase feature, allows customers to track purchases where they did not use their bank product to make payment. This allows customers to better estimate their own personal spending’s. Additionally, this allows TD to gather information on external purchases which then could be entered into the estimated spending model for more accurate estimations.

# Conclusion

Overall, I really enjoyed the time I spent developing the TD Challenge App. I would say that my favorite aspect was utilizing the data fetched from the Davinci endpoint. On a personal note, I would enjoy if this was a feature in existing google maps applications but I don’t believe this to be possible without the involvement of a financial institution or another entity that possessed the transaction data.
