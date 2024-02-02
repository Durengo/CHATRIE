const cassandra = require("cassandra-driver");

const client = new cassandra.Client({
  contactPoints: ["127.0.0.1"],
  localDataCenter: "datacenter1",
  keyspace: "testspace",
  socketOptions: {
    readTimeout: 30000,
  },
});

async function run() {
  try {
    // Connect to Cassandra
    await client.connect();

    const useKeyspaceQuery = "USE testspace";
    await client.execute(useKeyspaceQuery);

    // Create a table
    await client.execute(
      "CREATE TABLE IF NOT EXISTS testspace.users (id UUID PRIMARY KEY, name TEXT)"
    );

    // Insert data
    const insertQuery =
      "INSERT INTO testspace.users (id, name) VALUES (uuid(), ?)";
    await client.execute(insertQuery, ["Test User"]);

    // Query data
    const result = await client.execute(
      "SELECT id, name FROM testspace.users"
    );
    result.rows.forEach((row) => {
      console.log("User ID:", row.id, "Name:", row.name);
    });
  } catch (error) {
    console.error("Error:", error);
  } finally {
    // Close the connection
    await client.shutdown();
  }
}

run();
