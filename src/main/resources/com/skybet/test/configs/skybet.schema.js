use skybet
db.createUser(
  {
    user: "user",
    pwd: "12345678C",
    roles: [
       { role: "readWrite", db: "skybet" }
    ]
  }
);
db.createCollection("com.skybet.test.beans.Event");
db.getCollection("com.skybet.test.beans.Event").createIndex({"eventId": 1});
db.createCollection("com.skybet.test.beans.Market");
db.getCollection("com.skybet.test.beans.Market").createIndex({"marketId": 1});
db.createCollection("com.skybet.test.beans.Outcome");
db.getCollection("com.skybet.test.beans.Outcome").createIndex({"outcomeId": 1});