#AWS ElasticSearch

TODO:
- [ ] Initial ES configuration overview of minimum setup
  - AWS Dashboard configuration
  - IAM user and group creation
  - Project level files needed to point and query AWS ES
- [ ] Explain how AWS key chain method works.
- [ ] Initial document creation
  - Creates index and mapping if no other existing mapping
- [ ] Building an AWS ES queries in Java
  - Updating
  - Deletion
  - Search
- [ ] Execute the Java made query to AWS ES
- [ ] Handling AWS ES responses
- [ ] Handling AWS ES errors
- [ ] Maven Dependencies
- [ ] Python
  - Libraries
  - Mapping and Indexing

# Welcome
This project was made to help developers use Amazon's ElasticSearch through the use of their Java SDK. This skeleton project comes complete with working example API endpoints, and queries. Let this be your last stop of searching the entire internet the combing through AWS documentation on how to use their SDK. Enjoy. =)

# Getting Started
In order to fully see a working project, you will need an ES instance from AWS. Amazon offers a free tier of ES, which is enough to get started with this project. Create a free account here: https://aws.amazon.com/elasticsearch-service/

Once you have an account, download this project and fill out the `application.properties` file with the appropriate values for these fields:
  ```
  aws.region=your_region_here // Example: us-east-1
  aws.endpoint=your_endpoint_here // Example: https://aws-es-instance-url.es.amazonaws.com/
```
### Credentials
In order to authenticate AWS ES calls, you must have a valid user account provide the following keys:
 - `aws_access_key_id`
 - `aws_secret_access_key`

Create user accounts and groups through the `IAM` dashboard in AWS. Under the _Services_ menu, search and go to **IAM Dashboard** and create a _User_ and a _Group_. When you are done with this process, you will see your **keys**.

**Important Note: Your `aws_secret_access_key` is only shown to you ONCE when you create a user through the IAM dashboard. Keep this key safe and secured. You will not be able to retrieve it again.**

AWS provides 4 ways to provide these credentials to the project through its `DefaultAWSCredentialsProviderChain()` method. This is called automatically for you when you start your project. If no credentials are found, your build will fail and you will get an error message. Here are the 4 places you can place your credentials in:
1. Environment Variables
2. System properties
3. Profile Credentials Provider
4. EC2 Container Credentials Provider



## Configuration
Testing of this application was done with the latest version of ElasticSearch available on AWS (6.2), however this project should work with all versions at least 5.0 or greater.

When creating your instance of ElasticSearch in AWS, be sure to select `Public Access` in the `Network Configuration` section.
