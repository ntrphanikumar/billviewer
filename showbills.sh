rm electricity.html
gradle clean build fatjar
java -jar build/lib/billviewer-1.0.jar
firefox electricity.html
