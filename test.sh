javac -encoding utf8 -implicit:class -Xlint:unchecked -cp .:lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar dominotest/DominoTest.java
java -cp .:lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar org.junit.runner.JUnitCore dominotest.DominoTest

javac -encoding utf8 -implicit:class -Xlint:unchecked -cp .:lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar dominotest/DominoRmiTest.java
java -cp .:lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar:lib/hsqldb.jar org.junit.runner.JUnitCore dominotest.DominoRmiTest
