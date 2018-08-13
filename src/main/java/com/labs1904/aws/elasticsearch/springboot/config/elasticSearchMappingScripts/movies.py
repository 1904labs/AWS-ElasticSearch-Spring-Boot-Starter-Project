from requests_aws4auth import AWS4Auth
from elasticsearch import Elasticsearch, RequestsHttpConnection


#AWS ElasticSearch Configuration

HOST = 'aws-es-instance-url.es.amazonaws.com'
AWS_ACCESS_KEY = 'AWS Access Key'
AWS_SECRET_KEY = 'AWS Secret Key'
REGION = 'us-east-2'
SERVICE = 'es'


# Initializing AWS Authentication used to sign our ElasticSearch requests

awsAuth = AWS4Auth(AWS_ACCESS_KEY, AWS_SECRET_KEY, REGION, SERVICE)


# Configuring AWS ElasticSearch connection

es = Elasticsearch(
    hosts = [{'host': HOST, 'port': 443}],
    http_auth = awsAuth,
    use_ssl = True,
    verify_certs = True,
    connection_class = RequestsHttpConnection
)


# Index information

theIndex = "movies"
theDocTypeMapping = "movie"


# Check if the Index exists, and delete it if so. ElasticSearch 6.x doesn't allow deleting of the document mapping by
# itself, so it is easiest to delete the whole index and recreate it.

if es.indices.exists(theIndex):
    es.indices.delete(index=theIndex)
    if not es.indices.exists(theIndex):
        print("Successfully deleted {} index".format(theIndex))


# Create the Index

es.indices.create(index=theIndex)


# Check that the Index was successfully created

if es.indices.exists(theIndex):
    print("Successfully created {} index".format(theIndex))


# Put the mapping for the ElasticSearch Index

print("Starting request to put Index Document Mapping")
es.indices.put_mapping(
    index = theIndex,
    doc_type = theDocTypeMapping,
    body = {
        "properties" : {
            "title" : {
                "type" : "text",
                "fields" : {
                    "keyword" : {
                        "type" : "keyword",
                        "ignore_above" : 256
                    }
                }
            },
            "year" : {
                "type" : "long"
            }
        }
    }
)
print("Finished request to put Index Document Mapping")


# Verify that the Index was created

if es.indices.exists(theIndex):
    print("Successfully created {} index, with document mapping {}".format(theIndex, theDocTypeMapping))