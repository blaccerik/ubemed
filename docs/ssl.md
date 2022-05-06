hosted on digitalocean  
nameserver: freenom 

java:  
gradle clean build  
gradle clean build -x test  
java -jar app/build/libs/app-0.0.1-SNAPSHOT.jar

angular:  
npm i  
npm run start
ng build
