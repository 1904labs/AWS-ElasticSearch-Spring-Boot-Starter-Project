#AWS ElasticSearch

# Welcome
This `skeleton` project was made to help developers use Amazon's ElasticSearch through the use of their Java SDK. The project comes with complete working example API endpoints, and queries. Let this be your one stop solution to implementing ES logic for your Java needs.

# Getting Started
In order to fully see a working project, you will need an ES instance from AWS. Amazon offers a free tier of ES; which is enough to get started with this project. Create a free account here: https://aws.amazon.com/elasticsearch-service/

Ensure you have a good IDE to run the application. IntelliJ was used to create this project. Not required of course.

### Application Properties File
Once you have an account, download this project and locate the `application.properties` file within the project. Fill in the values for the fields below:
`(/src/main/resources)`
```
  aws.region=your_region_here // Example: us-east-1
  aws.endpoint=your_endpoint_here // Example: https://aws-es-instance-url.es.amazonaws.com/
```
### Credentials
In order to authenticate AWS ES calls, you must have a valid user account and provide the following keys:
 - `aws_access_key_id`
 - `aws_secret_access_key`

Create user accounts and groups through the `IAM` dashboard in AWS. Under the _Services_ menu, search and go to **IAM Dashboard** and create a _User_ and a _Group_. When you are done with this process, you will see your **keys**.

**Important Note: Your `aws_secret_access_key` is only shown to you ONCE! This happens while you creating a user through the IAM dashboard. Keep this key safe and secured. You will not be able to retrieve it again.**

There are 4 ways to provide these credentials to the project through its `DefaultAWSCredentialsProviderChain()` method. This is called automatically for you when you start your project. If no credentials are found, your build will fail and you will get an error message. Here are the 4 places you can place your credentials in:
1. Environment Variables
2. System properties
3. Profile Credentials Provider
4. EC2 Container Credentials Provider

**Profile Credentials Provider:** This is the simplest way to get started with the project. For `Mac` users, open a terminal window and do the following:
```
$> cd /Users/_your_profile_here_
$> mkdir .aws
$> cd .aws
$> touch credentials
```
Open the newly created `credentials` file with your favorite editor and paste the following:
```
[default]
aws_access_key_id=_your_key_id_here
aws_secret_access_key=_your_secret_key_here
```
Enter your key values, save and close. You're done. =)

**Notice: The _credentials_ file must NOT have a file extension.**

`Windows` users, place the `.aws/credentials` file in: `C:\Users\_your_user_profile`

## Configuration
Testing of this application was done with the latest version of ElasticSearch available on AWS (6.2), however this project should work with all versions at least 5.0 or greater.

When creating your instance of ElasticSearch in AWS, be sure to select `Public Access` in the `Network Configuration` section.

**Warning: Do NOT use `public access` for production level applications. This is only for testing, and practicing purposes!**

## Running Project
- Once you complete the steps above, you are ready to run your project. Choose your IDE wisely.

- Open up your favorite (ADE) API Development Environment like `Postman` and start hitting the endpoints =)
  Included in this build, is a `collection` of `Postman` API calls you can import. API instructions below.

---

# Basic Knowledge
Before we dive down into how to use the API, you must understand `4` simple concepts of ES: `Indexing`, `Documents`, `Mapping` and `Fields`. If you already have ES knowledge, then go ahead and skip ahead to `The APIs` section.

### 1. Indexing
Think of an index as a `table` in a database that ES can quickly search/reference. An ES instance allows you to have more than 1 type, and requires you to have at least 1 declared. In our movie database example, our index is `movies`. One good practice is to use the plural tense as good naming convention. You will see why soon.

### 2. Document
What we store and retrieve from an ES instance are `Documents`. Think of them as a record on a database (the `index`). A document is made up of an `index` that it belongs to, a `mapping type`, and a `body` field. More precisely, a document is a `JSON` type of the record, where the index represents its `type` and the `mapping` its data. Clear as mud? Don't worry, everything will make sense with our movie database example.

### 3. Mapping
This is how ES defines the data with given fields. It is the document's `data schema`. It tells ES how the data is structured. When creating a mapping, it needs an `index`, a `type`, and a `body` which is of JSON form. The `type` (as good convention) is the _single_ tense of its index. So in this case, it would be `movie`.

### 4. Fields
Fields are the individual _data points_ in the `Document`, and are defined by its `Type`. For example, the _title_ of a movie would be a field.

### Movie Database
This project revolves around a `Movie` database. We will create a searchable ES instance that gives us back records (movies). But first we must understand what makes our `index`, and populate it.

Here is the basic structure (Which is called the `Mapping`):
```
{
    "index": "movies",
    "doc_type": "movie",
    "body" = {
      "properties" {
        "title": {
          "type": "text",
          "fields": {
            "keyword": {
              "type": keyword,
              "ignore_above": 256
            }
          }
        },
        "year": {
          "type": "long"
        }
      }
    }
}
```
---
# The APIs
There are a total of 6 endpoints provided as examples. The core pattern is exemplified for each of the major functionalities that ES has to offer. We will be using a `movie` database pattern to test our ES APIs.

## Postman
Included in this project is a `Postman Collection.JSON` file that contains all 6 APIs that will be discussed. Go ahead and import this collection. Enjoy =)

### API 1: Creating an Index & Mapping with your first Document (POST)
Before we can interact with ES, we need data. By using an ADE, create a document using the `create` API using a `POST` call. Its `content-type` is `application/json`. The body of the call should have a JSON formatted object. Included in this project are 2 movies examples: `Black_Panther.json` and `Donnie_Darko.json`. Copy and paste these into the body and make the call.

**Request**: `http:localhost:8081/elastic-search/create`

**Response**: `Successfully created _movie-title_`. Where movie title will be the title value in your JSON body.

**Special Note: The index `type` has been hardcoded in ElasticSearchConstants.java: `MOVIE_INDEX = "movies"`**

Since your ES instance was empty at first, making this first call created 3 things:
  1. Created an `index` called `movies`.
  2. Created a `mapping` of type `movie`.
    - Mapping type is also hardcoded in ElasticSearchConstants.java: `MOVIES_DOCUMENT_TYPE = "movie"`.
  3. Created a `document` with the specified `index` and `mapping`.

Go ahead and insert more movies into your ES. This will help us test later on. =)

### API 2: Update an Document (PUT)
After we have data in our ElasticSearch Index, we can update it using the `update` API. For simplicity, the `update` API request body will fully replace the document in ElasticSearch.

**Request**: `http:localhost:8081/elastic-search/update`

**Response**: `Successfully updated _movie-title_`. Where movie title will be the title value in your JSON body.


### API 3: Search (POST)
The request body for the `search` API can contain any field of the `Movie` object that you wish to search on, along with the full or partial matching value. 

**Request**: `http:localhost:8081/elastic-search/search`

**Response**: The full ElasticSearch Response Body, including the Movie you searched for if it was found.


### API 4: FuzzySearch (POST)
The request body for the `fuzzySearch` API can contain any field of the `Movie` object that you wish to search on, along with a partial value. This endpoint is useful when searching incomplete words.

**Request**: `http:localhost:8081/elastic-search/fuzzySearch`

**Response**: The full ElasticSearch Response Body, including the Movie you searched for if it was found.


### API 5: Delete (DELETE)
The `delete` API does exactly what it implies. This will delete the document from ElasticSearch. In the Request, provide the Index, Document Type, and ID. These parameters tell ElasticSearch where to find the document you wish to delete.

**Request**: `http://localhost:8081/elastic-search/delete?index=movies&type=movie&id=1`

**Response**: `Successfully deleted movie with ID of _id_` Where the `id` is the ID of the document you specified in the request.


### API 6: Statistics (GET)
The `statistics` API provides in-depth statistics about your ElasticSearch index. For a complete description of what the response of this API returns, see this link to the [ElasticSearch documentation.](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-stats.html)

**Request**: `http://localhost:8081/elastic-search/statistics?index=movies`

**Response**: The Full ElasticSearch Response Body containing statistics of the requested index.
