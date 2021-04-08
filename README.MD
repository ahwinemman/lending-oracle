# Lending Oracle

Simple application to optimize cash at hand when provided list of loans.


## Requirements
Application requirements include:
* JDK 1.8
* Maven

## Running Project
1. Go into project directory
2. Run **mvn clean package** to compile jar
3. Run the generated jar **lending-oracle-jar-with-dependencies.jar** with below command
```` cmd script ````
  `java -jar target/lending-oracle-jar-with-dependencies.jar (inputFilePath> (outputFilePath) (N) (K)`
   
### Sample Input
`java -jar target/lending-oracle-jar-with-dependencies.jar /Users/branch/Documents/branch/applications.json ./output.txt 50000 1000`



