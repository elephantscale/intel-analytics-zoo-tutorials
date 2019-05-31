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

```bash
curl localhost:8080/recs
```

The correct output should be as follows:

```console
JTensor{data=[8.27429E-4, 0.01408234, 0.12668155, 0.78011787, 0.07829083], shape=[5]}
JTensor{data=[0.028999357, 0.072623424, 0.8328836, 0.0501375, 0.015356112], shape=[5]}
JTensor{data=[0.028534638, 0.43427455, 0.3857758, 0.123056024, 0.028358938], shape=[5]}
JTensor{data=[0.5963797, 0.31189707, 0.023491517, 0.062192094, 0.0060396], shape=[5]}
JTensor{data=[0.027380347, 0.18206437, 0.14272906, 0.5790257, 0.068800524], shape=[5]}
JTensor{data=[0.0047057667, 0.023357205, 0.26320422, 0.45267543, 0.2560574], shape=[5]}
JTensor{data=[6.8185234E-4, 0.022511918, 0.3564061, 0.57112473, 0.04927548], shape=[5]}
JTensor{data=[0.026432022, 0.3933438, 0.48831466, 0.085218534, 0.0066909124], shape=[5]}
JTensor{data=[0.0028073282, 0.068083726, 0.5216703, 0.37219667, 0.035242062], shape=[5]}
```

This is the result of the recommendations.
