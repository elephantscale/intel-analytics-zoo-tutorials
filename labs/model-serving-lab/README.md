# Simple Service Sample of recommendation


## Introduction

It's one thing to train a model with Analytics Zoo. But how do we use this in our Java or Scala 
application?  In this exercise, we will see how to incorporate your model into your application.

This uses a feature of the Analytics Zoo framework called Model Serving.

We are going to use an example already contained in Analyics Zoo around Recommendation, the NeuralCF example. You can
view it [here](https://github.com/intel-analytics/analytics-zoo/tree/master/zoo/src/main/scala/com/intel/analytics/zoo/examples/recommendation/README.md)


## Step 1: Clone the Analytics Zoo github repo


The NeuralCF example is part of analytics Zoo.

```bash
git clone https://github.com/intel-analytics/analytics-zoo.git
```

Set the environment variable `ANALYTICS_ZOO_HOME` to the location you cloned the github repo.


## Step 2: Modify the NeuralCF example to save a model

Edit the file [NeuralCFexample.scala](https://github.com/intel-analytics/analytics-zoo/blob/master/zoo/src/main/scala/com/intel/analytics/zoo/examples/recommendation/NeuralCFexample.scala)

Step 1. Follow the [example in the zoo](https://github.com/intel-analytics/analytics-zoo/tree/master/zoo/src/main/scala/com/intel/analytics/zoo/examples/recommendation/README.md) to train a NeuralCFModel. You want to modify the [example](https://github.com/intel-analytics/analytics-zoo/blob/master/zoo/src/main/scala/com/intel/analytics/zoo/examples/recommendation/NeuralCFexample.scala) and save the trained model into "./models/ncf.bigdl".


After approxiately around line 125:

```scala
userRecs.take(10).foreach(println)
itemRecs.take(10).foreach(println)
```

Add the following line:

```scala
ncf.saveModel("ncf_model.bin")
```

## Step 3: Build analytics zoo


```bash
cd $ANALYTICS_ZOO_HOME
mvn clean package -DskipTests
```

## Step 4: Run the NeuralCF example and retrieve the model.


```bash
   ${ANALYTICS_ZOO_HOME}/bin/spark-shell-with-zoo.sh \
   --master local[*] \
   --driver-memory 4g \
   --executor-memory 4g \
   --class com.intel.analytics.zoo.examples.recommendation.NeuralCFexample \
   --inputDir ./data/ml-1m 

```


## Step 5: Copy the model to the folder model-serving-lab in the Analytics Zoo Tutorials bundle.

```bash
cp ncf_model.bin $TUTORIALS_DIR/model-serving-lab
```


## Step 6: Build the Model Serving Lab

```bash
cd model-serving-lab
mvn clean package
```

## Step 7: Run the Spring Boot Service

```bash
mvn spring-boot:run
```

Ensure there are no errors, and leave the service running

## Step 8: Test the Service using curl

Open up a new terminal and type the following:

```bash
curl localhost:8080
```

You should see the following result:

```console
Recommendation Service
```

## Step 9: Get the Recommendations using curl

```bash
curl localhost:8080/r
