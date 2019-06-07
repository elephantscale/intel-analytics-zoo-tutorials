# Model Serving Example


## Introduction

It's one thing to train a model with Analytics Zoo. But how do we use this in our Java or Scala 
application?  In this exercise, we will see how to incorporate your model into your application.

This uses a feature of the Analytics Zoo framework called Model Serving.

We are going to use an example already contained in Analyics Zoo around Recommendation, the NeuralCF example. You can
view it [here](https://github.com/intel-analytics/analytics-zoo/tree/master/zoo/src/main/scala/com/intel/analytics/zoo/examples/recommendation/README.md)

We will then wrap the example in a web service based on Spring Boot, a subproject of Spring Framework.


## Step 1: Clone the Analytics Zoo github repo


The NeuralCF example is part of analytics Zoo.

```bash
git clone https://github.com/intel-analytics/analytics-zoo.git
```

## Step 2: Download and install Spark

Analytics zoo currently uses Spark v2.1, so download that.  You also need to set up SPARK_HOME

```bash
wget https://archive.apache.org/dist/spark/spark-2.1.0/spark-2.1.0-bin-hadoop2.3.tgz
tar zxvf spark-2.1.0-bin-hadoop2.3.tgz
mv spark-2.1.0-bin-hadoop2.3 spark
export SPARK_HOME=./spark
```



## Step 3: Modify the NeuralCF example to save a model

Edit the file [NeuralCFexample.scala](https://github.com/intel-analytics/analytics-zoo/blob/master/zoo/src/main/scala/com/intel/analytics/zoo/examples/recommendation/NeuralCFexample.scala)

```bash
cd analytics-zoo
vi zoo/src/main/scala/com/intel/analytics/zoo/examples/recommendation/NeuralCFexample.scala
```

After approxiately around line 122:

```scala
userRecs.take(10).foreach(println)
itemRecs.take(10).foreach(println)
```

Add the following line:

```scala
ncf.saveModel("ncf_model.bin")
```

## Step 4: Build analytics zoo


```bash
mvn clean package -DskipTests
```

Run `make-dist.sh`

```bash
make-dist.sh
```

Set `ANALYTICS_ZOO_HOME to the `dist` folder

```bash
export ANALYTICS_ZOO_HOME=/path/to/analytics-zoo/dist
```



## Step 5: Get the data

Run the following to get the data:

```bash
wget http://files.grouplens.org/datasets/movielens/ml-1m.zip 
unzip ml-1m.zip
mkdir -p /tmp/movielens
mv ml-1m /tmp/movielens
```


## Step 6: Run the NeuralCF example and retrieve the model.


```bash
   ${ANALYTICS_ZOO_HOME}/bin/spark-shell-with-zoo.sh \
   --master local[*] \
   --driver-memory 4g \
   --executor-memory 4g \
   --class com.intel.analytics.zoo.examples.recommendation.NeuralCFexample \
   --inputDir /tmp/movielens/ml-1m 

```


## Step 7: Copy the model to the folder model-serving-lab in the Analytics Zoo Tutorials bundle.

```bash
cp ncf_model.bin $TUTORIALS_DIR/model-serving-lab/model.bin
```

## Step 8: Examine the Logic for the Web Service

Most of the web service logic can be found in the file `src/main/java/com/elephantscale/apilab1/HelloController.java`.

Here is where we take the `model.bin` file and load it to be served:

```java
String modelPath = System.getProperty("MODEL_PATH", "model.bin");
rcm = new NueralCFJModel();
rcm.load(modelPath);
```

Here is where we take the input user item pairs and return the output:

```java
List<UserItemPair> userItemPairs = new ArrayList<>();
userItemPairs.add(new UserItemPair(user,item));

List<List<JTensor>> jts = rcm.preProcess(userItemPairs);
List<List<JTensor>> finalResult = rcm.predict(jts);

String returnVal = "";

for (List<JTensor> fjt : finalResult) {
   for (JTensor t : fjt) {
      returnVal += t + "\n";
   }
}
return returnVal;
```


## Step 8: Build the Model Serving Lab

```bash
cd model-serving-lab
mvn clean package
```

## Step 9: Run the Spring Boot Service

```bash
mvn spring-boot:run
```

If you need a different port other than port 8080, because for example you have another service running on 8080, do the following:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8085
```

Ensure there are no errors, and leave the service running

## Step 10: Test the Service using curl

Open up a new terminal and type the following:

```bash
curl localhost:8080
```

You should see the following result:

```console
Recommendation Service
```

## Step 11: Get the Recommendations using curl

Let's try getting some recommendations output. We will try getting predicted rating for user 1 and item 2.

```bash
curl localhost:8080/recs?user=1\&item=2
```

The correct output should be as follows:

```console
JTensor{data=[8.27429E-4, 0.01408234, 0.12668155, 0.78011787, 0.07829083], shape=[5]}
```

The result is the probabilities of prediction. In this case, the predicted rating is a 4 with a `.78` probability.
Note the .78 probability shows up in the 4th (3rd counting from zero) index of the returned JTensor object.

We can also try to get other results, for example, for user 2 and item 3 .

```bash
curl localhost:8080/recs?user=1\&item=2
```

The predicted output should be:
```console
JTensor{data=[0.028999357, 0.07262343, 0.8328836, 0.05013749, 0.0153561095], shape=[5]}
```

This would indicate that the predicted rating is 3 with a probability of `.83` .


