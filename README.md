# **Code structure**

- Code is seperated in three packages. 
- Controller package contains endpoint specific functionality like endpoint and exception handlers using *@RestController*.
- All endpoints are prefixed with /api/v1. This probably could be configurable, but seemed unnecessary.
- Model package contains API response, request and their accompanying data structures. Data structures are described in **Controllers** section. 
- Service package contains service layer functionality. Services are described in **Services** section.
- Tests are sepperated in api tests and service tests.

# **Used libraries/frameworks**
- Project generated using Spring Initializr.
- Spring Boot - API implementation.
- Lombook - reducing boilerplate.
- Hamcrest - matchers for unit tests.
- REST Assured - convenient syntax for API testing.
- Gradle - build system.

# **Controllers**

- There are two controlers, one for the required external services and other for ticket draft price calculation.
- Controllers use services for their functionality.
- External service endpoints all use **ExternalService** class.
- Ticket price calculation enpoint uses **TicketDraftPriceService** class.

## **External endpoints**

### *GET /api/v1/external/tax*

### **Response**

Returns a list of data as described in **TaxRateResponse** class. Tax rate type is not required to complete the assignment, but it seems appropriate to identify it. Value is hardcoded to be 11.
```
[
    {
        "type": "VAT",
        "percentage": 11
    }
]
```
### *GET /api/v1/external/base_price*

### **Params**

- source, route starting point, only Riga supported.
- destination, route end location, only Vilnius supported.
- Both parameters are used in **Route** class and are mandatory.
### **Response**

Returns data as described in **RouteBasePriceResponse** class.
```
{
    "amount": 11
}
```

## **Ticket price calculation endpoint**

### *POST /api/v1/ticket*

### **Request body**

Request data is as described in **TicketDraftPriceApi** class. *routeBasePrice* is the amount obtained from /api/v1/external/base_price. *passengers* is a list of passengers as described in **Passenger** class. *taxRates* is a list of tax rates as described in **TaxRateResponse** class. *type* under **Passenger** class is as described in **PassengerType** enum. *type* under **TaxRateResponse** class is as described in **TaxRateType** enum. All parameters are mandatory to have values, except *luggageCount* which is assumed 0 if it does not exist. POST method was chosen instead of GET because it simplifies passing data to the API. There seem to be several methods to pass complex objects in URL parameters, but it is easier to simply pass everything in POST body.
```
{
    "routeBasePrice": 10,
    "passengers": [
        {
            "type": "ADULT",
            "luggageCount": 2
        }, 
        {
            "type": "CHILD",
            "luggageCount": 1
        }
    ],
    "taxRates": [
        {
            "type": "VAT",
            "percentage": 21
        }
    ]
}
```

### **Response**

Return data is as described in **TicketDraftPriceResponse** class. *items* contains a list of price calculation items as described in **TicketDraftPriceItem** class. *total* contains total price. *type* under **TicketDraftPriceItem** class is as described in **TicketItemType** enum. Total is the item total including VAT and item *count*. Count could be ommited if it is equal to 1, but that would probably require custom serialization logic.

```
{
    "items": [
        {
            "type": "ADULT",
            "total": "12.10 EUR",
            "count": 1
        },
        {
            "type": "LUGGAGE",
            "total": "7.26 EUR",
            "count": 2
        },
        {
            "type": "CHILD",
            "total": "6.05 EUR",
            "count": 1
        },
        {
            "type": "LUGGAGE",
            "total": "3.63 EUR",
            "count": 1
        }
    ],
    "total": "29.04 EUR"
}
```

# Exception handling
- Exceptions are handled using *@ControllerAdvice* in **ControllerExceptionHandler**. Each possible exception is handled accordingly.
- Errors are returned as described by **ErrorResponse** class, containing code and message.
- Serialization errors are also handled by returning "Invalid data format" message. Not the most useful, it would be better to check before serialization if for example *routeBasePrice* is numeric.
- Missing parameters and field validation failures are handled and formatted.
- BigDecimal seems to be infinite and I didn't manage to get any overflows. *luggageCount* seems to fail to serialize if the amount is too large.
- Invalid HTTP methods are not specifically handled.

# **Services**

## ExternalService

- Used by **ExternalEndpointController** class.
- *getTaxRates* function returns hardcoded tax rate of 11.
- *getRouteBasePrice* returns an optional route base price using **Route** class as a key to a map lookup. In case route is missing, calling Optional.get raises an exception which in turn is handled in **ControllerExceptionHandler**.

## CurrencyService

- Used when calculating amounts.
- Amounts use **BigDecimal** for calculations.
- *amountToMinorUnits* function is used to convert euros to cents. Required as all currency calculations are in minor units to prevent unnecessary rounding errors.
- *formatAmountToMajorUnits* function is used to convert back to euros from cents and format the amount.
- *roundMinor* is a rounding utility, using HALF_UP rounding mode.

## TicketDraftPriceService

- Used by **TicketDraftPriceController** class to calculate ticket draft price.
- Contains a nested class **TicketDraftPriceBuilder**. This contains current ticket state. Required to avoid code duplication if luggage is also added during loop in *getTicketDraftPrice* function. *addItem* function could be implemented under **TicketDraftPriceController**, but that would require passing current total amount, item list and **TicketDraftPriceCalculator** class. It seemed simpler to encapsulate current state in its own class with required interface.
- **TicketDraftPriceCalculator** class does the calculation logic. Contains static information about child and luggage discount and a map of item base prices which are instantiated from route base price in the constructor. *calculateItemPrice* function calculates the price of an item. *applyTax* function applies VAT. Function could be useful if there are multiple taxes.

# **Unit tests**
- Under model package, all api endpoints are tested. By extension **TicketDraftPriceService** and **ExternalService** is also tested here.
- Service package contains tests for **CurrencyService** and **TicketDraftPriceCalculator**.
