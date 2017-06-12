# Buildit crawler

Its a maven based java application, it uses Junit and mockito for unit testing. It uses slf4j simple for logging on console.

#### Prerequisites:
```
   Java 8, Maven, Git
```

#### To build the application:
```
   mvn clean install
```

#### To run the application:
```
  mvn exec:java -Dexec.args=[Valid URL enclosed in double quotes]
  e.g. mvn exec:java -Dexec.args="http://wiprodigital.com"
  
  It will create a file urlOutput.txt in project folder that will have the name of all urls
  It will also create another file resOutput.txt for all the static resources
```

#### To run the test cases:
```
  mvn test
  This application has more than 30 unit test cases. Most of the classes are 100% covered with both negative and positive test cases.
``` 

#### Important Files:
```
  App: contains main method
  ProcessController: Validates initial input and responsible for concurrency and thread control
  Crawler: A runnable and handles most of the application logic
  Util: Handles util functions, like taking a Url connection
  CrawlerRejectedExecutionHandler: rejected execution handler
```
   
#### New changes
  1. Including Static Resources:
  ```
  To select static resources in the crawler I added following new method

      protected void crawlUrlforStaticResources(final Document doc) {
          final Elements elements = doc.select("[src]");
          for (Element element : elements) {
              final String resource = element.attr("abs:src");
              if (!staticResources.contains(resource)) {
                  staticResources.add(resource);
              }
          }
      }
  ```
  2. Include external urls
  ```
  To select external urls I have to change the consition in code which does not select the external urls.
  Now I have changed the condition to include the internal urls for further crawling and external urls are added but not crawled.
      
      if(childUrl.startsWith(rootUrl)) {
         queue.offer(childUrl);
      } else {
         crawledUrls.add(childUrl);
      }
  ```
  I have also changed the respective test cases and added few new test cases
