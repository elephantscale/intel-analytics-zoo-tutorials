# Model Serving Lab


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
python ./zoo/python_packages/sources/bigdl/dataset/movielens.py
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
cp ncf_model.bin $TUTORIALS_DIR/model-serving-lab
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

```bash
curl localhost:8080/recs
